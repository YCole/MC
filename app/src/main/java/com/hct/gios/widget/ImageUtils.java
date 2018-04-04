package com.hct.gios.widget;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class ImageUtils {

    public static final String TAG = "ImageUtils";

    public static final boolean USING_NATIVE = false;

    public static boolean changeImageColor(String sourcePath,
            String targetPath, int color) throws IOException {
        return changeImageColor(new File(sourcePath), new File(targetPath),
                color);
    }

    public static boolean changeImageColor(File source, File target, int color)
            throws IOException {

        if (!source.exists()) {
            throw new IOException("source file do not exit!");
        }

        if (source.isFile() && !source.getName().toLowerCase().endsWith(".png")) {
            throw new IOException("source file do not png image or folder!");
        }

        if (!target.exists() || target.isFile()) {
            if (!target.mkdirs()) {
                throw new IOException("Craete " + target.getPath() + " fail!");
            }
        }

        return processFile(source, target, color);
    }

    public static boolean changeImageColor(Bitmap image, String target,
            int color) throws IOException {

        return changeImageColor(image, new File(target), color);
    }

    public static boolean changeImageColor(Bitmap image, File target, int color)
            throws IOException {
        return processColorChange(image, target, color);
    }

    public static boolean processColorChange(Bitmap image, File target,
            int color) throws IOException {

        Bitmap targetImage;

        if (USING_NATIVE) {
            // TODO using native
            return true;
        } else {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            targetImage = JavaChanger.setColor_neo(image, r, g, b);
            try {
                return saveDataToFile(targetImage, target);
            } catch (IOException e) {
                throw e;
            } finally {
                if (targetImage != null)
                    targetImage.recycle();
            }
        }

    }

    private static boolean processFile(File source, File target, int color)
            throws IOException {
        if (source.isFile()) {
            if (source.getPath().toLowerCase().endsWith(".png")) {
                Bitmap image = BitmapFactory.decodeFile(source.getPath());
                File t = new File(target, source.getName());
                try {
                    return processColorChange(image, t, color);
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                } finally {
                    if (image != null) {
                        image.recycle();
                    }
                }
            } else {
                Log.i(TAG, "file " + source.getPath()
                        + " is not a png image, skip it!");
                return true;
            }
        } else {
            File[] files2 = source.listFiles();
            File[] files = source.listFiles(new PngFileFilter());
            for (File f : files) {
                processFile(f, target, color);
            }
        }

        return true;
    }

    private static boolean saveDataToFile(Bitmap map, File target)
            throws IOException {
        FileOutputStream out = new FileOutputStream(target);
        map.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();

        Process p = Runtime.getRuntime().exec(
                "chmod 775 data/color/" + target.getName());
        return true;
    }

    static class PngFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.getPath().toLowerCase().endsWith(".png")) {
                return true;
            }
            return false;
        }

    }

}
