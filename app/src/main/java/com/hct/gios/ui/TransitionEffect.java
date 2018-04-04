package com.hct.gios.ui;

import android.content.Context;
import android.graphics.Bitmap;

public class TransitionEffect {
    public static Bitmap createBitmap(Context paramContext, Bitmap paramBitmap,
            int paramInt) {
        Bitmap localBitmap = paramBitmap.copy(paramBitmap.getConfig(), true);
        if (paramInt < 1)
            return null;
        int i = localBitmap.getWidth();
        int j = localBitmap.getHeight();
        int[] arrayOfInt1 = new int[i * j];

        localBitmap.getPixels(arrayOfInt1, 0, i, 0, 0, i, j);
        int k = i - 1;
        int m = j - 1;
        int n = i * j;
        int i1 = paramInt + paramInt + 1;
        int[] arrayOfInt2 = new int[n];
        int[] arrayOfInt3 = new int[n];
        int[] arrayOfInt4 = new int[n];
        int[] arrayOfInt5 = new int[Math.max(i, j)];
        int i12 = i1 + 1 >> 1;
        i12 *= i12;
        int[] arrayOfInt6 = new int[256 * i12];
        for (int i7 = 0; i7 < 256 * i12; i7++)
            arrayOfInt6[i7] = (i7 / i12);
        int i10;
        int i11 = i10 = 0;
        int[][] arrayOfInt = new int[i1][3];
        int i16 = paramInt + 1;
        int i4;
        int i3;
        int i2;
        int i19;
        int i18;
        int i17;
        int i22;
        int i21;
        int i20;
        int i8;
        int[] arrayOfInt7;
        int i15;
        int i13;
        int i14;
        for (int i6 = 0; i6 < j; i6++) {
            i20 = i21 = i22 = i17 = i18 = i19 = i2 = i3 = i4 = 0;
            for (int i7 = -paramInt; i7 <= paramInt; i7++) {
                i8 = arrayOfInt1[(i10 + Math.min(k, Math.max(i7, 0)))];
                arrayOfInt7 = arrayOfInt[(i7 + paramInt)];
                arrayOfInt7[0] = ((i8 & 0xFF0000) >> 16);
                arrayOfInt7[1] = ((i8 & 0xFF00) >> 8);
                arrayOfInt7[2] = (i8 & 0xFF);
                i15 = i16 - Math.abs(i7);
                i2 += arrayOfInt7[0] * i15;
                i3 += arrayOfInt7[1] * i15;
                i4 += arrayOfInt7[2] * i15;
                if (i7 > 0) {
                    i20 += arrayOfInt7[0];
                    i21 += arrayOfInt7[1];
                    i22 += arrayOfInt7[2];
                } else {
                    i17 += arrayOfInt7[0];
                    i18 += arrayOfInt7[1];
                    i19 += arrayOfInt7[2];
                }
            }
            i13 = paramInt;
            for (int i5 = 0; i5 < i; i5++) {
                arrayOfInt2[i10] = arrayOfInt6[i2];
                arrayOfInt3[i10] = arrayOfInt6[i3];
                arrayOfInt4[i10] = arrayOfInt6[i4];
                i2 -= i17;
                i3 -= i18;
                i4 -= i19;
                i14 = i13 - paramInt + i1;
                arrayOfInt7 = arrayOfInt[(i14 % i1)];
                i17 -= arrayOfInt7[0];
                i18 -= arrayOfInt7[1];
                i19 -= arrayOfInt7[2];
                if (i6 == 0)
                    arrayOfInt5[i5] = Math.min(i5 + paramInt + 1, k);
                i8 = arrayOfInt1[(i11 + arrayOfInt5[i5])];
                arrayOfInt7[0] = ((i8 & 0xFF0000) >> 16);
                arrayOfInt7[1] = ((i8 & 0xFF00) >> 8);
                arrayOfInt7[2] = (i8 & 0xFF);
                i20 += arrayOfInt7[0];
                i21 += arrayOfInt7[1];
                i22 += arrayOfInt7[2];
                i2 += i20;
                i3 += i21;
                i4 += i22;
                i13 = (i13 + 1) % i1;
                arrayOfInt7 = arrayOfInt[(i13 % i1)];
                i17 += arrayOfInt7[0];
                i18 += arrayOfInt7[1];
                i19 += arrayOfInt7[2];
                i20 -= arrayOfInt7[0];
                i21 -= arrayOfInt7[1];
                i22 -= arrayOfInt7[2];
                i10++;
            }
            i11 += i;
        }
        for (int i5 = 0; i5 < i; i5++) {
            i20 = i21 = i22 = i17 = i18 = i19 = i2 = i3 = i4 = 0;
            int i9 = -paramInt * i;
            for (int i7 = -paramInt; i7 <= paramInt; i7++) {
                i10 = Math.max(0, i9) + i5;
                arrayOfInt7 = arrayOfInt[(i7 + paramInt)];
                arrayOfInt7[0] = arrayOfInt2[i10];
                arrayOfInt7[1] = arrayOfInt3[i10];
                arrayOfInt7[2] = arrayOfInt4[i10];
                i15 = i16 - Math.abs(i7);
                i2 += arrayOfInt2[i10] * i15;
                i3 += arrayOfInt3[i10] * i15;
                i4 += arrayOfInt4[i10] * i15;
                if (i7 > 0) {
                    i20 += arrayOfInt7[0];
                    i21 += arrayOfInt7[1];
                    i22 += arrayOfInt7[2];
                } else {
                    i17 += arrayOfInt7[0];
                    i18 += arrayOfInt7[1];
                    i19 += arrayOfInt7[2];
                }
                if (i7 >= m)
                    continue;
                i9 += i;
            }
            i10 = i5;
            i13 = paramInt;
            for (int i6 = 0; i6 < j; i6++) {
                arrayOfInt1[i10] = (0xFF000000 & arrayOfInt1[i10]
                        | arrayOfInt6[i2] << 16 | arrayOfInt6[i3] << 8 | arrayOfInt6[i4]);
                i2 -= i17;
                i3 -= i18;
                i4 -= i19;
                i14 = i13 - paramInt + i1;
                arrayOfInt7 = arrayOfInt[(i14 % i1)];
                i17 -= arrayOfInt7[0];
                i18 -= arrayOfInt7[1];
                i19 -= arrayOfInt7[2];
                if (i5 == 0)
                    arrayOfInt5[i6] = (Math.min(i6 + i16, m) * i);
                i8 = i5 + arrayOfInt5[i6];
                arrayOfInt7[0] = arrayOfInt2[i8];
                arrayOfInt7[1] = arrayOfInt3[i8];
                arrayOfInt7[2] = arrayOfInt4[i8];
                i20 += arrayOfInt7[0];
                i21 += arrayOfInt7[1];
                i22 += arrayOfInt7[2];
                i2 += i20;
                i3 += i21;
                i4 += i22;
                i13 = (i13 + 1) % i1;
                arrayOfInt7 = arrayOfInt[i13];
                i17 += arrayOfInt7[0];
                i18 += arrayOfInt7[1];
                i19 += arrayOfInt7[2];
                i20 -= arrayOfInt7[0];
                i21 -= arrayOfInt7[1];
                i22 -= arrayOfInt7[2];
                i10 += i;
            }
        }
        localBitmap.setPixels(arrayOfInt1, 0, i, 0, 0, i, j);
        return localBitmap;
    }
}
