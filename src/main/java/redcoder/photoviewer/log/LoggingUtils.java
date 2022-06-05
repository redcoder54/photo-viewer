package redcoder.photoviewer.log;

import redcoder.photoviewer.utils.RcFileSupport;

import java.io.File;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LoggingUtils {

    public static void resetLogManager() {
        try (InputStream in = LoggingUtils.class.getClassLoader().getResourceAsStream("log.properties")) {
            ensureLogDirExist();
            LogManager.getLogManager().readConfiguration(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensureLogDirExist() {
        File dir = new File(RcFileSupport.getParentDir(), "log");
        if (dir.exists()) {
            return;
        }
        dir.mkdir();
    }
}
