package redcoder.photoviewer.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PhotoViewerPane extends JSplitPane implements ActionListener {


    private final PhotoPane photoPane;
    private final PhotoPreviewPane previewPane;

    private final JFileChooser fileChooser = new JFileChooser();
    private final JButton clearBtn = new JButton("清空");
    private final JButton openBtn = new JButton("打开图片");
    private final JButton leftBtn = new JButton("左旋");
    private final JButton rightBtn = new JButton("右旋");
    private final JButton zoomInBtn = new JButton("放大");
    private final JButton zoomOutBtn = new JButton("缩小");
    private final JButton restoreBtn = new JButton("还原");

    public PhotoViewerPane() {
        super(JSplitPane.VERTICAL_SPLIT);

        photoPane = new PhotoPane();
        previewPane = new PhotoPreviewPane(photoPane);

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        clearBtn.addActionListener(this);
        openBtn.addActionListener(this);
        leftBtn.addActionListener(this);
        rightBtn.addActionListener(this);
        zoomInBtn.addActionListener(this);
        zoomOutBtn.addActionListener(this);
        restoreBtn.addActionListener(this);

        // top part
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(previewPane);
        splitPane.setRightComponent(photoPane);
        // splitPane.setResizeWeight(0.2);
        // bottom part
        JPanel btnPanel = new JPanel(new MigLayout("fillx"));
        btnPanel.add(clearBtn, "split 7, center");
        btnPanel.add(openBtn);
        btnPanel.add(leftBtn);
        btnPanel.add(rightBtn);
        btnPanel.add(zoomInBtn);
        btnPanel.add(zoomOutBtn);
        btnPanel.add(restoreBtn);

        setTopComponent(splitPane);
        setBottomComponent(btnPanel);
        setResizeWeight(0.999999);

        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "none");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "none");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "none");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "none");
    }

    public void addFile(List<File> files) {
        PhotoLoadingTask task = new PhotoLoadingTask(previewPane, files);
        task.execute();
        task.showProgressBar();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearBtn) {
            previewPane.clear();
            photoPane.removePhoto();
        } else if (e.getSource() == openBtn) {
            int i = fileChooser.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                addFile(Arrays.asList(files));
            }
        } else if (e.getSource() == leftBtn) {
            photoPane.rotateLeft();
        } else if (e.getSource() == rightBtn) {
            photoPane.rotateRight();
        } else if (e.getSource() == zoomInBtn) {
            photoPane.zoomIn();
        } else if (e.getSource() == zoomOutBtn) {
            photoPane.zoomOut();
        } else if (e.getSource() == restoreBtn) {
            photoPane.restore();
        }
    }
}
