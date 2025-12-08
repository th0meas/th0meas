package musicplayer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox; // Import for the visualizer container
import javafx.util.Duration; // Import for Duration

import java.io.File;
import java.util.ArrayList; // Fixes "cannot find symbol ArrayList"

public class MusicController {

    // --- FXML UI COMPONENTS ---
    @FXML private Button openButton;
    @FXML private Button playButton;
    @FXML private Button pauseButton; // Fixes "cannot find symbol pauseButton"
    @FXML private Button stopButton;  // Fixes "cannot find symbol stopButton"
    @FXML private Button nextButton;
    @FXML private Button prevButton;

    @FXML private Slider volumeSlider;
    @FXML private Label songLabel;

    //EQUALISER
    @FXML private Slider slider1;
    @FXML private Slider slider2;
    @FXML private Slider slider3;
    @FXML private Slider slider4;
    @FXML private Slider slider5;
    @FXML private Slider slider6;

    // The TreeView holds "File" objects now
    @FXML private TreeView<File> fileTree;

    // The Visualizer Container
    @FXML private VBox visualizerContainer;

    // --- LOGIC VARIABLES ---
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs = new ArrayList<>(); // The Playlist
    private int currentSongIndex = 0;

    @FXML
    public void initialize() {

        // 1. SETUP THE TREE VIEW (Show Names, not full paths)
        fileTree.setCellFactory(tv -> new TreeCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // 2. OPEN FOLDER ACTION
        openButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Music Folder");
            Stage stage = (Stage) openButton.getScene().getWindow();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if (selectedDirectory != null) {
                songs.clear(); // Clear old playlist
                TreeItem<File> rootItem = new TreeItem<>(selectedDirectory);
                findFiles(selectedDirectory, rootItem);

                fileTree.setRoot(rootItem);
                rootItem.setExpanded(true);
            }
        });

        // 3. TREE SELECTION (Play on Click)
        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                File selectedFile = newVal.getValue();
                currentSongIndex = songs.indexOf(selectedFile); // Sync playlist index
                playFile(selectedFile);
            }
        });

        // 4. BUTTON ACTIONS
        // Play/Pause Toggle
        if (playButton != null) {
            playButton.setOnAction(e -> {
                if (mediaPlayer == null) return;

                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    playButton.setText("▶"); // Optional: Change icon
                } else {
                    mediaPlayer.play();
                    playButton.setText("||");
                }
            });
        }

        // Stop
        if (stopButton != null) {
            stopButton.setOnAction(e -> {
                if (mediaPlayer != null) mediaPlayer.stop();
            });
        }

        // Next
        if (nextButton != null) {
            nextButton.setOnAction(e -> playNextSong());
        }

        // Previous
        if (prevButton != null) {
            prevButton.setOnAction(e -> {
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    playFile(songs.get(currentSongIndex));
                } else {
                    if (mediaPlayer != null) mediaPlayer.seek(Duration.ZERO);
                }
            });
        }
    }

    // --- HELPER METHODS ---

    private void playNextSong() {
        if (currentSongIndex < songs.size() - 1) {
            currentSongIndex++;
            playFile(songs.get(currentSongIndex));
        } else {
            // Loop back to start (Optional)
            currentSongIndex = 0;
            playFile(songs.get(currentSongIndex));
        }
    }

    private void playFile(File file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Inside playFile()...
            mediaPlayer = new MediaPlayer(media);

// ... existing volume code ...

            setupEqualizer(); // <--- ADD THIS LINE HERE

// ... startVisualizer() and play() ...

            if (songLabel != null) songLabel.setText(file.getName());

            // Volume Control
            if (volumeSlider != null) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                volumeSlider.valueProperty().addListener((obs, old, val) ->
                        mediaPlayer.setVolume(val.doubleValue() / 100.0)
                );
            }

            // Auto-Next Logic
            mediaPlayer.setOnEndOfMedia(this::playNextSong);

            startVisualizer(); // Start the bars!
            mediaPlayer.play();

        } catch (Exception e) {
            System.out.println("Error playing file: " + e.getMessage());
        }
    }

    private void startVisualizer() {
        if (mediaPlayer == null || visualizerContainer == null) return;

        // 1. Smoother Motion (60 FPS)
        mediaPlayer.setAudioSpectrumInterval(0.016);

        // 2. More Sensitivity (Pick up quiet treble)
        mediaPlayer.setAudioSpectrumThreshold(-80);

        int barCount = visualizerContainer.getChildren().size();
        mediaPlayer.setAudioSpectrumNumBands(barCount);

        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {

            for (int i = 0; i < barCount; i++) {
                if (visualizerContainer.getChildren().get(i) instanceof ProgressBar) {
                    ProgressBar bar = (ProgressBar) visualizerContainer.getChildren().get(i);

                    // 3. Basic Calculation (-80dB to 0dB scale)
                    float rawValue = magnitudes[i];
                    double adjustedValue = (rawValue + 80) / 80.0; // 0.0 to 1.0

                    // 4. THE TREBLE BOOST (The Cheat Code)
                    // We increase the multiplier as 'i' gets bigger.
                    // Bass (i=0) gets 1.0x boost (No change)
                    // Treble (i=15) gets ~2.5x boost
                    double multiplier = 1.0 + (i * 0.15);

                    double finalValue = adjustedValue * multiplier;

                    // Clamp to ensure we don't go over 100%
                    bar.setProgress(Math.max(0, Math.min(1, finalValue)));
                }
            }
        });
    }

    private void findFiles(File directory, TreeItem<File> parentItem) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    TreeItem<File> folderItem = new TreeItem<>(file);
                    parentItem.getChildren().add(folderItem);
                    findFiles(file, folderItem);
                } else {
                    if (file.getName().toLowerCase().endsWith(".mp3")) {
                        TreeItem<File> fileItem = new TreeItem<>(file);
                        parentItem.getChildren().add(fileItem);
                        songs.add(file); // Add to playlist
                    }
                }
            }
        }
    }

    private void setupEqualizer() {
        if (mediaPlayer == null) return;

        // Enable the Equalizer
        mediaPlayer.getAudioEqualizer().setEnabled(true);

        // JavaFX has 10 default bands: 32Hz, 63Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz
        // We will map your 6 sliders to the most relevant bands to cover Bass -> Treble.

        // Helper list to make loop easier
        Slider[] sliders = {slider1, slider2, slider3, slider4, slider5, slider6};

        // Map Slider Index -> Equalizer Band Index
        // Slider 0 (Bass) -> Band 0 (32Hz)
        // Slider 1 -> Band 2 (125Hz)
        // Slider 2 -> Band 4 (500Hz)
        // Slider 3 -> Band 5 (1kHz)
        // Slider 4 -> Band 7 (4kHz)
        // Slider 5 (Treble) -> Band 9 (16kHz)
        int[] bandIndices = {1, 2, 4, 5, 7, 9};

        for (int i = 0; i < sliders.length; i++) {
            if (sliders[i] != null) {
                // Get the specific frequency band
                javafx.scene.media.EqualizerBand band = mediaPlayer.getAudioEqualizer().getBands().get(bandIndices[i]);

                // Set slider range (dB is usually -12 to +12)
                sliders[i].setMin(javafx.scene.media.EqualizerBand.MIN_GAIN); // usually -24.0
                sliders[i].setMax(javafx.scene.media.EqualizerBand.MAX_GAIN); // usually +12.0
                sliders[i].setValue(0); // Reset to center

                // Connect slider movement to band gain
                sliders[i].valueProperty().addListener((obs, oldVal, newVal) -> {
                    band.setGain(newVal.doubleValue());
                });
            }
        }
    }
}