package redcoder.photoviewer.core;

import javafx.application.Platform;
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
import redcoder.photoviewer.utils.RcFileSupport;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreviewPhotoModel {

    private static final Logger LOGGER = Logger.getLogger(PreviewPhotoModel.class.getName());

    private static final Border border = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, BorderWidths.DEFAULT));

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
        ImageView thumbnail = createThumbnail(photoFile);
        if (thumbnail == null) {
            return;
        }

        NavigablePreviewItem label = new NavigablePreviewItem(photoFile);
        label.setAlignment(Pos.CENTER);
        label.setGraphic(thumbnail);
        label.setOnMouseClicked(event -> chooseItem(label));
        content.add(label, "growx, center");

        if (tail != null) {
            label.prev = tail;
            tail.next = label;
            tail = label;
        } else {
            // open first photo
            tail = label;
            chooseItem(label);
            photoModel.openPhoto(photoFile);
        }

        Platform.runLater(() -> {
            double vmax = previewPane.getVmax() + label.getHeight();
            previewPane.setVmax(vmax);
        });
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

    private void scrollIfNecessary(boolean down) {
        double y = selectedItem.getLayoutY();
        double height = selectedItem.getHeight();
        double vvalue = previewPane.getVvalue();
        if (down) {
            if (vvalue + height < y) {
                previewPane.setVvalue(y + height);
            }
        } else {
            if (vvalue > y) {
                previewPane.setVvalue(y);
            }
        }
    }

    private ImageView createThumbnail(File photoFile) {
        try {
            File destFile = Thumbnails.of(photoFile).height(100).width(100).asFiles(TmpFileManager.getThumbnailsDir(), new Rename() {
                @Override
                public String apply(String name, ThumbnailParameter param) {
                    return UUID.randomUUID().toString();
                }
            }).get(0);
            return new ImageView(new Image(new FileInputStream(destFile)));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create thumbnail.", e);
            return null;
        }
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

    private static class NavigablePreviewItem extends Label {
        NavigablePreviewItem prev;
        NavigablePreviewItem next;
        File photoFile;

        public NavigablePreviewItem(File photoFile) {
            this.photoFile = photoFile;
        }
    }
}
