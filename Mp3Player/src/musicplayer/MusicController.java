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
import java.util.Arrays;

public class MusicController {

    // --- FXML UI COMPONENTS ---
    @FXML private Button openButton;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Button stopButton;
    @FXML private Button nextButton;
    @FXML private Button prevButton;
    @FXML private SVGPath playIcon;

    @FXML private Slider volumeSlider;
    @FXML private Label songLabel;
    @FXML private Slider progressSlider;

    @FXML private TreeView<File> fileTree;
    @FXML private HBox visualizerContainer;

    @FXML private Slider slider1;
    @FXML private Slider slider2;
    @FXML private Slider slider3;
    @FXML private Slider slider4;
    @FXML private Slider slider5;
    @FXML private Slider slider6;

    //time progress bar
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;


    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs = new ArrayList<>();
    private int currentSongIndex = 0;
    private double[] fallDownMultipliers;
    private Timeline marqueeTimeline;
    private boolean isProgrammaticSelection = false;

    @FXML
    public void initialize() {

        //song label not showing if song not selected
        if (songLabel != null) songLabel.setText("");

        // 1. Setup TreeView with strictly typed File objects
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

        setupButtonActions();

        // 2. Setup Treeview
        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (isProgrammaticSelection) return;
            if (newVal != null && newVal.isLeaf()) {
                File selectedFile = newVal.getValue();
                currentSongIndex = songs.indexOf(selectedFile);
                playFile(selectedFile);
            }
        });

        // 3. Setup prog bar
        if (progressSlider != null) {
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
    }

    private void setupButtonActions() {
        if (openButton != null) {
            openButton.setOnAction(event -> {
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Select Music Folder");
                Stage stage = (Stage) openButton.getScene().getWindow();
                File dir = dc.showDialog(stage);
                if (dir != null) {
                    songs.clear();
                    TreeItem<File> root = new TreeItem<>(dir);
                    findFiles(dir, root);
                    fileTree.setRoot(root);
                    root.setExpanded(true);
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
                    resetVisualizerBars();
                } else {
                    mediaPlayer.play();
                    if (playIcon != null) playIcon.setContent(pauseShape);
                }
            });
        }

        if (stopButton != null) {
            stopButton.setOnAction(e -> {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    resetVisualizerBars();
                }
            });
        }

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
            updateTreeSelection(file);
            startMarquee(file.getName());

            if (volumeSlider != null) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 50.0);
                volumeSlider.valueProperty().addListener((o, old, val) ->
                        mediaPlayer.setVolume(val.doubleValue() / 50.0));
            }
            // --- PROGRESS BAR & TIMESTAMPS ---
            if (progressSlider != null) {

                // 1. Update Current Time as song plays
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    // Update the slider position
                    if (!progressSlider.isValueChanging()) {
                        progressSlider.setValue(newTime.toSeconds());
                    }
                    // Update the "00:00" text on the left
                    if (currentTimeLabel != null) {
                        currentTimeLabel.setText(formatTime(newTime.toSeconds()));
                    }
                });

                // 2. Set Total Time when song is ready
                mediaPlayer.setOnReady(() -> {
                    double totalDuration = media.getDuration().toSeconds();

                    // Set slider max
                    progressSlider.setMin(0);
                    progressSlider.setMax(totalDuration);

                    // Set the "03:45" text on the right
                    if (totalTimeLabel != null) {
                        totalTimeLabel.setText(formatTime(totalDuration));
                    }
                });
            }
            mediaPlayer.setOnEndOfMedia(this::playNextSong);
            setupEqualizer();
            startVisualizer();
            mediaPlayer.play();
            if (playIcon != null) playIcon.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

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

    private void startVisualizer() {
        if (mediaPlayer == null || visualizerContainer == null) return;

        mediaPlayer.setAudioSpectrumInterval(0.016); // 60 FPS
        mediaPlayer.setAudioSpectrumThreshold(-80);  // Sensitivity floor

        int visualBarCount = visualizerContainer.getChildren().size(); // 15 bars


        mediaPlayer.setAudioSpectrumNumBands(visualBarCount * 3);

        if (fallDownMultipliers == null || fallDownMultipliers.length != visualBarCount) {
            fallDownMultipliers = new double[visualBarCount];
        }

        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {

            // Loop only through the visible bars (0 to 14)
            for (int i = 0; i < visualBarCount; i++) {
                if (visualizerContainer.getChildren().get(i) instanceof Rectangle) {
                    Rectangle bar = (Rectangle) visualizerContainer.getChildren().get(i);

                    // We are reading from the start of the array, so we get the Bass/Mids automatically
                    float rawDB = magnitudes[i];

                    // 1. BASE MATH (-80dB floor)
                    double normalized = (rawDB + 80) / 80.0;

                    // 2. GENTLE TREBLE BOOST (Since we zoomed in, we don't need a crazy 8x boost anymore)
                    // Just a small curve to help the right-side bars
                    double boost = 1.0 + (i * 0.1);
                    normalized *= boost;

                    // 3. NOISE GATE (Clean up silence)
                    if (normalized < 0.1) normalized = 0;

                    // 4. GRAVITY (Fall down logic)
                    double currentHeightPercent = fallDownMultipliers[i];
                    double decaySpeed = 0.02;

                    if (normalized > currentHeightPercent) {
                        fallDownMultipliers[i] = normalized;
                    } else {
                        fallDownMultipliers[i] = Math.max(0, currentHeightPercent - decaySpeed);
                    }

                    // 5. APPLY VOLUME & HEIGHT
                    // Using 600.0 to ensure they hit the top of your taller UI
                    double vol = (volumeSlider != null) ? volumeSlider.getValue() / 100.0 : 1.0;

                    // Allow bars to go slightly over 100% for impact, but clamp at max height
                    double finalHeight = fallDownMultipliers[i] * 500.0 * vol;
                    bar.setHeight(Math.min(500.0, finalHeight));
                }
            }
        });
    }

    private void resetVisualizerBars() {
        if (visualizerContainer == null) return;
        for (javafx.scene.Node node : visualizerContainer.getChildren()) {
            if (node instanceof Rectangle) ((Rectangle) node).setHeight(0);
        }
        if (fallDownMultipliers != null) Arrays.fill(fallDownMultipliers, 0.0);
    }

    private void startMarquee(String text) {
        if (marqueeTimeline != null) marqueeTimeline.stop();
        if (text.length() < 30) {
            songLabel.setText(text);
            return;
        }
        final StringBuilder sb = new StringBuilder(text + "   *** ");
        marqueeTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            String current = sb.toString();
            String moved = current.substring(1) + current.charAt(0);
            sb.replace(0, sb.length(), moved);
            songLabel.setText(moved);
        }));
        marqueeTimeline.setCycleCount(Animation.INDEFINITE);
        marqueeTimeline.play();
    }

    private void updateTreeSelection(File file) {
        if (fileTree == null || fileTree.getRoot() == null) return;
        isProgrammaticSelection = true;
        findAndSelect(fileTree.getRoot(), file);
        isProgrammaticSelection = false;
    }

    private boolean findAndSelect(TreeItem<File> item, File target) {
        if (item.getValue().equals(target)) {
            fileTree.getSelectionModel().select(item);
            fileTree.scrollTo(fileTree.getRow(item));
            return true;
        }
        for (TreeItem<File> child : item.getChildren()) {
            if (findAndSelect(child, target)) return true;
        }
        return false;
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

    private String formatTime(double seconds) {
        int totalSeconds = (int) seconds;
        int minutes = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}