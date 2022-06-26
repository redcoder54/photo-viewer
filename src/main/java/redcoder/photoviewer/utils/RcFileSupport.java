package redcoder.photoviewer.utils;

import java.io.File;

public abstract class RcFileSupport {

    private static final String dir = "redcoder54/photo-viewer";

    public static File getParentDir() {
        File file = new File(SystemUtils.getUserHome(), dir);
        ensureDirExist(file);
        return file;
    }

    public static void ensureDirExist(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
