package redcoder.photoviewer.core;

import redcoder.photoviewer.utils.RcFileSupport;

import java.io.File;

public class TmpFileManager {

    private static final File tmpDir = new File(RcFileSupport.getParentDir(), "tmp");
    private static final File thumbnailsDir = new File(RcFileSupport.getParentDir(), "thumbnails");

    static {
        // create dir if not exist
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        if (!thumbnailsDir.exists()) {
            thumbnailsDir.mkdirs();
        }
    }

    public static File getTmpDir() {
        return tmpDir;
    }

    public static File getThumbnailsDir() {
        return thumbnailsDir;
    }

    public static void clearTmpDir() {
        File[] files = tmpDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            file.delete();
        }
    }

    public static void deleteThumbnails() {
        File[] files = thumbnailsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            file.delete();
        }
    }
}
