package musicplayer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class MusicController {

    // --- FXML UI COMPONENTS ---
    @FXML private Button openButton;
    @FXML private Button playButton;
    @FXML private Button stopButton;
    @FXML private Button nextButton;
    @FXML private Button prevButton;
    @FXML private SVGPath playIcon;

    @FXML private Slider volumeSlider;
    @FXML private Label songLabel;

    // NEW: The Progress/Seek Slider
    @FXML private Slider progressSlider;

    @FXML private TreeView<File> fileTree;
    @FXML private HBox visualizerContainer;

    @FXML private Slider slider1, slider2, slider3, slider4, slider5, slider6;

    // --- LOGIC VARIABLES ---
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs = new ArrayList<>();
    private int currentSongIndex = 0;
    private double[] fallDownMultipliers;

    // For the Marquee (Scrolling Text)
    private Timeline marqueeTimeline;

    //for green background when skipping fix
    private boolean isProgrammaticSelection = false;

    @FXML
    public void initialize() {

        // 1. SETUP TREE VIEW
        fileTree.setCellFactory(tv -> new TreeCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        // 2. SETUP PROGRESS SLIDER (Seek functionality)
        if (progressSlider != null) {
            // When user drags the slider, seek to that position
            progressSlider.setOnMousePressed(e -> {
                if (mediaPlayer != null) mediaPlayer.pause();
            });
            progressSlider.setOnMouseReleased(e -> {
                if (mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
                    mediaPlayer.play();
                }
            });
        }

        setupButtonActions();
    }

    private void setupButtonActions() {
        // Open Folder
        if (openButton != null) {
            openButton.setOnAction(event -> {
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Select Music Folder");
                File dir = dc.showDialog(openButton.getScene().getWindow());
                if (dir != null) {
                    songs.clear();
                    TreeItem<File> root = new TreeItem<>(dir);
                    findFiles(dir, root);
                    fileTree.setRoot(root);
                    root.setExpanded(true);
                }
            });
        }

        // Tree Click
        // Inside initialize()...

        if (fileTree != null) {
            fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {

                // --- NEW LINE: STOP if the computer selected it ---
                if (isProgrammaticSelection) return;

                if (newVal != null && newVal.isLeaf()) {
                    File selectedFile = newVal.getValue();
                    currentSongIndex = songs.indexOf(selectedFile);
                    playFile(selectedFile);
                }
            });
        }

        if (playButton != null) {
            playButton.setOnAction(e -> {
                if (mediaPlayer == null) return;

                String playShape = "M8 5v14l11-7z";
                String pauseShape = "M6 19h4V5H6v14zm8-14v14h4V5h-4z";

                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    if (playIcon != null) playIcon.setContent(playShape);

                    // --- NEW: Drop the bars immediately ---
                    resetVisualizerBars();

                } else {
                    mediaPlayer.play();
                    if (playIcon != null) playIcon.setContent(pauseShape);
                }
            });
        }

        // Stop Button
        if (stopButton != null) {
            stopButton.setOnAction(e -> {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    // --- NEW: Drop the bars immediately ---
                    resetVisualizerBars();
                }
            });
        }

        // Next/Prev
        if (nextButton != null) nextButton.setOnAction(e -> playNextSong());
        if (prevButton != null) prevButton.setOnAction(e -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                playFile(songs.get(currentSongIndex));
            }
        });
    }

    private void playNextSong() {
        if (currentSongIndex < songs.size() - 1) {
            currentSongIndex++;
            playFile(songs.get(currentSongIndex));
        } else {
            currentSongIndex = 0; // Loop
            playFile(songs.get(currentSongIndex));
        }
    }

    private void startMarquee(String text) {
        if (marqueeTimeline != null) marqueeTimeline.stop();

        // If text fits, just show it without scrolling
        // (Increased threshold slightly so short titles don't jitter)
        if (text.length() < 30) {
            songLabel.setText(text);
            return;
        }

        final StringBuilder sb = new StringBuilder(text);

        // CHANGE THIS LINE: Increased from 200 to 400 for slower, smoother shifts
        marqueeTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            String current = sb.toString();
            String moved = current.substring(1) + current.charAt(0);
            sb.replace(0, sb.length(), moved);
            songLabel.setText(moved);
        }));
        marqueeTimeline.setCycleCount(Animation.INDEFINITE);
        marqueeTimeline.play();
    }

    private void playFile(File file) {
        // 1. Clean up the previous song
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            // 2. Load the new song
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // 3. SYNC THE TREEVIEW (Highlight the song in the list)
            updateTreeSelection(file);

            // 4. Start the Scrolling Text
            startMarquee(file.getName());

            // 5. Volume Control
            if (volumeSlider != null) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                volumeSlider.valueProperty().addListener((o, old, val) ->
                        mediaPlayer.setVolume(val.doubleValue() / 100.0));
            }

            // 6. Progress Bar / Seek Logic
            if (progressSlider != null) {
                // Update slider as song plays
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!progressSlider.isValueChanging()) {
                        progressSlider.setValue(newTime.toSeconds());
                    }
                });

                // Set max value when metadata is ready
                mediaPlayer.setOnReady(() -> {
                    progressSlider.setMin(0);
                    progressSlider.setMax(media.getDuration().toSeconds());
                });
            }

            // 7. Auto-Next when song finishes
            mediaPlayer.setOnEndOfMedia(this::playNextSong);

            // 8. Restart Visuals & EQ
            setupEqualizer();
            startVisualizer();

            // 9. Play and update Icon
            mediaPlayer.play();

            // Set Pause Icon (since it's now playing)
            if (playIcon != null) {
                playIcon.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");
            }

        } catch (Exception e) {
            System.out.println("Error playing file: " + e.getMessage());
        }
    }

    // --- COPY YOUR EXISTING 'setupEqualizer' AND 'startVisualizer' AND 'findFiles' METHODS HERE ---
    // (I omitted them to save space, but DO NOT DELETE THEM from your file!)
    // Just paste your previous EQ/Visualizer methods back in at the bottom.

    // --- EQUALIZER ---
    private void setupEqualizer() {
        if (mediaPlayer == null) return;
        mediaPlayer.getAudioEqualizer().setEnabled(true);
        Slider[] sliders = {slider1, slider2, slider3, slider4, slider5, slider6};
        int[] bandIndices = {0, 2, 4, 5, 7, 9};
        for (int i = 0; i < sliders.length; i++) {
            if (sliders[i] != null) {
                EqualizerBand band = mediaPlayer.getAudioEqualizer().getBands().get(bandIndices[i]);
                sliders[i].setMin(EqualizerBand.MIN_GAIN);
                sliders[i].setMax(EqualizerBand.MAX_GAIN);
                sliders[i].setValue(0);
                sliders[i].valueProperty().addListener((obs, oldVal, newVal) -> band.setGain(newVal.doubleValue()));
            }
        }
    }

    // --- VISUALIZER ---
    private void startVisualizer() {
        if (mediaPlayer == null || visualizerContainer == null) return;
        mediaPlayer.setAudioSpectrumInterval(0.016);
        mediaPlayer.setAudioSpectrumThreshold(-90);
        int barCount = visualizerContainer.getChildren().size();
        mediaPlayer.setAudioSpectrumNumBands(barCount);
        if (fallDownMultipliers == null || fallDownMultipliers.length != barCount) fallDownMultipliers = new double[barCount];

        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            for (int i = 0; i < barCount; i++) {
                if (visualizerContainer.getChildren().get(i) instanceof Rectangle) {
                    Rectangle bar = (Rectangle) visualizerContainer.getChildren().get(i);
                    float rawDB = magnitudes[i];
                    double normalized = (rawDB + 90) / 90.0;
                    if (i < 5) {
                        if (normalized < 0.2) normalized = 0;
                    } else {
                        if (normalized < 0.05) normalized = 0;
                        else normalized *= (1.2 + ((i - 5) * 0.15));
                    }
                    normalized = Math.max(0, Math.min(1, normalized));
                    double decaySpeed = 0.02;
                    if (normalized > fallDownMultipliers[i]) fallDownMultipliers[i] = normalized;
                    else fallDownMultipliers[i] = Math.max(0, fallDownMultipliers[i] - decaySpeed);
                    double vol = volumeSlider.getValue() / 100.0;
                    bar.setHeight(fallDownMultipliers[i] * 500.0 * vol);
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
                        songs.add(file);
                    }
                }
            }
        }
    }

    // Recursive helper to find and select a file in the tree
    private void updateTreeSelection(File file) {
        if (fileTree.getRoot() == null) return;

        isProgrammaticSelection = true; // LOCK (Don't trigger playFile again)

        findAndSelect(fileTree.getRoot(), file);

        isProgrammaticSelection = false; // UNLOCK
    }

    // Recursively walks the tree to find the matching item
    private boolean findAndSelect(TreeItem<File> item, File target) {
        if (item.getValue().equals(target)) {
            fileTree.getSelectionModel().select(item);
            fileTree.scrollTo(fileTree.getRow(item)); // Auto-scroll to show it!
            return true;
        }

        for (TreeItem<File> child : item.getChildren()) {
            if (findAndSelect(child, target)) return true;
        }
        return false;
    }
    private void resetVisualizerBars() {
        if (visualizerContainer == null) return;

        // 1. Force all bars to Height 0
        for (javafx.scene.Node node : visualizerContainer.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setHeight(0);
            }
        }

        // 2. Reset the Gravity Memory (so they don't ghost back up when you resume)
        if (fallDownMultipliers != null) {
            java.util.Arrays.fill(fallDownMultipliers, 0.0);
        }
    }
}

