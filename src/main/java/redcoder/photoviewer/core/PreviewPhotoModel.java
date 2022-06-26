package redcoder.photoviewer.core;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
    private static final File thumbnailsDir = new File(RcFileSupport.getParentDir(), "thumbnails");
    private static final Border border = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private MigPane previewPane;
    private PhotoModel photoModel;
    private NavigableLabel tailPointer;
    private NavigableLabel selectedLabel;

    public PreviewPhotoModel(MigPane previewPane, PhotoModel photoModel) {
        this.previewPane = previewPane;
        this.photoModel = photoModel;

        if (!thumbnailsDir.exists()) {
            thumbnailsDir.mkdirs();
        }
    }

    public void addPhotoFile(File photoFile) {
        ImageView thumbnail = createThumbnail(photoFile);
        if (thumbnail == null) {
            return;
        }

        NavigableLabel label = new NavigableLabel(photoFile);
        label.setAlignment(Pos.CENTER);
        label.setGraphic(thumbnail);
        label.setOnMouseClicked(event -> selectLabel(label));
        previewPane.add(label, "growx, center");

        if (tailPointer != null) {
            tailPointer.next = label;
            label.prev = tailPointer;
        }
        tailPointer = label;
    }

    public void prev() {
        selectLabel(selectedLabel.prev);
    }

    public void next() {
        selectLabel(selectedLabel.next);
    }

    private ImageView createThumbnail(File photoFile) {
        try {
            File destFile = Thumbnails.of(photoFile).height(100).width(100).asFiles(thumbnailsDir, new Rename() {
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

    public void selectLabel(NavigableLabel label) {
        if (label == null) {
            return;
        }

        photoModel.openPhoto(label.photoFile);
        if (selectedLabel != null) {
            selectedLabel.setBorder(Border.EMPTY);
        }
        selectedLabel = label;
        selectedLabel.setBorder(border);
    }

    private static class NavigableLabel extends Label {
        NavigableLabel prev;
        NavigableLabel next;
        File photoFile;

        public NavigableLabel(File photoFile) {
            this.photoFile = photoFile;
        }
    }
}
