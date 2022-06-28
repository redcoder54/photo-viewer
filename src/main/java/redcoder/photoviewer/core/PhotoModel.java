package redcoder.photoviewer.core;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import redcoder.photoviewer.utils.RcFileSupport;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoModel {

    private static final Logger LOGGER = Logger.getLogger(PhotoModel.class.getName());
    private static final double SCALE_UNIT = 0.1;
    private static final double SCALE_MINIMUM = 0.1;
    private static final double SCALE_MAXIMUM = 10;


    private File sourcePhotoFile;
    private File operationTarget;
    private double rotate = 0;
    private double scale = 1.0;

    private final ImageView imageView;

    public PhotoModel(ImageView imageView) {
        this.imageView = imageView;
    }

    public void rotateLeft() {
        rotate -= 90;
        if (rotate <= -360) {
            rotate = 0;
        }
        imageView.setRotate(rotate);
    }

    public void rotateRight() {
        rotate += 90;
        if (rotate >= 360) {
            rotate = 0;
        }
        imageView.setRotate(rotate);
    }

    public void zoomIn() {
        if (scale == SCALE_MAXIMUM) {
            return;
        }
        scale += SCALE_UNIT;
        scale = Math.min(scale, SCALE_MAXIMUM);
        try {
            List<File> files = Thumbnails.of(sourcePhotoFile).scale(scale).asFiles(TmpFileManager.getTmpDir(), new Rename() {
                @Override
                public String apply(String name, ThumbnailParameter param) {
                    return UUID.randomUUID().toString();
                }
            });
            operationTarget = files.get(0);
            update();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "zoom in image", e);
        }
    }

    public void zoomOut() {
        if (scale == SCALE_MINIMUM) {
            return;
        }
        scale -= SCALE_UNIT;
        scale = Math.max(scale, SCALE_MINIMUM);
        try {
            List<File> files = Thumbnails.of(sourcePhotoFile).scale(scale).asFiles(TmpFileManager.getTmpDir(), Rename.PREFIX_DOT_THUMBNAIL);
            operationTarget = files.get(0);
            update();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "zoom out image", e);
        }
    }

    public void noScale() {
        operationTarget = sourcePhotoFile;
        scale = 1.0;
        update();
    }

    public void openPhoto(File photoFile) {
        sourcePhotoFile = photoFile;
        operationTarget = sourcePhotoFile;
        rotate = 0;
        scale = 1.0;
        imageView.setRotate(0);
        update();
    }

    public void closePhoto() {
        sourcePhotoFile = null;
        operationTarget = null;
        imageView.setImage(null);
    }

    private void update() {
        try {
            imageView.setImage(new Image(new FileInputStream(operationTarget)));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "update error", e);
        }
    }
}
