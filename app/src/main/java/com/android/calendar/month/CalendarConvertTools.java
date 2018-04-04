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

package com.android.calendar.month;

import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;
import android.util.Log;

import com.android.calendar.R;
import com.android.calendar.Utils;

/**
 * @author:duanqingpeng Fuction:Convert date between solar and chinese lunar
 *                      calendar
 */
public class CalendarConvertTools {
    public static final int YEAR_START_INT = 1900;
    public static final int YEAR_LUNAR_MAX = 2050;
    public static final int IMPLY_NO_LEAP_MONTH_INT = 13;
    /*
     * The info of term from 1901--2050,high 4 bit indicate the interval days
     * between first term date and the 15th day in a month. /*the low 4 bit
     * indicate the interval days between second term date and the 15th day in a
     * month.
     */
    public static final char[] TERM_INFO_CHAR_ARRAY = {
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1901
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x87,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1902
            0x96,
            0xA5,
            0x87,
            0x96,
            0x87,
            0x87,
            0x79,
            0x69,
            0x69,
            0x69,
            0x78,
            0x78, // 1903
            0x86,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x78,
            0x87, // 1904
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1905
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1906
            0x96,
            0xA5,
            0x87,
            0x96,
            0x87,
            0x87,
            0x79,
            0x69,
            0x69,
            0x69,
            0x78,
            0x78, // 1907
            0x86,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1908
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1909
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1910
            0x96,
            0xA5,
            0x87,
            0x96,
            0x87,
            0x87,
            0x79,
            0x69,
            0x69,
            0x69,
            0x78,
            0x78, // 1911
            0x86,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1912
            0x95,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1913
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1914
            0x96,
            0xA5,
            0x97,
            0x96,
            0x97,
            0x87,
            0x79,
            0x79,
            0x69,
            0x69,
            0x78,
            0x78, // 1915
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1916
            0x95,
            0xB4,
            0x96,
            0xA6,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x87, // 1917
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1918
            0x96,
            0xA5,
            0x97,
            0x96,
            0x97,
            0x87,
            0x79,
            0x79,
            0x69,
            0x69,
            0x78,
            0x78, // 1919
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1920
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x87, // 1921
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1922
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x87,
            0x79,
            0x79,
            0x69,
            0x69,
            0x78,
            0x78, // 1923
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1924
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x87, // 1925
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1926
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x87,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1927
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1928
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1929
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1930
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x87,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1931
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1932
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1933
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1934
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1935
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1936
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1937
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1938
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1939
            0x96,
            0xA5,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1940
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1941
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1942
            0x96,
            0xA4,
            0x96,
            0x96,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1943
            0x96,
            0xA5,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1944
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1945
            0x95,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x77, // 1946
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1947
            0x96,
            0xA5,
            0xA6,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 1948
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x79,
            0x78,
            0x79,
            0x77,
            0x87, // 1949
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x77, // 1950
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x79,
            0x79,
            0x79,
            0x69,
            0x78,
            0x78, // 1951
            0x96,
            0xA5,
            0xA6,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 1952
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1953
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x68,
            0x78,
            0x87, // 1954
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1955
            0x96,
            0xA5,
            0xA5,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 1956
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1957
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1958
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1959
            0x96,
            0xA4,
            0xA5,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1960
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1961
            0x96,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1962
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1963
            0x96,
            0xA4,
            0xA5,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1964
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1965
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1966
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1967
            0x96,
            0xA4,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1968
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1969
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1970
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x79,
            0x69,
            0x78,
            0x77, // 1971
            0x96,
            0xA4,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1972
            0xA5,
            0xB5,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1973
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1974
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x97,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x77, // 1975
            0x96,
            0xA4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x88,
            0x89,
            0x88,
            0x78,
            0x87,
            0x87, // 1976
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 1977
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x78,
            0x87, // 1978
            0x96,
            0xB4,
            0x96,
            0xA6,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x77, // 1979
            0x96,
            0xA4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1980
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 1981
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1982
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x78,
            0x79,
            0x78,
            0x69,
            0x78,
            0x77, // 1983
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1984
            0xA5,
            0xB4,
            0xA6,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 1985
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1986
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x79,
            0x78,
            0x69,
            0x78,
            0x87, // 1987
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 1988
            0xA5,
            0xB4,
            0xA5,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1989
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 1990
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1991
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 1992
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1993
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1994
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1995
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 1996
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 1997
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 1998
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 1999
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2000
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2001
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 2002
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 2003
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2004
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2005
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2006
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x69,
            0x78,
            0x87, // 2007
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x87,
            0x78,
            0x87,
            0x86, // 2008
            0xA5,
            0xB3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2009
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2010
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x78,
            0x87, // 2011
            0x96,
            0xB4,
            0xA5,
            0xB5,
            0xA5,
            0xA6,
            0x87,
            0x88,
            0x87,
            0x78,
            0x87,
            0x86, // 2012
            0xA5,
            0xB3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2013
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2014
            0x95,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x97,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 2015
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x87,
            0x88,
            0x87,
            0x78,
            0x87,
            0x86, // 2016
            0xA5,
            0xC3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2017
            0xA5,
            0xB4,
            0xA6,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2018
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 2019
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x78,
            0x87,
            0x86, // 2020
            0xA5,
            0xC3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2021
            0xA5,
            0xB4,
            0xA5,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2022
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x79,
            0x77,
            0x87, // 2023
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x78,
            0x87,
            0x96, // 2024
            0xA5,
            0xC3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2025
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2026
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 2027
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x78,
            0x87,
            0x96, // 2028
            0xA5,
            0xC3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2029
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2030
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0x96,
            0x96,
            0x88,
            0x78,
            0x78,
            0x78,
            0x87,
            0x87, // 2031
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x78,
            0x87,
            0x96, // 2032
            0xA5,
            0xC3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2033
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2034
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2035
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x78,
            0x87,
            0x96, // 2036
            0xA5,
            0xC3,
            0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x86, // 2037
            0xA5,
            0xB3,
            0xA5,
            0xA5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2038
            0xA5,
            0xB4,
            0x96,
            0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2039
            0x95,
            0xB4,
            0xA5,
            0xB4,
            0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x78,
            0x87,
            0x96, // 2040
            0xA5, 0xC3,
            0xA5,
            0xB5,
            0xA5,
            0xA6,
            0x87,
            0x88,
            0x87,
            0x78,
            0x87,
            0x86, // 2041
            0xA5, 0xB3, 0xA5,
            0xB5,
            0xA6,
            0xA6,
            0x88,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2042
            0xA5, 0xB4, 0x96, 0xA5,
            0xA6,
            0x96,
            0x88,
            0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2043
            0x95, 0xB4, 0xA5, 0xB4, 0xA5,
            0xA6,
            0x97,
            0x87,
            0x87,
            0x88,
            0x87,
            0x96, // 2044
            0xA5, 0xC3, 0xA5, 0xB4, 0xA5, 0xA6,
            0x87,
            0x88,
            0x87,
            0x78,
            0x87,
            0x86, // 2045
            0xA5, 0xB3, 0xA5, 0xB5, 0xA6, 0xA6, 0x87,
            0x88,
            0x88,
            0x78,
            0x87,
            0x87, // 2046
            0xA5, 0xB4, 0x96, 0xA5, 0xA6, 0x96, 0x88, 0x88,
            0x78,
            0x78,
            0x87,
            0x87, // 2047
            0x95, 0xB4, 0xA5, 0xB4, 0xA5, 0xA5, 0x97, 0x87, 0x87,
            0x88,
            0x86,
            0x96, // 2048
            0xA4, 0xC3, 0xA5, 0xA5, 0xA5, 0xA6, 0x97, 0x87, 0x87, 0x78,
            0x87,
            0x86, // 2049
            0xA5, 0xC3, 0xA5, 0xB5, 0xA6, 0xA6, 0x87, 0x88, 0x78, 0x78, 0x87,
            0x87 // 2050
    };
    /*
     * Low 5 bit indicate fes monthday,mid 4 bit indicate fes month,high 7 bit
     * indicate fes string index
     */
    public static final short[] CHN_FES_INFO_INT_ARRAY = { 0x0188, 0x039E,
            0x0421, 0x062F, 0x08E7, 0x0AEF, 0x0D0F, 0x0F29, 0x10A5 };
    public static final short[] CHN_HOL_INFO_INT_ARRAY = { 0x0021, 0x0268,
            0x046C, 0x06A1, 0x08C1, 0x0AE1, 0x0D01, 0x0F2A, 0x1141, 0x1399,
            0x14AC, 0x16A4, 0x184E, 0x1B5F };
    /*
     * Including the lunar year info from 1900 to 2059 (160 Years).The units and
     * tens of element in LunarYearInfo indicate the interval days between lunar
     * start day and solar start day in one year.The hundreds indicats whether
     * one year has a leaf month.The kilobits and myriabit indicats the leap
     * month number if one year has a leaf month(13 indicats no leap month).
     */
    public static final int[] LUNAR_YEAR_INFO_INT_ARRAY = { 8130, 13049, 13038,
            5128, 13046, 13034, 4224, 13043, 13032, 2121, 13040, 6129, 13048,
            13036, 5225, 13044, 13034, 2122, 13041, 7231, 13050, 13038, 5127,
            13046, 13035, 4223, 13043, 13032, 2122, 13040, 6129, 13047, 13036,
            5125, 13044, 13034, 3223, 13041, 7230, 13049, 13038, 6226, 13045,
            13035, 4224, 13043, 13032, 2121, 13040, 7128, 13047, 13036, 5226,
            13044, 13033, 3223, 13042, 8130, 13048, 13038, 6127, 13045, 13035,
            4124, 13043, 13032, 3120, 13039, 7129, 13047, 13036, 5126, 13045,
            13033, 4122, 13041, 8130, 13048, 13037, 6227, 13046, 13035, 4124,
            13043, 10132, 13050, 13039, 6128, 13047, 13036, 5126, 13045, 13034,
            3122, 13040, 8130, 13049, 13037, 5127, 13046, 13035, 4123, 13042,
            13031, 2121, 13039, 7128, 13048, 13037, 5125, 13044, 13033, 4122,
            13040, 9130, 13049, 13038, 6227, 13046, 13035, 4124, 13042, 13031,
            2121, 13040, 6128, 13047, 13036, 5125, 13043, 13033, 3122, 13041,
            11130, 13049, 13038, 6227, 13045, 13034, 5123, 13042, 13031, 2121,
            13040, 7129, 13047, 13036, 5225, 13044, 13032, 3222, 13041, 8131,
            13049, 13038, 6227, 13045, 13034, 4223, 13042 };

