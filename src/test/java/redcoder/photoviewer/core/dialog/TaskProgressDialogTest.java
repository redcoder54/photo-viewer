package redcoder.photoviewer.core.dialog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Optional;

public class TaskProgressDialogTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("show task progress dialog");
        button.setOnAction(event -> {
            TaskProgressDialog dialog = new TaskProgressDialog("测试任务");
            dialog.show();
        });
        StackPane root = new StackPane();
        root.getChildren().add(button);

        Scene scene = new Scene(root,400,300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TaskProgressDialogTest");
        primaryStage.show();
    }
}
