/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.chinesefestivalalert;

import java.util.Calendar;

import android.text.format.Time;
import android.util.Log;

import com.hct.dom.DomFeedParser;
import com.hct.dom.Rawdata;

/**
 * @author:zhangwei Fuction:Convert date between solar and chinese lunar
 *                  calendar
 */
public class ChineseFestivalRawData {
    private static final String TAG = "ChineseFestivalRawData";
    // 2013091806415
    // frist 8 bits alarmManeger time
    // 9bit-11bit set alarming period
    // 12bit whether need notify users{1 notify,0 ignore}
    // 13bit flag show which festival {yuandan chunjie qingming wuyi duanwu
    // zhongqiu guoqing}
    // {0 1 2 3 4 5 6}
    public static int initialYear = 2016;
    private static String rawdata[][] = {

            // last year Dec
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Jan
            { "1", "1", "1", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Feb
            { "3", "3", "3", "3", "3", "2", "1", "1", "1", "1", "1", "1", "1",
                    "2", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3" },
            // Mar
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // April
            { "3", "1", "1", "1", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "1" },
            // May
            { "1", "1", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // June
            { "3", "3", "3", "3", "3", "3", "3", "3", "1", "1", "1", "2", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3" },
            // July
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Aug
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Sep
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "1", "1", "1", "2", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3" },
            // Oct
            { "1", "1", "1", "1", "1", "1", "1", "2", "2", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Nov
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", },
            // Dec
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // year label
            { String.valueOf(initialYear) }, };
    // this dada show nothing
    private static String emptyData[][] = {

            // last year Dec
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Jan
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Feb
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3" },
            // Mar
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // April
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", },
            // May
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // June
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3" },
            // July
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Aug
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Sep
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3" },
            // Oct
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // Nov
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", },
            // Dec
            { "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3",
                    "3", "3", "3", "3", "3", "3" },
            // year label
            { "1982" }, };
    static {
        refrashData();
    }

    public static String[] Year;

    public static String[] Month01;
    public static String[] Month02;
    public static String[] Month03;
    public static String[] Month04;
    public static String[] Month05;
    public static String[] Month06;
    public static String[] Month07;
    public static String[] Month08;
    public static String[] Month09;
    public static String[] Month10;
    public static String[] Month11;
    public static String[] Month12;
    public static String[] lastMonth12;

    public static boolean isInitiYear() {
        Time t = new Time();
        t.setToNow();
        if (t.year == initialYear
                && ((t.month < Calendar.DECEMBER) || (t.month == Calendar.DECEMBER)
                        && t.monthDay < basicfounction.DOWNLOAD_BEGIN_DAY)) {
            return true;
        } else if (t.year + 1 == initialYear
                && (t.month == Calendar.DECEMBER && t.monthDay >= basicfounction.DOWNLOAD_DEANLINE_DAY)) {
            return true;
        } else {
            Log.v(TAG, "HCT:not initialYear");
            return false;
        }
    }

    public static void refrashData() {
        boolean isInitYear = isInitiYear();
        boolean isFileExist = false;
        Rawdata rawData = null;
        if (!isInitYear && (isFileExist = DomFeedParser.isFileExist())) {
            rawData = DomFeedParser.parse();
            // Log.d(TAG, "rawData = " + rawData);
            // Log.d(TAG, "rawData.YEAR = " + rawData.YEAR);
            if ((rawData == null) || (rawData.YEAR == null)) {
                isFileExist = false;
                Log.d(TAG, "isFileExist = " + isFileExist);
            }
        }
        Year = isInitYear ? rawdata[13] : (isFileExist ? rawData.YEAR
                : emptyData[13]);
        Month01 = isInitYear ? rawdata[1] : (isFileExist ? rawData.Month01
                : emptyData[1]);
        Month02 = isInitYear ? rawdata[2] : (isFileExist ? rawData.Month02
                : emptyData[2]);
        Month03 = isInitYear ? rawdata[3] : (isFileExist ? rawData.Month03
                : emptyData[3]);
        Month04 = isInitYear ? rawdata[4] : (isFileExist ? rawData.Month04
                : emptyData[4]);
        Month05 = isInitYear ? rawdata[5] : (isFileExist ? rawData.Month05
                : emptyData[5]);
        Month06 = isInitYear ? rawdata[6] : (isFileExist ? rawData.Month06
                : emptyData[6]);
        Month07 = isInitYear ? rawdata[7] : (isFileExist ? rawData.Month07
                : emptyData[7]);
        Month08 = isInitYear ? rawdata[8] : (isFileExist ? rawData.Month08
                : emptyData[8]);
        Month09 = isInitYear ? rawdata[9] : (isFileExist ? rawData.Month09
                : emptyData[9]);
        Month10 = isInitYear ? rawdata[10] : (isFileExist ? rawData.Month10
                : emptyData[10]);
        Month11 = isInitYear ? rawdata[11] : (isFileExist ? rawData.Month11
                : emptyData[11]);
        Month12 = isInitYear ? rawdata[12] : (isFileExist ? rawData.Month12
                : emptyData[12]);
        lastMonth12 = isInitYear ? rawdata[0]
                : (isFileExist ? rawData.lastMonth12 : emptyData[0]);
    }
}