    /*
     * Including the lunar year info from 1900 to 2059.Every element in
     * LunarYearMonthInfo indicatss the greater and small month info of one
     * year.
     */
    public static final int[] LUNAR_MONTH_INFO_INT_ARRAY = { 1213, 1198, 2647,
            1357, 3366, 3477, 1749, 1386, 2477, 1373, 1198, 2651, 2637, 3365,
            3365, 2900, 1386, 2778, 2395, 1175, 1175, 2635, 2891, 1701, 1748,
            2741, 694, 2391, 1327, 1175, 1623, 3402, 3402, 1769, 1453, 694,
            2158, 2350, 3213, 3221, 3402, 3466, 2901, 1130, 2651, 605, 2349,
            3371, 2709, 2901, 1738, 2901, 1333, 1210, 2651, 1111, 1323, 2714,
            3733, 1706, 2794, 2741, 1206, 2734, 2647, 1318, 3878, 3477, 1461,
            1386, 2413, 1245, 1197, 2637, 3405, 3365, 3413, 2900, 2922, 2394,
            2395, 1179, 2711, 2635, 2855, 1701, 1748, 2804, 2742, 2359, 1199,
            1175, 1611, 1866, 3749, 1717, 1452, 2742, 2413, 2350, 3222, 3477,
            3402, 3493, 1877, 1386, 2747, 605, 2349, 3243, 2709, 2890, 2986,
            2773, 1373, 1210, 2651, 1303, 1323, 2707, 1941, 1706, 2773, 1461,
            1206, 2670, 2638, 3366, 3750, 3411, 1450, 1898, 2413, 1199, 1197,
            2637, 3339, 3365, 3412, 3540, 2906, 1389, 1371, 1179, 2647, 2635,
            2725, 2853, 1746, 2778, 1206, 2359, 1183, 1175, 1611, 1674, 3749,
            1714, 2668, 2734 };

