package redcoder.photoviewer.core.dialog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Optional;

public class ConfirmDialogTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("show confirm dialog");
        button.setOnAction(event -> {
            ConfirmDialog dialog = new ConfirmDialog("ConfirmDialogTest","正在全力加载图片，确定终止吗？");
            Optional<ConfirmDialog.State> state = dialog.showAndWait();
            if (state.isPresent() && state.get() == ConfirmDialog.State.OK) {
                System.out.println("ok");
            }
        });
        StackPane root = new StackPane();
        root.getChildren().add(button);

        Scene scene = new Scene(root,400,300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ConfirmDialogTest");
        primaryStage.show();
    }
}
