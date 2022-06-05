package redcoder.photoviewer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 */
public class FileUtils {

    public static List<File> extractImageFileOnly(List<File> files) {
        List<File> onlyFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                extractFileFromDir(file, onlyFiles);
            } else if (isImageFile(file)) {
                onlyFiles.add(file);
            }
        }
        return onlyFiles;
    }

    private static void extractFileFromDir(File dir, List<File> onlyFiles) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    extractFileFromDir(file, onlyFiles);
                } else if (isImageFile(file)) {
                    onlyFiles.add(file);
                }
            }
        }
    }

    private static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith("jpeg") || name.endsWith("gif");
    }
}
