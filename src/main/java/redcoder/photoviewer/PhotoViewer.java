package redcoder.photoviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import redcoder.photoviewer.log.LoggingUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoViewer extends Application {

    private static final Logger LOGGER = Logger.getLogger(PhotoViewer.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        SplitPane root = FXMLLoader.load(getClass().getResource("/fxml/photo-viewer.fxml"));
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Photo Viewer");
        primaryStage.show();
    }

    public static void main(String[] args) {
        LoggingUtils.resetLogManager();
        LOGGER.log(Level.CONFIG, "LogManager has been reset.");
        launch(args);
    }
}