    private/* static */String[] mTianGan = null;
    private/* static */String[] mDiZhi = null;
    private/* static */String[] mLunarSpecialString = null;
    private/* static */String[] mLunarNumber = null;
    private String[] mChnFes = null;
    private String[] mChnFesDes = null;
    private String[] mChnHol = null;
    private String[] mChnHolDes = null;
    private String[] mTerms = null;
    private boolean mbIsInit = false;
    private Context mContext = null;

    public CalendarConvertTools(Context context) {
        mContext = context;
    }

    public String[] getFestivals() {
        InitValue();
        boolean isAbroad = Utils.isAbroadBranch(mContext);
        return isAbroad == true ? null : mChnFes;
    }

    public String[] getHolidays() {
        InitValue();
        return mChnHol;
    }

    public String[] getTerms() {
        InitValue();
        return mTerms;
    }

    private boolean InitValue() {
        if (mbIsInit) {
            return true;
        }
        Resources r = null;
        if (null != mContext) {
            r = mContext.getResources();
        } else {
            return false;
        }

        mTianGan = r.getStringArray(R.array.tian_gan_string_array);
        if (null == mTianGan) {
            return false;
        }

        mDiZhi = r.getStringArray(R.array.di_zhi_string_array);
        if (null == mDiZhi) {
            return false;
        }

        mLunarSpecialString = r
                .getStringArray(R.array.special_lunar_string_array);
        if (null == mLunarSpecialString) {
            return false;
        }

        mLunarNumber = r.getStringArray(R.array.lunar_num_string_array);
        if (null == mLunarNumber) {
            return false;
        }

        mChnFes = r.getStringArray(R.array.chn_fes_string_array);
        if (null == mChnFes) {
            return false;
        }

        mChnFesDes = r.getStringArray(R.array.chn_fesprompt_string_array);
        if (null == mChnFesDes) {
            return false;
        }

        mChnHol = r.getStringArray(R.array.chn_hol_string_array);
        if (null == mChnHol) {
            return false;
        }

        mChnHolDes = r.getStringArray(R.array.chn_holprompt_string_array);
        if (null == mChnHolDes) {
            return false;
        }

        mTerms = r.getStringArray(R.array.term_string_array);
        if (null == mTerms) {
            return false;
        }

        mbIsInit = true;
        return true;
    }

    /*
     * Function:Get the chn lunar date string from gving solar date Param:
     * tmSolarDate:The input solar date Return:The lunar date string
     */
    public String getLunarStringFromSolar(final Time tmSolarDate) {
        if (null == tmSolarDate) {
            return null;
        }
        if (!InitValue()) {
            return "";
        }
        Time tmLunarDate = getLunarDateFromSolar(tmSolarDate);
        return getLunarDateStringFromLunar(tmLunarDate);
    }

