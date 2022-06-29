package redcoder.photoviewer.core;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.tbee.javafx.scene.layout.fxml.MigPane;
import redcoder.photoviewer.utils.TaskExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreviewPhotoModel {

    private static final Logger LOGGER = Logger.getLogger(PreviewPhotoModel.class.getName());
    private static final Border border = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, BorderWidths.DEFAULT));
    private static final Image loadImage = new Image(PreviewPhotoModel.class.getClassLoader().getResourceAsStream("images/load.gif"));
    private static final int THUMB_WIDTH = 100;
    private static final int THUMB_HEIGHT = 100;

    private final ScrollPane previewPane;
    private final MigPane content;
    private final PhotoModel photoModel;
    private NavigablePreviewItem tail;
    private NavigablePreviewItem selectedItem;

    public PreviewPhotoModel(ScrollPane previewPane, PhotoModel photoModel) {
        this.previewPane = previewPane;
        this.photoModel = photoModel;
        this.content = (MigPane) previewPane.getContent();
    }

    public void addPhotoFile(File photoFile) {
        NavigablePreviewItem item = new NavigablePreviewItem(photoFile);
        item.setAlignment(Pos.CENTER);
        item.setGraphic(new ImageView(loadImage));
        item.setOnMouseClicked(event -> chooseItem(item));
        content.add(item, "growx, center");

        if (tail != null) {
            item.prev = tail;
            tail.next = item;
            tail = item;
        } else {
            // open first photo
            tail = item;
            chooseItem(item);
            photoModel.openPhoto(photoFile);
        }

        // create thumbnails asynchronously
        ThumbnailsTask task = new ThumbnailsTask(photoFile);
        task.setOnSucceeded(event -> {
            try {
                item.setGraphic(new ImageView(task.get()));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to create thumbnails", e);
                removeItem(item);
            }
        });
        task.setOnFailed(event -> {
            LOGGER.log(Level.WARNING, "Failed to create thumbnails", task.getException());
            removeItem(item);
        });
        TaskExecutor.execute(task);

        // update vmax for scrolling dynamically
        Platform.runLater(() -> {
            double vmax = previewPane.getVmax() + item.getHeight();
            previewPane.setVmax(vmax);
        });
    }

    private void removeItem(NavigablePreviewItem item) {
        item.prev.next = item.next;
        content.remove(item);
    }

    public void clear() {
        tail = null;
        selectedItem = null;
        content.getChildren().clear();
        previewPane.setVmin(0);
        previewPane.setVmax(1);
    }

    public void prev() {
        if (selectedItem == null) {
            return;
        }
        chooseItem(selectedItem.prev);
        scrollIfNecessary(false);
    }

    public void next() {
        if (selectedItem == null) {
            return;
        }
        chooseItem(selectedItem.next);
        scrollIfNecessary(true);
    }

    private void chooseItem(NavigablePreviewItem item) {
        if (item == null) {
            return;
        }

        photoModel.openPhoto(item.photoFile);
        if (selectedItem != null) {
            selectedItem.setBorder(Border.EMPTY);
        }
        selectedItem = item;
        selectedItem.setBorder(border);
    }

    private void scrollIfNecessary(boolean down) {
        double change = selectedItem.getHeight() + 50;
        double vvalue = previewPane.getVvalue();
        if (down) {
            previewPane.setVvalue(vvalue + change);
        } else {
            previewPane.setVvalue(vvalue - change);
        }
    }

    private static class NavigablePreviewItem extends Label {
        NavigablePreviewItem prev;
        NavigablePreviewItem next;
        File photoFile;

        public NavigablePreviewItem(File photoFile) {
            this.photoFile = photoFile;
        }
    }

    private static class ThumbnailsTask extends Task<Image> {

        private final File photoFile;

        public ThumbnailsTask(File photoFile) {
            this.photoFile = photoFile;
        }

        @Override
        protected Image call() throws Exception {
            return createThumbnail(photoFile);
        }

        private Image createThumbnail(File photoFile) {
            try {
                File destFile = Thumbnails.of(photoFile)
                        .height(THUMB_HEIGHT)
                        .width(THUMB_WIDTH)
                        .asFiles(TmpFileManager.getThumbnailsDir(), new Rename() {
                            @Override
                            public String apply(String name, ThumbnailParameter param) {
                                return UUID.randomUUID().toString();
                            }
                        }).get(0);
                return new Image(new FileInputStream(destFile));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create thumbnail.", e);
                return null;
            }
        }
    }
}
