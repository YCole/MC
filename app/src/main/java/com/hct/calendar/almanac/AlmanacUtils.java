package com.hct.calendar.almanac;

import android.text.format.Time;
import android.util.Log;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;

public class AlmanacUtils {
    private static final String TAG = "AlmanacUtils";
    private static final String[] JIA_ZI = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.jiazi);
    private static final String[] SHENG_XIAO = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.shengxiao);
    private final static int MEASURE_YEAR = 1924;
    private static final String[] TIAN_GAN = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.tiangan);
    private static final String[] DI_ZHI = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.dizhi);

    private static final String[] SHI_CHENG = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.shicheng);

    private static final String[] FANG_WEI = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.fangwei);

    public static String calcYearGanZhi(int lunarYear) {
        return JIA_ZI[Math.abs(lunarYear - MEASURE_YEAR) % 60];
    }

    public static String calcYearShengXiao(int lunarYear) {
        return SHENG_XIAO[Math.abs(lunarYear - MEASURE_YEAR) % 12];
    }

    public static String calcAlmanacYear(int lunarYear) {
        int diff = Math.abs(lunarYear - MEASURE_YEAR);
        return JIA_ZI[diff % 60] + SHENG_XIAO[diff % 12];
    }

    public static String[] getShiCheng() {
        return SHI_CHENG.clone();
    }

    public static String[] getFangWei() {
        return FANG_WEI.clone();
    }

    public static String getFangWei(int index) {
        return FANG_WEI[index];
    }

    public static String calcMonthGanZhi(Time lunarTime) {
        return calcMonthGanZhi(lunarTime.year, lunarTime.month,
                lunarTime.monthDay);
    }

    private static final int[] YEAR_MAP_MONTH_GANZHI = { 2, 4, 6, 8, 0, 2, 4,
            6, 8, 0 };

    public static String calcMonthGanZhi(int year, int month, int day) {
        int diff = Math.abs(year - MEASURE_YEAR);
        int ganIndex = diff % 10;
        int monthGanIndex = YEAR_MAP_MONTH_GANZHI[ganIndex];
        return TIAN_GAN[(month + monthGanIndex) % 10]
                + DI_ZHI[(month + 2) % 12];
    }

    private static boolean isLeap(final int nYear) {
        return (nYear % 4 == 0) && ((nYear % 100 != 0) || (nYear % 400 == 0));
    }

    public static int calcLeapNum(int sYear, int eYear) {
        int leapS = sYear / 4 - sYear / 100 + sYear / 400;
        int leapE = eYear / 4 - eYear / 100 + eYear / 400;
        int num = leapE - leapS;
        if (isLeap(eYear)) {
            num = num - 1;
        }
        return Math.abs(num);
    }

    public static GanZhiIndex calcNewYearDayGanZhi(int year) {
        int x = (year - 2001);
        int offset = 365 * x;
        int leapNum = calcLeapNum(2001, year);
        int gan = offset % 10 + leapNum;
        int zhi = offset % 12 + leapNum;
        return GanZhiIndex.create(gan % 10, zhi % 12);
    }

    private static final int[] MONTH_TIAN_GAN = { 0, 0, 8, 9, 9, 0, 0, 1, 2, 2,
            3, 3 };
    private static final int[] MONTH_DI_ZHI = { 0, 6, 10, 5, 11, 6, 0, 7, 2, 8,
            3, 9 };

    public static GanZhiIndex calcDayGanZhi(int year, int month, int day) {
        GanZhiIndex yGz = calcNewYearDayGanZhi(year);
        int leapOffset = 0;
        if (month >= 3) {
            leapOffset = isLeap(year) ? 1 : 0;
        }
        int dayGan = (yGz.ganIndex + day + MONTH_TIAN_GAN[month] + leapOffset) % 10;
        int dayZhi = (yGz.zhiIndex + day + MONTH_DI_ZHI[month] + leapOffset) % 12;
        return GanZhiIndex.create(dayGan % 10 == 0 ? 9 : dayGan % 10 - 1,
                dayZhi % 12 == 0 ? 11 : dayZhi % 12 - 1);
    }

    public static GanZhiIndex calcDayGanZhi2(Time solarTime) {
        return calcDayGanZhi2(solarTime.year, solarTime.month,
                solarTime.monthDay);
    }

    public static GanZhiIndex calcDayGanZhi2(int year, int month, int day) {
        int shiji = calcCentury(year) - 1;
        int y = year % 100;
        int off = (month + 1) % 2 == 0 ? 6 : 0;
        if (month < 2) {
            month = month + 13;
            y = y - 1;
        }
        int g = shiji * 4 + shiji / 4 + y * 5 + y / 4 + 3 * (month + 1 + 1) / 5
                + day - 3;
        int z = shiji * 8 + shiji / 4 + y * 5 + y / 4 + 3 * (month + 1 + 1) / 5
                + day + 7 + off;
        GanZhiIndex yGz = GanZhiIndex.create(g % 10 == 0 ? 9 : g % 10 - 1,
                z % 12 == 0 ? 11 : z % 12 - 1);
        return yGz;
    }

    private static final int[] MONTH_DAY_TIAN_GAN = { 0, 2, 4, 6, 8, 0, 2, 4,
            6, 8 };

    public static int calcCentury(int year) {
        return (year / 100) + 1;
    }

    public static class GanZhiIndex {
        public static GanZhiIndex create(int g, int z) {
            return new GanZhiIndex(g, z);
        }

        public GanZhiIndex(int g, int z) {
            ganIndex = g;
            zhiIndex = z;
        }

        public int ganIndex;
        public int zhiIndex;

        @Override
        public String toString() {
            return TIAN_GAN[ganIndex] + DI_ZHI[zhiIndex];
        }

        public void print() {
            Log.v(TAG, String.format(">>>%s%s", TIAN_GAN[ganIndex],
                    DI_ZHI[zhiIndex]));
        }
    }
}
