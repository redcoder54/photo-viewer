package redcoder.photoviewer.core;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.tbee.javafx.scene.layout.fxml.MigPane;
import redcoder.photoviewer.core.dialog.ConfirmDialog;
import redcoder.photoviewer.core.dialog.TaskProgressDialog;
import redcoder.photoviewer.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoViewerController {

    private static final Logger LOGGER = Logger.getLogger(PhotoViewerController.class.getName());

    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private SplitPane root;
    @FXML
    private ScrollPane previewPane;
    @FXML
    private ImageView imageView;

    private PhotoModel photoModel;
    private PreviewPhotoModel previewPhotoModel;

    @FXML
    private void initialize() {
        // init file chooser & directory chooser
        fileChooser.setTitle("选择照片");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        directoryChooser.setTitle("选择文件夹");

        photoModel = new PhotoModel(imageView);
        previewPhotoModel = new PreviewPhotoModel(previewPane, photoModel);

        // add some event-filter
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.UP) {
                previewPhotoModel.prev();
                event.consume();
            } else if (event.isControlDown() && event.getCode() == KeyCode.DOWN) {
                previewPhotoModel.next();
                event.consume();
            }
        });
        root.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown() && event.getEventType() == ScrollEvent.SCROLL) {
                if (event.getDeltaY() > 0) {
                    // zoom in
                    photoModel.zoomIn();
                } else if (event.getDeltaY() < 0) {
                    // zoom out
                    photoModel.zoomOut();
                }
                event.consume();
            }
        });
    }

    @FXML
    private void handleAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        Action action = (Action) button.getUserData();
        switch (action) {
            case CLEAR:
                previewPhotoModel.clear();
                photoModel.closePhoto();
                break;
            case OPEN_FILE:
                List<File> files = fileChooser.showOpenMultipleDialog(null);
                if (files != null) {
                    handleFils(files);
                }
                break;
            case OPEN_DIR:
                File file = directoryChooser.showDialog(null);
                if (file != null) {
                    handleFils(Collections.singletonList(file));
                }
                break;
            case LEFT_ROTATE:
                photoModel.rotateLeft();
                break;
            case RIGHT_ROTATE:
                photoModel.rotateRight();
                break;
            case ZOOM_IN:
                photoModel.zoomIn();
                break;
            case ZOOM_OUT:
                photoModel.zoomOut();
                break;
            case RESTORE:
                photoModel.restore();
                break;
            default:
                LOGGER.warning(() -> "Unknown action: " + action);
                break;
        }
    }

    private void handleFils(List<File> files) {
        TaskProgressDialog dialog = new TaskProgressDialog("图片加载中，请稍等~");
        PhotoLoadingTask task = new PhotoLoadingTask(files, dialog);
        task.setOnSucceeded(e -> {
            try {
                List<File> photoFiles = task.get();
                photoFiles.forEach(t -> previewPhotoModel.addPhotoFile(t));
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "error", e);
            }
        });
        startTask(task);
        dialog.show();
    }

    private void startTask(Task task) {
        Thread thread = new Thread(task, "LoadingTask[" + UUID.randomUUID() + "]");
        thread.setDaemon(true);
        thread.start();
    }

    private static class PhotoLoadingTask extends Task<List<File>> {

        private final List<File> files;
        private final TaskProgressDialog dialog;

        public PhotoLoadingTask(List<File> files, TaskProgressDialog dialog) {
            this.files = files;
            // configure dialog
            this.dialog = dialog;
            this.dialog.getProgressBar().progressProperty().bind(this.progressProperty());
            this.dialog.setOnCloseRequest(event -> {
                if (!isDone()) {
                    ConfirmDialog confirmDialog = new ConfirmDialog("Photo Viewer", "正在全力加载图片，确定终止吗？");
                    Optional<ConfirmDialog.State> state = confirmDialog.showAndWait();
                    if (!state.isPresent() || state.get() != ConfirmDialog.State.OK) {
                        event.consume();
                    }
                }
            });
        }

        @Override
        protected List<File> call() throws Exception {
            List<File> validFiles = new ArrayList<>();

            List<File> filesOnly = FileUtils.extractFileOnly(this.files);
            long max = filesOnly.size();
            long workDone = 0;
            for (File file : filesOnly) {
                try {
                    Image image = new Image(new FileInputStream(file));
                    Exception exception = image.getException();
                    if (exception != null) {
                        throw exception;
                    }
                    validFiles.add(file);
                    dialog.append("已加载图片: " + file.getName());
                } catch (Exception e) {
                    String message = String.format("Failed to load %s, it's really a image file?", file.getAbsolutePath());
                    LOGGER.log(Level.WARNING, message, e);
                } finally {
                    updateProgress(++workDone, max);
                }
            }

            return validFiles;
        }

        @Override
        protected void succeeded() {
            dialog.close();
        }

        @Override
        protected void failed() {
            LOGGER.log(Level.SEVERE, "Failed to load photo.", getException());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Photo Viewer");
            alert.setContentText("图片加载失败：" + getException().getMessage());
            alert.showAndWait();

            dialog.close();
        }
    }
}
