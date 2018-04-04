package com.hct.gios.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class JavaChanger {
    private static int mColor;
    private static int[] mPalette = new int[255 * 3 + 1];

    private static void updatePalette(int color) {
        RGBDouble rgb = new RGBDouble((color >> 16) & 0xFF,
                (color >> 8) & 0xFF, color & 0xFF);
        HSIDouble hsi_change = RGB2HSI_neo(rgb);
        RGBDouble rgb_change = new RGBDouble();
        for (int i = 0; i < mPalette.length; i++) {
            hsi_change.i = (double) i / (3 * 255);
            rgb_change = HSI2RGB_neo(hsi_change);
            mPalette[i] = ((int) rgb_change.r << 16)
                    + ((int) rgb_change.g << 8) + ((int) rgb_change.b);
        }
        mColor = (color & 0xffffff);
    }

    private static Bitmap createColoredIcon(Bitmap srcImg, int color) {
        int[] argb = new int[srcImg.getWidth() * srcImg.getHeight()];
        srcImg.getPixels(argb, 0, srcImg.getWidth(), 0, 0, srcImg.getWidth(),
                srcImg.getHeight());
        color &= 0x00ffffff;
        for (int i = 0; i < argb.length; i++) {
            if ((argb[i] & 0x00ffffff) == 0x00328BDE
                    || (argb[i] & 0x00ffffff) == 0x00FAFAFA) {
                argb[i] = (argb[i] & 0xff000000) | color;
            } else {
                argb[i] = mPalette[(argb[i] & 0xff) + ((argb[i] >> 8) & 0xff)
                        + ((argb[i] >> 16) & 0xff)]
                        | (argb[i] & 0xff000000);
            }
        }

        return Bitmap.createBitmap(argb, srcImg.getWidth(), srcImg.getHeight(),
                srcImg.getConfig());
    }

    private static Bitmap createColoredIconWidget(Bitmap srcImg, int color,
            boolean disable) {
        int[] argb = new int[srcImg.getWidth() * srcImg.getHeight()];
        srcImg.getPixels(argb, 0, srcImg.getWidth(), 0, 0, srcImg.getWidth(),
                srcImg.getHeight());
        color &= 0x00ffffff;
        for (int i = 0; i < argb.length; i++) {

            if ((argb[i] & 0x00FFFFFF) != 0x00FFFFFF)

            {
                if (disable) {
                    argb[i] = (argb[i] & 0x9A000000) | color;
                } else {
                    argb[i] = (argb[i] & 0xff000000) | color;
                }
            }

            // }

        }

        return Bitmap.createBitmap(argb, srcImg.getWidth(), srcImg.getHeight(),
                srcImg.getConfig());
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        // int fgWidth = foreground.getWidth();
        // int fgHeight = foreground.getHeight();
        // create the new blank bitmap
        Bitmap newbmp = Bitmap
                .createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        // draw bg into
        cv.drawBitmap(background, 0, 0, null);
        // draw fg into
        cv.drawBitmap(foreground, 0, 0, null);
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);
        // store
        cv.restore();
        return newbmp;
    }

    public static Bitmap setColor(Bitmap srcImg, int color) {
        if ((color & 0xffffff) != mColor) {
            updatePalette(color);
        }

        return createColoredIcon(srcImg, color);
    }

    public static Bitmap setColorWidget(Bitmap srcImg, int color,
            boolean disable) {
        if ((color & 0xffffff) != mColor) {
            updatePalette(color);
        }

        return createColoredIconWidget(srcImg, color, disable);
    }

    public static Bitmap setColor_neo(Bitmap sourceImg, int red, int green,
            int blue) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
                sourceImg.getWidth(), sourceImg.getHeight());
        RGBDouble rgb = new RGBDouble((double) red, (double) green,
                (double) blue);
        RGBDouble rgb_change = new RGBDouble();
        HSIDouble hsi_change = new HSIDouble();
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < argb.length; i++) {
            if ((argb[i] & 0xFF000000) != 0x00000000)
            {
                if ((argb[i] & 0x00FFFFFF) != 0x00FFFFFF) {
                    r = (argb[i] & 0x00FF0000) >> 16;
                    g = (argb[i] & 0x0000FF00) >> 8;
                    b = (argb[i] & 0x000000FF);

                    hsi_change = RGB2HSI_neo(rgb);
                    hsi_change.i = (double) (r + g + b) / (3 * 255);
                    rgb_change = HSI2RGB_neo(hsi_change);

                    argb[i] = (argb[i] & 0xFF000000)
                            + ((int) rgb_change.r << 16)
                            + ((int) rgb_change.g << 8) + ((int) rgb_change.b);
                }
            }
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
                sourceImg.getHeight(), Config.ARGB_8888);
        return sourceImg;
    }

    public static HSIDouble RGB2HSI_neo(RGBDouble rgb) {
        HSIDouble hsi = new HSIDouble();

        double h = 0;
        double s = 0;
        double i = 0;
        double r = rgb.r / 255;
        double g = rgb.g / 255;
        double b = rgb.b / 255;

        double max, min;
        max = Math.max(Math.max(r, g), b);
        min = Math.min(Math.min(r, g), b);

        if (max == min) {
            h = 0;
        } else if (max == r && g >= b) {
            h = 60 * ((g - b) / (max - min));
        } else if (max == r && g < b) {
            h = 60 * ((g - b) / (max - min)) + 360;
        } else if (max == g) {
            h = 60 * ((b - r) / (max - min)) + 120;
        } else if (max == b) {
            h = 60 * ((r - g) / (max - min)) + 240;
        }
        i = (max + min) / 2;
        if (i == 0 || max == min) {
            s = 0;
        } else if (i > 0 && i <= 0.5) {
            s = (max - min) / (max + min);
        } else if (i > 0.5) {
            s = (max - min) / (2 - (max + min));
        }

        hsi.h = h;
        hsi.s = s;
        hsi.i = i;
        return hsi;
    }

    public static RGBDouble HSI2RGB_neo(HSIDouble hsi) {
        RGBDouble rgb = new RGBDouble();

        double h = hsi.h;
        double s = hsi.s;
        double i = hsi.i;
        double r = 0;
        double g = 0;
        double b = 0;

        if (s == 0) {
            r = i;
            g = i;
            b = i;
        } else {
            double q, p, TR, TG, TB;
            if (i < 0.5) {
                q = i * (1 + s);
            } else {
                q = i + s - (i * s);
            }
            p = 2 * i - q;
            h = h / 360;
            TR = h + ((double) 1 / 3);
            TG = h;
            TB = h - ((double) 1 / 3);
            r = toRGB(TR, q, p, h);
            g = toRGB(TG, q, p, h);
            b = toRGB(TB, q, p, h);
        }
        rgb.r = Math.round(255 * r);
        rgb.g = Math.round(255 * g);
        rgb.b = Math.round(255 * b);
        return rgb;
    }

    public static double toRGB(double TColor, double q, double p, double h) {
        if (TColor < 0) {
            TColor += 1;
        }
        if (TColor > 1) {
            TColor -= 1;
        }
        if (TColor < ((double) 1 / 6)) {
            return p + ((q - p) * 6 * TColor);
        } else if (TColor < ((double) 1 / 2)) {
            return q;
        } else if (TColor < ((double) 2 / 3)) {
            return p + ((q - p) * 6 * ((double) 2 / 3 - TColor));
        } else {
            return p;
        }
    }

    static class HSIDouble {
        public double h;
        public double s;
        public double i;

        public HSIDouble() {
            h = 0;
            s = 0;
            i = 0;
        }

        public HSIDouble(double h_in, double s_in, double i_in) {
            h = h_in;
            s = s_in;
            i = i_in;
        }
    }

    static class RGBDouble {
        public double r;
        public double g;
        public double b;

        public RGBDouble() {
            r = 0;
            g = 0;
            b = 0;
        }

        public RGBDouble(double r_in, double g_in, double b_in) {
            r = r_in;
            g = g_in;
            b = b_in;
        }
    }

}
