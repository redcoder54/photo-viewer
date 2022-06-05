package redcoder.photoviewer.ui;

import net.miginfocom.swing.MigLayout;
import redcoder.photoviewer.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoLoadingTask extends SwingWorker<Map<String, List<PhotoLoadingTask.PhotoFile>>, Void> {

    private static final Logger LOGGER = Logger.getLogger(PhotoLoadingTask.class.getName());

    private static final String HANDLED_FILE_NUM_PROPERTY = "handledFileNum";
    private static final String KEY_SUCCESS = "successList";
    private static final String KEY_FAIL = "failList";

    private final PhotoPreviewPane previewPane;
    private final List<File> files;
    private final TaskProgressBar taskProgressBar;
    private int handledFileNum = 0;
    private boolean done = false;
    private boolean terminate = false;

    public PhotoLoadingTask(PhotoPreviewPane previewPane, List<File> files) {
        this.previewPane = previewPane;
        this.files = files;
        this.taskProgressBar = new TaskProgressBar();

        addPropertyChangeListener(evt -> {
            if (HANDLED_FILE_NUM_PROPERTY.equals(evt.getPropertyName())) {
                taskProgressBar.setIndeterminate(false);
                int progress = (Integer) evt.getNewValue();
                taskProgressBar.setValue(progress);
            }
        });
    }

    @Override
    protected Map<String, List<PhotoFile>> doInBackground() throws Exception {
        List<File> imageFiles = FileUtils.extractImageFileOnly(files);

        taskProgressBar.setValue(0);
        taskProgressBar.setMaximum(imageFiles.size());

        List<PhotoFile> successList = new ArrayList<>(imageFiles.size());
        List<PhotoFile> failList = new ArrayList<>(imageFiles.size());
        Iterator<File> it = imageFiles.iterator();
        while (!terminate && it.hasNext()) {
            File file = it.next();

            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                successList.add(new PhotoFile(file, bufferedImage));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "打开文件失败：{0}", file.getAbsolutePath());
                failList.add(new PhotoFile(file, null));
            }
            taskProgressBar.appendStr("已处理文件" + file.getName());
            firePropertyChange(HANDLED_FILE_NUM_PROPERTY, handledFileNum, ++handledFileNum);
        }

        Map<String, List<PhotoFile>> result = new HashMap<>();
        result.put(KEY_SUCCESS, successList);
        result.put(KEY_FAIL, failList);

        return result;
    }

    @Override
    protected void done() {
        try {
            done = true;
            taskProgressBar.hidden();
            Toolkit.getDefaultToolkit().beep();

            Map<String, List<PhotoFile>> result = get();
            for (PhotoFile pf : result.get(KEY_SUCCESS)) {
                previewPane.addPhoto(pf.bufferedImage);
            }
            for (PhotoFile pf : result.get(KEY_FAIL)) {
                // todo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressBar() {
        taskProgressBar.shown();
    }

    private class TaskProgressBar {

        private final String[] options = {"确定", "取消"};

        private final JProgressBar progressBar;
        private final JTextArea textArea;
        private final JDialog dialog;

        public TaskProgressBar() {
            progressBar = new JProgressBar();
            progressBar.setMinimum(0);
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(true);

            textArea = new JTextArea(5, 30);
            textArea.setEditable(false);
            textArea.setMargin(new Insets(5, 5, 5, 5));

            dialog = new JDialog();
            dialog.setModal(true);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (!done) {
                        int i = JOptionPane.showOptionDialog(null, "文件还未处理完，确定终止吗？", "终止任务？",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        if (i == JOptionPane.YES_OPTION) {
                            terminate = true;
                            dialog.dispose();
                            dialog.setVisible(false);
                        }
                    }
                }
            });
            dialog.setLayout(new MigLayout("fill, flowy"));
        }

        public void setIndeterminate(boolean newValue) {
            progressBar.setIndeterminate(newValue);
        }

        public void setValue(int value) {
            progressBar.setValue(value);
        }

        public void setMaximum(int maximum) {
            progressBar.setMaximum(maximum);
        }

        public void appendStr(String str) {
            textArea.append(str);
            textArea.append(System.lineSeparator());

            try {
                Document document = textArea.getDocument();
                Rectangle rectangle = textArea.modelToView(document.getLength() - 1);
                if (rectangle != null) {
                    textArea.scrollRectToVisible(rectangle);
                }
            } catch (BadLocationException e) {
                //
            }
        }

        public void shown() {
            dialog.add(progressBar);
            dialog.add(new JScrollPane(textArea));
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

        public void hidden() {
            dialog.dispose();
            dialog.setVisible(false);
        }
    }

    public static class PhotoFile {
        private File sourceFile;
        private BufferedImage bufferedImage;

        public PhotoFile(File sourceFile, BufferedImage bufferedImage) {
            this.sourceFile = sourceFile;
            this.bufferedImage = bufferedImage;
        }
    }
}
