package redcoder.photoviewer.ui;

import net.coobird.thumbnailator.Thumbnails;
import net.miginfocom.swing.MigLayout;
import redcoder.photoviewer.utils.ImageUtils;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoPane extends JScrollPane implements MouseWheelListener {

    private static final Logger LOGGER = Logger.getLogger(PhotoPane.class.getName());
    private static final double SCALE_UNIT = 0.1;
    private static final double SCALE_MINIMUM = 0.1;
    private static final double SCALE_MAXIMUM = 10;

    private final JLabel label = new JLabel();
    private BufferedImage originalImage;
    private BufferedImage changeableImage;
    private double scale = 1.0;

    public PhotoPane() {
        super();

        JPanel panel = new JPanel(new MigLayout("fill"));
        panel.add(label, "center");

        getHorizontalScrollBar().setUnitIncrement(5);
        getVerticalScrollBar().setUnitIncrement(5);
        setViewportView(panel);
        addMouseWheelListener(this);
    }

    public void rotateLeft() {
        changeableImage = ImageUtils.rotate(changeableImage, Math.toRadians(-90));
        updateIcon();
    }

    public void rotateRight() {
        changeableImage = ImageUtils.rotate(changeableImage, Math.toRadians(90));
        updateIcon();
    }

    public void zoomIn() {
        if (scale == SCALE_MAXIMUM) {
            return;
        }
        scale += SCALE_UNIT;
        scale = Math.min(scale, SCALE_MAXIMUM);
        try {
            changeableImage = Thumbnails.of(originalImage).scale(scale).asBufferedImage();
            updateIcon();
        } catch (IOException e) {
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
            changeableImage = Thumbnails.of(originalImage).scale(scale).asBufferedImage();
            updateIcon();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "zoom out image", e);
        }
    }

    public void restore() {
        changeableImage = originalImage;
        updateIcon();
    }

    public void setPhoto(BufferedImage bufferedImage) {
        originalImage = bufferedImage;
        changeableImage = originalImage;
        scale = 1.0;
        updateIcon();
    }

    public void removePhoto() {
        originalImage = null;
        changeableImage = null;
        scale = 1.0;
        label.setIcon(null);
    }

    private void updateIcon() {
        label.setIcon(new ImageIcon(changeableImage));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (InputEvent.CTRL_MASK == e.getModifiers()) {
            if (e.getWheelRotation() < 0) {
                // wheel rotate up
                zoomIn();
            } else if (e.getWheelRotation() > 0) {
                // wheel rotate down
                zoomOut();
            }
        }
    }
}
