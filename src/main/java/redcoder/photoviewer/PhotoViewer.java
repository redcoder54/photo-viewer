package redcoder.photoviewer;

import redcoder.photoviewer.log.LoggingUtils;
import redcoder.photoviewer.ui.PhotoViewerFrame;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoViewer {

    private static final Logger LOGGER = Logger.getLogger(PhotoViewer.class.getName());

    public static void main(String[] args) {
        LoggingUtils.resetLogManager();
        LOGGER.log(Level.CONFIG, "LogManager has been reset.");
        SwingUtilities.invokeLater(() -> {
            PhotoViewerFrame frame = new PhotoViewerFrame();
            frame.creatAndShowGUI();
        });
    }
}
