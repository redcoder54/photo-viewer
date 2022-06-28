package redcoder.photoviewer.core.dialog;

import javafx.scene.control.*;
import org.tbee.javafx.scene.layout.fxml.MigPane;

/**
 * 任务进度对话
 *
 * @author wxy
 * @since 2022-06-27
 */
public class TaskProgressDialog extends Dialog<Void> {

    private final ProgressBar progressBar = new ProgressBar(-1);
    private final TextArea textArea = new TextArea();

    public TaskProgressDialog(String title) {
        setTitle(title);
        textArea.setEditable(false);

        MigPane content = new MigPane();
        content.setLayout("flowy");
        content.add(progressBar, "pushx");
        content.add(textArea, "pushx");

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void append(String info) {
        textArea.appendText(info + System.lineSeparator());
    }
}