    /*
     * Function:Get the chinese lular date from giveing solar date Param:
     * tmSolarDate:The input solar date Return:The lunar date object
     */
    public Time getLunarDateFromSolar(final Time tmSolarDate) {
        Time tmLunarDate = new Time();

        if (null == tmSolarDate) {
            return null;
        }
        if (!InitValue()) {
            return null;
        }
        tmSolarDate.normalize(true);
        int nYear = tmSolarDate.year;
        int nMonth = tmSolarDate.month;
        int nDay = tmSolarDate.monthDay;

        int[] LunarMonthDayCount = new int[12];
        // Get the count of the days in every solar month of the giving year.
        int[] DaysInSolarMonth = new int[12];
        DaysInSolarMonth[0] = DaysInSolarMonth[2] = DaysInSolarMonth[4] = DaysInSolarMonth[6] = DaysInSolarMonth[7] = DaysInSolarMonth[9] = DaysInSolarMonth[11] = 31;

        DaysInSolarMonth[3] = DaysInSolarMonth[5] = DaysInSolarMonth[8] = DaysInSolarMonth[10] = 30;

        if (isLeap(nYear)) {
            DaysInSolarMonth[1] = 29;
        } else {
            DaysInSolarMonth[1] = 28;
        }

        int nDaysCountInlunarLeapMonth = getLunarMonthInfo(nYear,
                LunarMonthDayCount);

        int nLunarYearInfo = LUNAR_YEAR_INFO_INT_ARRAY[nYear - YEAR_START_INT];
        int nIntervalDaySToL = nLunarYearInfo % 100;
        int nLunarLeapMonth = nLunarYearInfo / 1000;

        int nDaysFromStartOfYear = 0;

        // Get the solar interval days from start day of this year to now
        for (int i = 0; i <= nMonth - 1; i++) {
            nDaysFromStartOfYear += DaysInSolarMonth[i];
        }
        nDaysFromStartOfYear += nDay;
        // Get the lunar interval days from start day of this year to now
        nDaysFromStartOfYear -= nIntervalDaySToL;
        int i = 0;
        // Last lunar year
        if (nDaysFromStartOfYear <= 0) {
            int[] LunarMonthDayCountLastYear = new int[12];
            nDaysCountInlunarLeapMonth = getLunarMonthInfo(nYear - 1,
                    LunarMonthDayCountLastYear);
            nLunarYearInfo = LUNAR_YEAR_INFO_INT_ARRAY[nYear - 1
                    - YEAR_START_INT];
            nLunarLeapMonth = nLunarYearInfo / 1000;

            // If there is no leap month last year
            if (IMPLY_NO_LEAP_MONTH_INT == nLunarLeapMonth) {
                nLunarLeapMonth = 0;
            }

            for (i = 12; i > nLunarLeapMonth; i--) {
                nDaysFromStartOfYear += LunarMonthDayCountLastYear[i - 1];
                if (nDaysFromStartOfYear > 0)
                    break;
            }
            // After the leap month
            if (nDaysFromStartOfYear > 0) {
                tmLunarDate.year = nYear - 1;
                tmLunarDate.month = i;
                tmLunarDate.monthDay = nDaysFromStartOfYear;
            } else {
                nDaysFromStartOfYear += nDaysCountInlunarLeapMonth;
                // Is the leap month
                if (nDaysFromStartOfYear > 0) {
                    tmLunarDate.year = nYear - 1;
                    tmLunarDate.month = -nLunarLeapMonth;
                    tmLunarDate.monthDay = nDaysFromStartOfYear;
                } else {
                    for (i = nLunarLeapMonth; i > 0; i--) {
                        nDaysFromStartOfYear += LunarMonthDayCountLastYear[i - 1];
                        if (nDaysFromStartOfYear > 0) {
                            break;
                        }
                    }
                    if (nDaysFromStartOfYear > 0) {
                        tmLunarDate.year = nYear - 1;
                        tmLunarDate.month = i;
                        tmLunarDate.monthDay = nDaysFromStartOfYear;
                    }
                }
            }
        }
        // This lunar year
        else {
            for (i = 1; i < nLunarLeapMonth; i++) {
                nDaysFromStartOfYear -= LunarMonthDayCount[i - 1];
                if (nDaysFromStartOfYear <= 0) {
                    break;
                }
            }
            // Before leap month
            if (nDaysFromStartOfYear <= 0) {
                tmLunarDate.year = nYear;
                tmLunarDate.month = i;
                tmLunarDate.monthDay = LunarMonthDayCount[i - 1]
                        + nDaysFromStartOfYear;
            } else {
                nDaysFromStartOfYear -= LunarMonthDayCount[nLunarLeapMonth - 1];
                if (nDaysFromStartOfYear <= 0) {
                    tmLunarDate.year = nYear;
                    tmLunarDate.month = nLunarLeapMonth;
                    tmLunarDate.monthDay = LunarMonthDayCount[nLunarLeapMonth - 1]
                            + nDaysFromStartOfYear;
                } else {
                    LunarMonthDayCount[nLunarLeapMonth - 1] = nDaysCountInlunarLeapMonth;
                    for (i = nLunarLeapMonth; i <= 12; i++) {
                        nDaysFromStartOfYear -= LunarMonthDayCount[i - 1];
                        if (nDaysFromStartOfYear <= 0) {
                            break;
                        }
                    }
                    // Is leap month
                    if (i == nLunarLeapMonth) {
                        tmLunarDate.month = -nLunarLeapMonth;
                    } else {
                        tmLunarDate.month = i;
                    }
                    tmLunarDate.year = nYear;
                    tmLunarDate.monthDay = LunarMonthDayCount[i - 1]
                            + nDaysFromStartOfYear;
                }
            }
        }
        return tmLunarDate;
        // return getLunarDateString(tmLunarDate);
    }

