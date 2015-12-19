package chen.zheng.gifdecodeencode.Utils;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

/**
 * Created by ChenZheng on 2015/12/19.
 */
public class CommonUtils {
    public static Intent getImageFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    public static String fileName(String path) {
        if (!isEmpty(path)) {
            // adapted from DropboxAPI.java v1.5.4
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            int ind = path.lastIndexOf('/');

            return path.substring(ind + 1, path.length());
        }

        return "";
    }

    public static String parentPath(String path) {
        // adapted from DropboxAPI.java v1.5.4
        if (isEmpty(path) || path.equals("/")) {
            return "";
        } else {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            int ind = path.lastIndexOf('/');

            if (ind == 0) {
                // strip the last slash, unless the entire path is '/'
                ind = 1;
            }

            return ind == -1 ? path : path.substring(0, ind);
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    static File createUniqueFileInternal(File directory, String filename) throws IOException {
        File file = new File(directory, filename);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (file.createNewFile()) {
            return file;
        }
        // Get the extension of the file, if any.
        int index = filename.lastIndexOf('.');
        String name = filename;
        String extension = "";

        if (index != -1) {
            name = filename.substring(0, index);
            extension = filename.substring(index);
        }

        for (int i = 2; i < Integer.MAX_VALUE; i++) {
            file = new File(directory, name + "-" + i + extension);
            if (file.createNewFile()) {
                return file;
            }
        }
        return null;
    }

    public static String createUniqueFileName(String parentPath, String fileName) {
        try {
            File directory = new File(parentPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            if (file.createNewFile()) {
                return file.getPath();
            }
            // Get the extension of the file, if any.
            int index = fileName.lastIndexOf('.');
            String name = fileName;
            String extension = "";

            if (index != -1) {
                name = fileName.substring(0, index);
                extension = fileName.substring(index);
            }

            for (int i = 2; i < Integer.MAX_VALUE; i++) {
                file = new File(directory, name + "-" + i + extension);
                if (file.createNewFile()) {
                    return file.getPath();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
