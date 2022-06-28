package redcoder.photoviewer;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Random;

public class ProgressSample extends Application {

    private Random random = new Random();

    @Override
    public void start(Stage stage) {
        Button button = new Button("启动任务");
        button.setOnAction(event -> {
            Dialog<Void> dialog = new Dialog<>();

            Task<Void> task = new Task<Void>() {
                @Override public Void call() {
                    final int max = 100;
                    for (int i=1; i<=max; i++) {
                        if (isCancelled()) {
                            break;
                        }
                        try {
                            Thread.sleep(random.nextInt(100));
                        } catch (InterruptedException e) {
                            //
                        }
                        updateProgress(i, max);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    dialog.close();
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            ProgressBar progressBar = new ProgressBar(-1);
            progressBar.progressProperty().bind(task.progressProperty());

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setContent(progressBar);
            dialog.setTitle("任务处理中");
            dialogPane.getButtonTypes().add(ButtonType.CANCEL);
            dialog.show();
        });

        StackPane root = new StackPane();
        root.getChildren().add(button);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
