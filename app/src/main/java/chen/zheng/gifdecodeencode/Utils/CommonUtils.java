package chen.zheng.gifdecodeencode.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;

import chen.zheng.gifdecodeencode.R;

/**
 * Created by ChenZheng on 2015/12/19.
 */
public class CommonUtils {
    private static final int KILO = 1024;
    private static final int MEGA = KILO * KILO;


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

    public static String getFilePathFromURI(Context context, Uri contentUri) {

        String filePath = contentUri.toString();
        if (filePath.startsWith("file://")) {
            try {
                filePath = URLDecoder.decode(filePath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return filePath.substring(7);
        } else if (filePath.startsWith("content://")) {
            Cursor cursor = null;
            try {
                String[] project = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, project, null,
                        null, null);
                if (cursor != null) {
                    int idxFilePath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (idxFilePath != -1) {
                        cursor.moveToFirst();
                        return cursor.getString(idxFilePath);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return null;
    }

    public static long getFileSize(Context context,String filePath){
        if(context!=null && !TextUtils.isEmpty(filePath)){
            File f= new File(filePath);
            if (f.exists() && f.isFile()){
                return f.length();
            }
        }
        return -1l;
    }

    public static String getReadableFileSize(Context context, String filePath){
        if(context!=null && !TextUtils.isEmpty(filePath)){
            File f= new File(filePath);
            if (f.exists() && f.isFile()){
                return convertToHumanReadableSize(context,f.length());
            }
        }
        return null;
    }
    public static String convertToHumanReadableSize(Context context, long size) {

        // liutz,对于KB采用非小数方式显示，MB，GB采用两位小数方式显示
        DecimalFormat df = new DecimalFormat("#.00");
        // liutz,采用四舍五入方式舍入\r 80
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (size < KILO) {
            return context.getString(R.string.bytes, size);
        } else if (size < MEGA) {
            df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            return context.getString(R.string.kilobytes,
                    df.format((double) size / KILO));
        } else {
            return context.getString(R.string.megabytes,
                    df.format((double) size / MEGA));
        }
    }
    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
}
