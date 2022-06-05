package redcoder.photoviewer.ui;

import net.miginfocom.swing.MigLayout;
import redcoder.photoviewer.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class PhotoPreviewPane extends JScrollPane {

    private static final String ACTION_UP = "UP_PHOTO";
    private static final String ACTION_DOWN = "DOWN_PHOTO";

    private final JPanel panel;
    private final PhotoPane photoPane;
    private NavigableLabel tailPointer;
    private NavigableLabel selectedNavLabel;

    public PhotoPreviewPane(PhotoPane photoPane) {
        this.panel = new JPanel(new MigLayout("flowy, fillx"));
        this.photoPane = photoPane;

        setPreferredSize(new Dimension(150, 150));
        getHorizontalScrollBar().setUnitIncrement(5);
        getVerticalScrollBar().setUnitIncrement(5);
        setViewportView(panel);

        // add key-binding
        ActionMap actionMap = panel.getActionMap();
        actionMap.put(ACTION_UP, new UpAction());
        actionMap.put(ACTION_DOWN, new DownAction());
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), ACTION_UP);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), ACTION_DOWN);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), ACTION_UP);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), ACTION_DOWN);
    }

    public void addPhoto(BufferedImage bufferedImage) {
        NavigableLabel label = new NavigableLabel(bufferedImage);

        if (tailPointer != null) {
            tailPointer.next = label;
            label.prev = tailPointer;
        }
        tailPointer = label;

        label.setHorizontalAlignment(SwingUtilities.CENTER);
        label.setIcon(new ImageIcon(ImageUtils.createThumbnail(bufferedImage)));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchSelectedLabel(label);
            }
        });
        panel.add(label, "growx, center");
        panel.validate();

        JViewport viewport = (JViewport) panel.getParent();
        viewport.setViewPosition(label.getLocation());
    }

    public void clear() {
        panel.removeAll();
        panel.repaint();
    }

    private void switchSelectedLabel(NavigableLabel label) {
        if (label == null) {
            return;
        }

        // display related image
        photoPane.setPhoto(label.bufferedImage);

        if (selectedNavLabel != null) {
            // restore last selected label
            selectedNavLabel.setBorder(BorderFactory.createEmptyBorder());
        }

        // remember current selected label
        selectedNavLabel = label;
        // highlight selected label
        selectedNavLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    private static class NavigableLabel extends JLabel {
        NavigableLabel prev;
        NavigableLabel next;
        BufferedImage bufferedImage;

        public NavigableLabel(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }
    }

    private class UpAction extends AbstractAction{
        public UpAction() {
            super("上一张图片");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switchSelectedLabel(selectedNavLabel.prev);
        }
    }

    private class DownAction extends AbstractAction{
        public DownAction() {
            super("下一张图片");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switchSelectedLabel(selectedNavLabel.next);
        }
    }
}
