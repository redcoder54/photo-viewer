package redcoder.photoviewer.core;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import org.tbee.javafx.scene.layout.fxml.MigPane;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class PhotoViewerController {

    private static final Logger LOGGER = Logger.getLogger(PhotoViewerController.class.getName());

    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private SplitPane root;
    @FXML
    private MigPane previewPane;
    @FXML
    private ImageView imageView;

    private PhotoModel photoModel;
    private PreviewPhotoModel previewPhotoModel;

    @FXML
    private void initialize() {
        // init file chooser
        fileChooser.setTitle("选择照片");

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
                previewPane.getChildren().clear();
                photoModel.closePhoto();
                break;
            case OPEN:
                List<File> files = fileChooser.showOpenMultipleDialog(null);
                if (files != null) {
                    for (File file : files) {
                        previewPhotoModel.addPhotoFile(file);
                    }
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
}