    /*
     * Function:If the giving year is a leapyear Param: nYear:The giving year
     * Return: true:Is a leap year false:Not a leap year
     */
    private boolean isLeap(final int nYear) {
        return (nYear % 4 == 0) && ((nYear % 100 != 0) || (nYear % 400 == 0));
    }

    /*
     * Function:Get the count of days in month(include leap month) Param:
     */
    private int getLunarMonthInfo(final int nYear, int[] MonthInfo) {
        final int LEAP_MONTH_DAY1 = 29, LEAP_MONTH_DAY2 = 30;
        int nLeapMonthDayCount = 0;
        if (null == MonthInfo) {
            return nLeapMonthDayCount;
        }
        int nLunarYearInfo = LUNAR_YEAR_INFO_INT_ARRAY[nYear - YEAR_START_INT];
        int nLunarMonthInfoInYear = LUNAR_MONTH_INFO_INT_ARRAY[nYear
                - YEAR_START_INT];
        int nTemp = nLunarMonthInfoInYear, nModeResult = 0;
        for (int i = 11; i >= 0; i--) {
            nModeResult = nTemp % 2;
            if (nModeResult == 0) {
                MonthInfo[i] = LEAP_MONTH_DAY1;
            } else {
                MonthInfo[i] = LEAP_MONTH_DAY2;
            }
            nTemp = nTemp / 2;
        }
        nTemp = (nLunarYearInfo % 1000) / 100;

        switch (nTemp) {
        case 0: {
            nLeapMonthDayCount = 0;
            break;
        }
        case 1: {
            nLeapMonthDayCount = LEAP_MONTH_DAY1;
            break;
        }
        case 2: {
            nLeapMonthDayCount = LEAP_MONTH_DAY2;
            break;
        }
        }
        return nLeapMonthDayCount;
    }

    public String getLunarStringWithoutYear(Time tmSolarDate) {
        if (null == tmSolarDate) {
            return null;
        }
        if (!InitValue()) {
            return null;
        }
        Time tmLunarDate = getLunarDateFromSolar(tmSolarDate);
        String stDateString = "";
        stDateString += getLunarMonthString(tmLunarDate);
        stDateString += getLunarDayString(tmLunarDate);
        return stDateString;
    }

    public String getLunarYearString(Time tmLunarDate) {
        if (null == tmLunarDate) {
            return "";
        }
        if (!InitValue()) {
            return "";
        }
        String strYear = "";
        int nOffset = tmLunarDate.year - YEAR_START_INT + 1;
        // Year
        int nTianGanIdx = (nOffset + 5) % 10;
        int nDiZhiIdx = (nOffset + 11) % 12;
        strYear += mTianGan[nTianGanIdx];
        strYear += mDiZhi[nDiZhiIdx];
        strYear += mLunarSpecialString[0];
        return strYear;
    }

    public String getLunarMonthString(Time tmLunarDate) {
        if (null == tmLunarDate) {
            return "";
        }
        if (!InitValue()) {
            return "";
        }
        String strMonth = "";
        if (tmLunarDate.month <= 0) {
            strMonth += mLunarSpecialString[1];
        }
        if (1 == tmLunarDate.month || -1 == tmLunarDate.month) {
            strMonth += mLunarSpecialString[2];
        } else if (12 == tmLunarDate.month) {
            strMonth += mLunarSpecialString[5];
        } else if (11 == tmLunarDate.month) {
            strMonth += mLunarSpecialString[6];
        } else {
            if (tmLunarDate.month > 0) {
                strMonth += mLunarNumber[tmLunarDate.month - 1];
            } else {
                strMonth += mLunarNumber[-tmLunarDate.month - 1];
            }
        }
        strMonth += mLunarSpecialString[3];
        return strMonth;
    }

    public String getLunarDayString(Time tmLunarDate) {
        if (null == tmLunarDate) {
            return "";
        }
        if (!InitValue()) {
            return "";
        }
        String strDay = "";
        // Day
        if (tmLunarDate.monthDay >= 1 && tmLunarDate.monthDay <= 10) {
            strDay += mLunarSpecialString[4];
        }
        strDay += mLunarNumber[tmLunarDate.monthDay - 1];
        return strDay;
    }

    /*
     * Function:Get the lunar format date string Param: tmLunarDate:The giving
     * lunar date Return:The format lunar date string
     */
    public String getLunarDateStringFromLunar(Time tmLunarDate) {
        if (null == tmLunarDate) {
            return "";
        }
        String strLunarDate = "";
        // Year
        /*
         * int nOffset = tmLunarDate.year - YEAR_START_INT + 1; int nTianGanIdx
         * = (nOffset + 5) % 10; int nDiZhiIdx = (nOffset + 11) % 12;
         * strLunarDate += mTianGan[nTianGanIdx]; strLunarDate +=
         * mDiZhi[nDiZhiIdx]; strLunarDate += mLunarSpecialString[0];
         */
        strLunarDate += getLunarYearString(tmLunarDate);
        // Month
        /*
         * if (tmLunarDate.month <= 0) { strLunarDate += mLunarSpecialString[1];
         * } if (1 == tmLunarDate.month || -1 == tmLunarDate.month) {
         * strLunarDate += mLunarSpecialString[2]; } else if (12 ==
         * tmLunarDate.month) { strLunarDate += mLunarSpecialString[5]; } else {
         * if (tmLunarDate.month > 0) { strLunarDate +=
         * mLunarNumber[tmLunarDate.month - 1]; } else { strLunarDate +=
         * mLunarNumber[-tmLunarDate.month - 1]; } } strLunarDate +=
         * mLunarSpecialString[3];
         */
        strLunarDate += getLunarMonthString(tmLunarDate);
        // Day
        /*
         * if (tmLunarDate.monthDay >=1 && tmLunarDate.monthDay <= 10) {
         * strLunarDate += mLunarSpecialString[4]; } strLunarDate +=
         * mLunarNumber[tmLunarDate.monthDay - 1];
         */
        strLunarDate += getLunarDayString(tmLunarDate);
        return strLunarDate;
    }

