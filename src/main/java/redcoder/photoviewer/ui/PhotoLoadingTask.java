// package redcoder.photoviewer.ui;
//
// import net.miginfocom.swing.MigLayout;
// import redcoder.photoviewer.utils.FileUtils;
// import redcoder.photoviewer.utils.StringUtils;
//
// import javax.imageio.ImageIO;
// import javax.swing.*;
// import javax.swing.text.BadLocationException;
// import javax.swing.text.Document;
// import java.awt.*;
// import java.awt.event.WindowAdapter;
// import java.awt.event.WindowEvent;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.util.List;
// import java.util.*;
// import java.util.logging.Level;
// import java.util.logging.Logger;
// import java.util.stream.Collectors;
//
// public class PhotoLoadingTask extends SwingWorker<Map<String, List<PhotoLoadingTask.PhotoFile>>, Void> {
//
//     private static final Logger LOGGER = Logger.getLogger(PhotoLoadingTask.class.getName());
//
//     private static final int HANDLED_FILE_MAXIMUM_PER_TIME = 20;
//     private static final String HANDLED_FILE_NUM_PROPERTY = "handledFileNum";
//     private static final String KEY_SUCCESS = "successList";
//     private static final String KEY_FAIL = "failList";
//
//     private final PhotoPreviewPane previewPane;
//     private final List<File> files;
//     private final TaskProgressBar taskProgressBar;
//     private int handledFileNum = 0;
//     private boolean done = false;
//     private boolean terminate = false;
//
//     public PhotoLoadingTask(PhotoPreviewPane previewPane, List<File> files) {
//         this.previewPane = previewPane;
//         this.files = files;
//         this.taskProgressBar = new TaskProgressBar();
//
//         addPropertyChangeListener(evt -> {
//             if (HANDLED_FILE_NUM_PROPERTY.equals(evt.getPropertyName())) {
//                 taskProgressBar.setIndeterminate(false);
//                 int progress = (Integer) evt.getNewValue();
//                 taskProgressBar.setValue(progress);
//             }
//         });
//     }
//
//     @Override
//     protected Map<String, List<PhotoFile>> doInBackground() throws Exception {
//         List<File> imageFiles = FileUtils.extractImageFileOnly(files);
//         if (imageFiles.size() > HANDLED_FILE_MAXIMUM_PER_TIME) {
//             String message = "The number of open files exceeds the limit: 20!";
//             JOptionPane.showMessageDialog(null, message, PhotoViewerFrame.TITLE, JOptionPane.WARNING_MESSAGE);
//             return Collections.emptyMap();
//         }
//
//         taskProgressBar.setValue(0);
//         taskProgressBar.setMaximum(imageFiles.size());
//
//         List<PhotoFile> successList = new ArrayList<>(imageFiles.size());
//         List<PhotoFile> failList = new ArrayList<>(imageFiles.size());
//         Iterator<File> it = imageFiles.iterator();
//         while (!terminate && it.hasNext()) {
//             File file = it.next();
//             try {
//                 BufferedImage bufferedImage = ImageIO.read(file);
//                 if (bufferedImage == null) {
//                     LOGGER.log(Level.SEVERE, "Cannot open file: {0}, it's a image file?", file.getAbsolutePath());
//                     failList.add(new PhotoFile(file, null));
//                 } else {
//                     successList.add(new PhotoFile(file, bufferedImage));
//                 }
//             } catch (IOException e) {
//                 LOGGER.log(Level.SEVERE, "Failed to open file: {0}", file.getAbsolutePath());
//                 failList.add(new PhotoFile(file, null));
//             }
//             taskProgressBar.appendStr("handled file: " + file.getName());
//             firePropertyChange(HANDLED_FILE_NUM_PROPERTY, handledFileNum, ++handledFileNum);
//         }
//
//         Map<String, List<PhotoFile>> result = new HashMap<>();
//         result.put(KEY_SUCCESS, successList);
//         result.put(KEY_FAIL, failList);
//
//         return result;
//     }
//
//     @Override
//     protected void done() {
//         try {
//             done = true;
//             taskProgressBar.hidden();
//             Toolkit.getDefaultToolkit().beep();
//
//             Map<String, List<PhotoFile>> result = get();
//             if (result.isEmpty()) {
//                 return;
//             }
//             for (PhotoFile pf : result.get(KEY_SUCCESS)) {
//                 previewPane.addPhoto(pf.bufferedImage);
//             }
//             List<PhotoFile> failedList = result.get(KEY_FAIL);
//             if (!failedList.isEmpty()) {
//                 List<String> names = failedList.stream().map(t -> t.sourceFile.getName()).collect(Collectors.toList());
//                 String message = "The following file cannot be opened:" + "\n\n" + StringUtils.join(names, "\n") + "\n\n";
//                 JOptionPane.showMessageDialog(null, message, PhotoViewerFrame.TITLE, JOptionPane.INFORMATION_MESSAGE);
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
//
//     public void showProgressBar() {
//         taskProgressBar.shown();
//     }
//
//     private class TaskProgressBar {
//
//         private final String[] options = {"确定", "取消"};
//
//         private final JProgressBar progressBar;
//         private final JTextArea textArea;
//         private final JDialog dialog;
//
//         public TaskProgressBar() {
//             progressBar = new JProgressBar();
//             progressBar.setMinimum(0);
//             progressBar.setIndeterminate(true);
//             progressBar.setStringPainted(true);
//
//             textArea = new JTextArea(5, 30);
//             textArea.setEditable(false);
//             textArea.setMargin(new Insets(5, 5, 5, 5));
//
//             dialog = new JDialog();
//             dialog.setTitle(PhotoViewerFrame.TITLE);
//             dialog.setModal(true);
//             dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//             dialog.addWindowListener(new WindowAdapter() {
//                 @Override
//                 public void windowClosing(WindowEvent e) {
//                     if (!done) {
//                         int i = JOptionPane.showOptionDialog(null, "文件还未处理完，确定终止吗？", "终止任务？",
//                                 JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
//                         if (i == JOptionPane.YES_OPTION) {
//                             terminate = true;
//                             dialog.dispose();
//                             dialog.setVisible(false);
//                         }
//                     }
//                 }
//             });
//             dialog.setLayout(new MigLayout("fill, flowy"));
//         }
//
//         public void setIndeterminate(boolean newValue) {
//             progressBar.setIndeterminate(newValue);
//         }
//
//         public void setValue(int value) {
//             progressBar.setValue(value);
//         }
//
//         public void setMaximum(int maximum) {
//             progressBar.setMaximum(maximum);
//         }
//
//         public void appendStr(String str) {
//             textArea.append(str);
//             textArea.append(System.lineSeparator());
//
//             try {
//                 Document document = textArea.getDocument();
//                 Rectangle rectangle = textArea.modelToView(document.getLength() - 1);
//                 if (rectangle != null) {
//                     textArea.scrollRectToVisible(rectangle);
//                 }
//             } catch (BadLocationException e) {
//                 //
//             }
//         }
//
//         public void shown() {
//             dialog.add(progressBar);
//             dialog.add(new JScrollPane(textArea));
//             dialog.pack();
//             dialog.setLocationRelativeTo(null);
//             dialog.setVisible(true);
//         }
//
//         public void hidden() {
//             dialog.dispose();
//             dialog.setVisible(false);
//         }
//     }
//
//     public static class PhotoFile {
//         private File sourceFile;
//         private BufferedImage bufferedImage;
//
//         public PhotoFile(File sourceFile, BufferedImage bufferedImage) {
//             this.sourceFile = sourceFile;
//             this.bufferedImage = bufferedImage;
//         }
//     }
// }
