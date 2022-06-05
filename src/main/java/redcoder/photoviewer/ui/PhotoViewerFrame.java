package redcoder.photoviewer.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoViewerFrame extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(PhotoViewerFrame.class.getName());

    private PhotoViewerPane photoViewerPane;

    public PhotoViewerFrame() {
        super("Photo Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(900, 600));
        setLayout(new MigLayout("fill"));
        setTransferHandler(new InvoiceTransferHandler());
    }

    public void creatAndShowGUI() {
        photoViewerPane = new PhotoViewerPane();
        add(photoViewerPane, "grow");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class InvoiceTransferHandler extends TransferHandler {

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            try {
                @SuppressWarnings("unchecked")
                List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                photoViewerPane.addFile(files);
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "importData", e);
                return false;
            }
        }


        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDrop() || support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }
    }
}