    /*
     * Function: Get Chn fes from solar dateParam:tmSolarDate:The solar date
     * return:The fes string.if not fes day,return empty string
     */
    public String getChineseFestivalFromSolar(Time tmSolarDate) {
        if (!InitValue()) {
            return "";
        }
        // Convert to lunar date
        Time tmLunarDate = getLunarDateFromSolar(tmSolarDate);

        if (tmLunarDate.month == 12 && tmLunarDate.monthDay == 29) {

            Time tmSolarDate2 = new Time();
            tmSolarDate2.set(tmSolarDate.toMillis(true));
            tmSolarDate2.monthDay = tmSolarDate2.monthDay + 1;
            tmSolarDate2.normalize(true);
            Time tmLunarDate2 = getLunarDateFromSolar(tmSolarDate2);
            if (tmLunarDate2.month == 1 && tmLunarDate2.monthDay == 1) {
                return mChnFes[1];
            }
        }

        return getChnFesFromLunar(tmLunarDate);
    }

    public String getChineseFestivalFromSolar(Time tmSolarDate,
            StringBuffer description) {
        if (!InitValue()) {
            return "";
        }
        // Convert to lunar date
        Time tmLunarDate = getLunarDateFromSolar(tmSolarDate);

        if (tmLunarDate.month == 12 && tmLunarDate.monthDay == 29) {

            Time tmSolarDate2 = new Time();
            tmSolarDate2.set(tmSolarDate.toMillis(true));
            tmSolarDate2.monthDay = tmSolarDate2.monthDay + 1;
            tmSolarDate2.normalize(true);
            Time tmLunarDate2 = getLunarDateFromSolar(tmSolarDate2);
            if (tmLunarDate2.month == 1 && tmLunarDate2.monthDay == 1) {
                description.append(mChnFesDes[1]);
                return mChnFes[1];
            }
        }

        return getChnFesFromLunar(tmLunarDate, description);
    }

    public String getChnFesFromLunar(Time tmLunarDate, StringBuffer description) {
        String strFes = "";
        final short DAY_MASK_SHROT = 0x001F, MONTH_MASK_SHROT = 0x000F;
        final int DAY_BIT_COUNT = 5, MONTH_BIT_COUNT = 4;
        if (null == tmLunarDate) {
            return "";
        }
        int nMonth = tmLunarDate.month;
        // It is leap month,has no festival
        if (0 > nMonth) {
            return "";
        }
        int nElementCount = CHN_FES_INFO_INT_ARRAY.length;
        // Check the fes info
        for (int nIdxFes = 0; nIdxFes < nElementCount; nIdxFes++) {
            short snFesInfo = CHN_FES_INFO_INT_ARRAY[nIdxFes];
            int nFesDay = snFesInfo & DAY_MASK_SHROT;
            int nFesMonth = (snFesInfo >> DAY_BIT_COUNT) & MONTH_MASK_SHROT;

            if (nFesDay != tmLunarDate.monthDay
                    || nFesMonth != tmLunarDate.month) {
                continue;
            }
            int nStringIndex = snFesInfo >> (DAY_BIT_COUNT + MONTH_BIT_COUNT);
            strFes = mChnFes[nStringIndex];
            description.append(mChnFesDes[nStringIndex]);
            break;
        }
        return strFes;
    }

    public String getChnFesFromLunar(Time tmLunarDate) {
        String strFes = "";
        final short DAY_MASK_SHROT = 0x001F, MONTH_MASK_SHROT = 0x000F;
        final int DAY_BIT_COUNT = 5, MONTH_BIT_COUNT = 4;
        if (null == tmLunarDate) {
            return "";
        }
        int nMonth = tmLunarDate.month;
        // It is leap month,has no festival
        if (0 > nMonth) {
            return "";
        }
        int nElementCount = CHN_FES_INFO_INT_ARRAY.length;
        // Check the fes info
        for (int nIdxFes = 0; nIdxFes < nElementCount; nIdxFes++) {
            short snFesInfo = CHN_FES_INFO_INT_ARRAY[nIdxFes];
            int nFesDay = snFesInfo & DAY_MASK_SHROT;
            int nFesMonth = (snFesInfo >> DAY_BIT_COUNT) & MONTH_MASK_SHROT;

            if (nFesDay != tmLunarDate.monthDay
                    || nFesMonth != tmLunarDate.month) {
                continue;
            }
            int nStringIndex = snFesInfo >> (DAY_BIT_COUNT + MONTH_BIT_COUNT);
            strFes = mChnFes[nStringIndex];
            break;
        }
        return strFes;
    }

    public String getChnHolFromSolor(Time tmSolarDate, StringBuffer description) {
        String strHol = "";
        final short DAY_MASK_SHROT = 0x001F, MONTH_MASK_SHROT = 0x000F;
        final int DAY_BIT_COUNT = 5, MONTH_BIT_COUNT = 4;
        if (null == tmSolarDate) {
            return "";
        }
        int nMonth = tmSolarDate.month;
        // It is leap month,has no festival
        if (0 > nMonth) {
            return "";
        }

        Resources r = mContext.getResources();

        if (nMonth == 4 && tmSolarDate.weekDay == 0
                && tmSolarDate.monthDay >= 8 && tmSolarDate.monthDay <= 14) {
            strHol = mChnHol[14];
            description.append(mChnHolDes[14]);
            Log.d("HolidayAdapter", "getChnHolFromSolor: description = "
                    + description);
            return strHol;
        } else if (nMonth == 5 && tmSolarDate.weekDay == 0
                && tmSolarDate.monthDay >= 15 && tmSolarDate.monthDay <= 21) {
            strHol = mChnHol[15];
            description.append(mChnHolDes[15]);
            Log.d("HolidayAdapter", "getChnHolFromSolor: description = "
                    + description);
            return strHol;
        } else if (nMonth == 10 && tmSolarDate.weekDay == 4
                && tmSolarDate.monthDay >= 22 && tmSolarDate.monthDay <= 28) {
            strHol = mChnHol[16];
            description.append(mChnHolDes[16]);
            Log.d("HolidayAdapter", "getChnHolFromSolor: description = "
                    + description);
            return strHol;
        }
        if ((nMonth == 2 && tmSolarDate.monthDay >= 22)
                || (nMonth == 3 && tmSolarDate.monthDay <= 25)) {
            Time tmTermDate = new Time();
            getTermDateByIndex(tmSolarDate.year, 6, tmTermDate);
            Time tmLunarDate = getLunarDateFromSolar(tmTermDate);
            if (tmLunarDate.monthDay < 15) {
                int IntervalDay = 15 - tmLunarDate.monthDay;
                tmTermDate.monthDay = tmTermDate.monthDay + IntervalDay;
            } else {
                tmTermDate.month = tmTermDate.month + 1;
                Time tmLunarDate2 = getLunarDateFromSolar(tmTermDate);
                int IntervalDay = tmLunarDate2.monthDay - 15;
                tmTermDate.monthDay = tmTermDate.monthDay - IntervalDay;
            }
            tmTermDate.normalize(true);
            if (tmTermDate.weekDay == 0) {
                tmTermDate.monthDay = tmTermDate.monthDay + 7;
            } else {
                tmTermDate.monthDay = tmTermDate.monthDay
                        + (7 - tmTermDate.weekDay);
            }
            tmTermDate.normalize(true);

            if (tmSolarDate.month == tmTermDate.month
                    && tmSolarDate.monthDay == tmTermDate.monthDay) {
                strHol = mChnHol[17];
                description.append(mChnHolDes[17]);
                Log.d("HolidayAdapter", "getChnHolFromSolor: description = "
                        + description);
                return strHol;
            }
        }
        // 20160329 add qingmingjie to Holiday View by zhxj
        if (tmSolarDate.month == Calendar.APRIL
                && (tmSolarDate.monthDay == 4 || tmSolarDate.monthDay == 5 || tmSolarDate.monthDay == 6)) {
            strHol = mChnHol[18];
            description.append(mChnHolDes[18]);
            Log.d("HolidayAdapter", "getChnHolFromSolor: description = "
                    + description);
            return strHol;
        }
        int nElementCount = CHN_HOL_INFO_INT_ARRAY.length;
        // Check the fes info
        for (int nIdxHol = 0; nIdxHol < nElementCount; nIdxHol++) {
            short snHolInfo = CHN_HOL_INFO_INT_ARRAY[nIdxHol];
            int nHolDay = snHolInfo & DAY_MASK_SHROT;
            int nHolMonth = (snHolInfo >> DAY_BIT_COUNT) & MONTH_MASK_SHROT;

            if (nHolDay != tmSolarDate.monthDay
                    || nHolMonth != tmSolarDate.month + 1) {
                continue;
            }
            int nStringIndex = snHolInfo >> (DAY_BIT_COUNT + MONTH_BIT_COUNT);
            strHol = mChnHol[nStringIndex];
            description.append(mChnHolDes[nStringIndex]);
            break;
        }
        return strHol;
    }

    public String getChnHolFromSolor(Time tmSolarDate) {
        String strHol = "";
        final short DAY_MASK_SHROT = 0x001F, MONTH_MASK_SHROT = 0x000F;
        final int DAY_BIT_COUNT = 5, MONTH_BIT_COUNT = 4;
        if (null == tmSolarDate) {
            return "";
        }
        int nMonth = tmSolarDate.month;
        // It is leap month,has no festival
        if (0 > nMonth) {
            return "";
        }

        Resources r = mContext.getResources();

        if (nMonth == 4 && tmSolarDate.weekDay == 0
                && tmSolarDate.monthDay >= 8 && tmSolarDate.monthDay <= 14) {
            strHol = mChnHol[14];
            return strHol;
        } else if (nMonth == 5 && tmSolarDate.weekDay == 0
                && tmSolarDate.monthDay >= 15 && tmSolarDate.monthDay <= 21) {
            strHol = mChnHol[15];
            return strHol;
        } else if (nMonth == 10 && tmSolarDate.weekDay == 4
                && tmSolarDate.monthDay >= 22 && tmSolarDate.monthDay <= 28) {
            strHol = mChnHol[16];
            return strHol;
        }
        if ((nMonth == 2 && tmSolarDate.monthDay >= 22)
                || (nMonth == 3 && tmSolarDate.monthDay <= 25)) {
            Time tmTermDate = new Time();
            getTermDateByIndex(tmSolarDate.year, 6, tmTermDate);
            Time tmLunarDate = getLunarDateFromSolar(tmTermDate);
            if (tmLunarDate.monthDay < 15) {
                int IntervalDay = 15 - tmLunarDate.monthDay;
                tmTermDate.monthDay = tmTermDate.monthDay + IntervalDay;
            } else {
                tmTermDate.month = tmTermDate.month + 1;
                Time tmLunarDate2 = getLunarDateFromSolar(tmTermDate);
                int IntervalDay = tmLunarDate2.monthDay - 15;
                tmTermDate.monthDay = tmTermDate.monthDay - IntervalDay;
            }
            tmTermDate.normalize(true);
            if (tmTermDate.weekDay == 0) {
                tmTermDate.monthDay = tmTermDate.monthDay + 7;
            } else {
                tmTermDate.monthDay = tmTermDate.monthDay
                        + (7 - tmTermDate.weekDay);
            }
            tmTermDate.normalize(true);

            if (tmSolarDate.month == tmTermDate.month
                    && tmSolarDate.monthDay == tmTermDate.monthDay) {
                strHol = mChnHol[17];
                return strHol;
            }
        }

        int nElementCount = CHN_HOL_INFO_INT_ARRAY.length;
        // Check the fes info
        for (int nIdxHol = 0; nIdxHol < nElementCount; nIdxHol++) {
            short snHolInfo = CHN_HOL_INFO_INT_ARRAY[nIdxHol];
            int nHolDay = snHolInfo & DAY_MASK_SHROT;
            int nHolMonth = (snHolInfo >> DAY_BIT_COUNT) & MONTH_MASK_SHROT;

            if (nHolDay != tmSolarDate.monthDay
                    || nHolMonth != tmSolarDate.month + 1) {
                continue;
            }
            int nStringIndex = snHolInfo >> (DAY_BIT_COUNT + MONTH_BIT_COUNT);
            strHol = mChnHol[nStringIndex];
            break;
        }
        return strHol;
    }

    private boolean getTermDateByIndex(final int nYear,
            final int nIdxTermOneYear, Time tmTermDate) {
        final int HALF_MONTH_INT = 15;
        final short FIRST_TERM_MASK_SHORT = 0x0F;
        boolean bReturn = true;
        if (null == tmTermDate) {
            return bReturn;
        }
        int nIdxTerm = (nIdxTermOneYear + 1) / 2 - 1 + 12
                * (nYear - YEAR_START_INT - 1);
        char cTermInfo = TERM_INFO_CHAR_ARRAY[nIdxTerm];
        // If first term
        if (1 == nIdxTermOneYear % 2) {
            tmTermDate.monthDay = HALF_MONTH_INT - (cTermInfo >> 4)
                    & (FIRST_TERM_MASK_SHORT);
            tmTermDate.month = (nIdxTermOneYear / 2) + 1;
            tmTermDate.year = nYear;
        }
        // If second term
        if (0 == nIdxTermOneYear % 2) {
            cTermInfo &= FIRST_TERM_MASK_SHORT;
            tmTermDate.monthDay = cTermInfo + HALF_MONTH_INT;
            tmTermDate.month = nIdxTermOneYear / 2;
            tmTermDate.year = nYear;
        }
        tmTermDate.month -= 1;
        tmTermDate.normalize(true);
        return bReturn;
    }

    public String getTermStringFromSolarDate(final Time tmSolarDate) {
        if (!InitValue()) {
            return "";
        }
        String strTermString = "";
        if (null == tmSolarDate) {
            return "";
        }
        tmSolarDate.normalize(true);
        int nMonth = tmSolarDate.month;

        nMonth += 1;
        // The first term of month
        int nTermIndex = 2 * nMonth - 1;
        Time tmTermDate = new Time();
        if (!getTermDateByIndex(tmSolarDate.year, nTermIndex, tmTermDate)) {
            return strTermString;
        }

        if (tmTermDate.year == tmSolarDate.year
                && tmTermDate.monthDay == tmSolarDate.monthDay
                && tmTermDate.month == tmSolarDate.month) {
            strTermString = mTerms[nTermIndex - 1];
            return strTermString;
        }

        // The second term of month
        nTermIndex = 2 * nMonth;
        if (!getTermDateByIndex(tmSolarDate.year, nTermIndex, tmTermDate)) {
            return strTermString;
        }

        if (tmTermDate.year == tmSolarDate.year
                && tmTermDate.monthDay == tmSolarDate.monthDay
                && tmTermDate.month == tmSolarDate.month) {
            strTermString = mTerms[nTermIndex - 1];
            return strTermString;
        }
        return strTermString;
    }
}
