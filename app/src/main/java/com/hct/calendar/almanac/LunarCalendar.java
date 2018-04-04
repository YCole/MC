package com.hct.calendar.almanac;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class LunarCalendar {
    private static int[] lunarInfo = { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5,
            0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0,
            0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,
            0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40,
            0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0,
            0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7,
            0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0,
            0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355,
            0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263,
            0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0,
            0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6, 0x095b0,
            0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46,
            0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
            0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954,
            0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0,
            0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0,
            0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50,
            0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6,
            0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0,
            0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0, 0x14b63,
            0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20,
            0x1a6c4, 0x0aae0, 0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0,
            0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4, 0x052d0, 0x0a9b8,
            0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0,
            0x052b0, 0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55,
            0x04b60, 0x0a570, 0x054e4, 0x0d160, 0x0e968, 0x0d520, 0x0daa0,
            0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,
            0x0d520 };
    private static int[] solarMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
            30, 31 };
    private static String[] Gan = { "\u7532", "\u4e59", "\u4e19", "\u4e01",
            "\u620a", "\u5df1", "\u5e9a", "\u8f9b", "\u58ec", "\u7678" };
    private static String[] Zhi = { "\u5b50", "\u4e11", "\u5bc5", "\u536f",
            "\u8fb0", "\u5df3", "\u5348", "\u672a", "\u7533", "\u9149",
            "\u620c", "\u4ea5" };
    private static String[] Animals = { "\u9f20", "\u725b", "\u864e", "\u5154",
            "\u9f99", "\u86c7", "\u9a6c", "\u7f8a", "\u7334", "\u9e21",
            "\u72d7", "\u732a" };
    private static String[] solarTerm = { "\u5c0f\u5bd2", "\u5927\u5bd2",
            "\u7acb\u6625", "\u96e8\u6c34", "\u60ca\u86f0", "\u6625\u5206",
            "\u6e05\u660e", "\u8c37\u96e8", "\u7acb\u590f", "\u5c0f\u6ee1",
            "\u8292\u79cd", "\u590f\u81f3", "\u5c0f\u6691", "\u5927\u6691",
            "\u7acb\u79cb", "\u5904\u6691", "\u767d\u9732", "\u79cb\u5206",
            "\u5bd2\u9732", "\u971c\u964d", "\u7acb\u51ac", "\u5c0f\u96ea",
            "\u5927\u96ea", "\u51ac\u81f3" };
    private static String[] sTermInfo = { "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c3598082c95f8c965cc920f",
            "97bd0b06bdb0722c965ce1cfcc920f", "b027097bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f",
            "97bd0b06bdb0722c965ce1cfcc920f", "b027097bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f",
            "97bd0b06bdb0722c965ce1cfcc920f", "b027097bd097c36b0b6fc9274c91aa",
            "9778397bd19801ec9210c965cc920e", "97b6b97bd19801ec95f8c965cc920f",
            "97bd09801d98082c95f8e1cfcc920f", "97bd097bd097c36b0b6fc9210c8dc2",
            "9778397bd197c36c9210c9274c91aa", "97b6b97bd19801ec95f8c965cc920e",
            "97bd09801d98082c95f8e1cfcc920f", "97bd097bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36c9210c9274c91aa", "97b6b97bd19801ec95f8c965cc920e",
            "97bcf97c3598082c95f8e1cfcc920f", "97bd097bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36c9210c9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c3598082c95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c3598082c95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c359801ec95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c359801ec95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c359801ec95f8c965cc920f", "97bd097bd07f595b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9210c8dc2", "9778397bd19801ec9210c9274c920e",
            "97b6b97bd19801ec95f8c965cc920f", "97bd07f5307f595b0b0bc920fb0722",
            "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c920e",
            "97b6b97bd19801ec95f8c965cc920f", "97bd07f5307f595b0b0bc920fb0722",
            "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bd07f1487f595b0b0bc920fb0722",
            "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f595b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f595b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f531b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c9274c920e", "97bcf7f0e47f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c91aa",
            "97b6b97bd197c36c9210c9274c920e", "97bcf7f0e47f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36c9210c9274c920e", "97b6b7f0e47f531b0723b0b6fb0722",
            "7f0e37f5307f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36b0b70c9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721",
            "7f0e37f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc9210c8dc2",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0787b0721",
            "7f0e27f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "9778397bd097c36b0b6fc9210c91aa", "97b6b7f0e47f149b0723b0787b0721",
            "7f0e27f0e47f531b0723b0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "9778397bd097c36b0b6fc9210c8dc2", "977837f0e37f149b0723b0787b0721",
            "7f07e7f0e47f531b0723b0b6fb0722", "7f0e37f5307f595b0b0bc920fb0722",
            "7f0e397bd097c35b0b6fc9210c8dc2", "977837f0e37f14998082b0787b0721",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e37f1487f595b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc9210c8dc2", "977837f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "977837f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "977837f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14998082b0787b06bd",
            "7f07e7f0e47f149b0723b0787b0721", "7f0e27f0e47f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14998082b0723b06bd",
            "7f07e7f0e37f149b0723b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14898082b0723b02d5",
            "7ec967f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722",
            "7f0e37f1487f595b0b0bb0b6fb0722", "7f0e37f0e37f14898082b0723b02d5",
            "7ec967f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722",
            "7f0e37f1487f531b0b0bb0b6fb0722", "7f0e37f0e37f14898082b0723b02d5",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e37f1487f531b0b0bb0b6fb0722", "7f0e37f0e37f14898082b072297c35",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e37f0e37f14898082b072297c35",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e37f0e366aa89801eb072297c35",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f149b0723b0787b0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e37f0e366aa89801eb072297c35",
            "7ec967f0e37f14998082b0723b06bd", "7f07e7f0e47f149b0723b0787b0721",
            "7f0e27f0e47f531b0723b0b6fb0722", "7f0e37f0e366aa89801eb072297c35",
            "7ec967f0e37f14998082b0723b06bd", "7f07e7f0e37f14998083b0787b0721",
            "7f0e27f0e47f531b0723b0b6fb0722", "7f0e37f0e366aa89801eb072297c35",
            "7ec967f0e37f14898082b0723b02d5", "7f07e7f0e37f14998082b0787b0721",
            "7f07e7f0e47f531b0723b0b6fb0722", "7f0e36665b66aa89801e9808297c35",
            "665f67f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b0721",
            "7f07e7f0e47f531b0723b0b6fb0722", "7f0e36665b66a449801e9808297c35",
            "665f67f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e36665b66a449801e9808297c35",
            "665f67f0e37f14898082b072297c35", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e26665b66a449801e9808297c35",
            "665f67f0e37f1489801eb072297c35", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722" };
    private static String[] nStr1 = { "\u65e5", "\u4e00", "\u4e8c", "\u4e09",
            "\u56db", "\u4e94", "\u516d", "\u4e03", "\u516b", "\u4e5d",
            "\u5341" };
    private static String[] nStr2 = { "\u521d", "\u5341", "\u5eff", "\u5345" };
    private static String[] nStr3 = { "\u6b63", "\u4e8c", "\u4e09", "\u56db",
            "\u4e94", "\u516d", "\u4e03", "\u516b", "\u4e5d", "\u5341",
            "\u51ac", "\u814a" };

    public static int lYearDays(int year) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            sum += (lunarInfo[year - 1900] & i) != 0 ? 1 : 0;
        }
        return sum + leapDays(year);
    }

    public static int leapMonth(int year) {
        return lunarInfo[year - 1900] & 0xf;
    }

    public static int leapDays(int year) {
        if (leapMonth(year) != 0) {
            return (lunarInfo[year - 1900] & 0x10000) != 0 ? 30 : 29;
        }
        return 0;
    }

    public static int monthDays(int year, int month) {
        if (month > 12 || month < 1) {
            return -1;
        }
        return ((lunarInfo[year - 1900] & (0x10000 >> month)) != 0 ? 30 : 29);
    }

    public static int solarDays(int year, int month) {
        if (month > 12 || month < 1) {
            return -1;
        }
        int ms = month - 1;
        if (ms == 1) {
            return (((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) ? 29
                    : 28);
        } else {
            return solarMonth[ms];
        }
    }

    public static String toGanZhi(int offset) {
        return (Gan[offset % 10] + Zhi[offset % 12]);
    }

    private static Integer parseHexStr2Int(String v) {
        if ("".equals(v)) {
            return 0;
        }
        return Integer.valueOf(v, 16);
    }

    public static int getTerm(int y, int n) {
        if (y < 1900 || y > 2100) {
            return -1;
        }
        if (n < 1 || n > 24) {
            return -1;
        }
        String _table = sTermInfo[y - 1900];
        String[] _info = { parseHexStr2Int(_table.substring(0, 5)).toString(),
                parseHexStr2Int(_table.substring(5, 10)).toString(),
                parseHexStr2Int(_table.substring(10, 15)).toString(),
                parseHexStr2Int(_table.substring(15, 20)).toString(),
                parseHexStr2Int(_table.substring(20, 25)).toString(),
                parseHexStr2Int(_table.substring(25, 30)).toString() };
        String[] _calday = { _info[0].substring(0, 1),
                _info[0].substring(1, 3), _info[0].substring(3, 4),
                _info[0].substring(4, 6), _info[1].substring(0, 1),
                _info[1].substring(1, 3), _info[1].substring(3, 4),
                _info[1].substring(4, 6), _info[2].substring(0, 1),
                _info[2].substring(1, 3), _info[2].substring(3, 4),
                _info[2].substring(4, 6), _info[3].substring(0, 1),
                _info[3].substring(1, 3), _info[3].substring(3, 4),
                _info[3].substring(4, 6), _info[4].substring(0, 1),
                _info[4].substring(1, 3), _info[4].substring(3, 4),
                _info[4].substring(4, 6), _info[5].substring(0, 1),
                _info[5].substring(1, 3), _info[5].substring(3, 4),
                _info[5].substring(4, 6), };
        return Integer.valueOf(_calday[n - 1]);
    }

    private static String toChinaMonth(int m) {
        if (m > 12 || m < 1) {
            return null;
        }
        String s = nStr3[m - 1];
        s += "\u6708";
        return s;
    }

    private static String toChinaDay(int d) {
        String s;
        switch (d) {
        case 10:
            s = "\u521d\u5341";
            break;
        case 20:
            s = "\u4e8c\u5341";
            break;
        case 30:
            s = "\u4e09\u5341";
            break;
        default:
            s = nStr2[(int) Math.floor(d / 10)];
            s += nStr1[d % 10];
        }
        return (s);
    }

    public static String getAnimal(int y) {
        return Animals[(y - 4) % 12];
    }

    public static LunarInfo solar2lunar(int y, int m, int d) {
        if (y < 1900 || y > 2100) {
            return null;
        }
        if (y == 1900 && m == 1 && d < 31) {
            return null;
        }
        Calendar objDate = Calendar.getInstance();
        objDate.set(y, m - 1, d);
        int i, leap = 0, temp = 0;
        y = objDate.get(Calendar.YEAR);
        m = objDate.get(Calendar.MONTH) + 1;
        d = objDate.get(Calendar.DAY_OF_MONTH);

        Calendar tempCalendar = Calendar.getInstance(TimeZone
                .getTimeZone("GMT"));
        tempCalendar.set(y, m - 1, d, 0, 0, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        final long sTimeMillis = tempCalendar.getTimeInMillis();
        tempCalendar.set(1900, 0, 31, 0, 0, 0);
        final long eTimeMillis = tempCalendar.getTimeInMillis();
        int offset = (int) ((sTimeMillis - eTimeMillis) / 86400000);
        for (i = 1900; i < 2101 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
        }
        if (offset < 0) {
            offset += temp;
            i--;
        }
        Calendar todayObj = Calendar.getInstance();
        boolean isToday = false;
        if (todayObj.get(Calendar.YEAR) == y
                && todayObj.get(Calendar.MONTH) + 1 == m
                && todayObj.get(Calendar.DAY_OF_MONTH) == d) {
            isToday = true;
        }
        int nWeek = objDate.get(Calendar.DAY_OF_WEEK) - 1;
        String cWeek = nStr1[nWeek];
        if (nWeek == 0) {
            nWeek = 7;
        }
        int year = i;
        leap = leapMonth(i);
        boolean isLeap = false;
        for (i = 1; i < 13 && offset > 0; i++) {
            if (leap > 0 && i == (leap + 1) && isLeap == false) {
                --i;
                isLeap = true;
                temp = leapDays(year);
            } else {
                temp = monthDays(year, i);
            }
            if (isLeap == true && i == (leap + 1)) {
                isLeap = false;
            }
            offset -= temp;
        }
        if (offset == 0 && leap > 0 && i == leap + 1)
            if (isLeap) {
                isLeap = false;
            } else {
                isLeap = true;
                --i;
            }
        if (offset < 0) {
            offset += temp;
            --i;
        }
        int month = i;
        int day = offset + 1;
        int sm = m - 1;
        int term3 = getTerm(year, 3);
        String gzY = toGanZhi(year - 4);
        if (sm < 2 && d < term3) {
            gzY = toGanZhi(year - 5);
        } else {
            gzY = toGanZhi(year - 4);
        }
        int firstNode = getTerm(y, (m * 2 - 1));
        int secondNode = getTerm(y, (m * 2));
        String gzM = toGanZhi((y - 1900) * 12 + m + 11);
        if (d >= firstNode) {
            gzM = toGanZhi((y - 1900) * 12 + m + 12);
        }
        boolean isTerm = false;
        String Term = null;
        if (firstNode == d) {
            isTerm = true;
            Term = solarTerm[m * 2 - 2];
        }
        if (secondNode == d) {
            isTerm = true;
            Term = solarTerm[m * 2 - 1];
        }
        tempCalendar.set(y, sm, 1, 0, 0, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        long dayCyclical = tempCalendar.getTimeInMillis() / 86400000 + 25567 + 10;
        String gzD = toGanZhi((int) dayCyclical + d - 1);
        LunarInfo info = new LunarInfo();
        info.lYear = year;
        info.lMonth = month;
        info.lDay = day;
        info.animal = getAnimal(year);
        info.iMonthCn = (isLeap ? "\u95f0" : "") + toChinaMonth(month);
        info.iDayCn = toChinaDay(day);
        info.cYear = y;
        info.cMonth = m;
        info.cDay = d;
        info.gzYear = gzY;
        info.gzMonth = gzM;
        info.gzDay = gzD;
        info.isToday = isToday;
        info.isLeap = isLeap;
        info.nWeek = nWeek;
        info.ncWeek = "\u661f\u671f" + cWeek;
        info.isTerm = isTerm;
        info.Term = Term;
        return info;
    }

    public static LunarDate lunar2solarDate(int y, int m, int d,
            boolean isLeapMonth) {
        int leapMonth = leapMonth(y);
        if (isLeapMonth && (leapMonth != m)) {
            return null;
        }
        if (y == 2100 && m == 12 && d > 1 || y == 1900 && m == 1 && d < 31) {
            return null;
        }
        int day = monthDays(y, m);
        if (y < 1900 || y > 2100 || d > day) {
            return null;
        }
        long offset = 0;
        for (int i = 1900; i < y; i++) {
            offset += lYearDays(i);
        }
        int leap = 0;
        boolean isAdd = false;
        for (int i = 1; i < m; i++) {
            leap = leapMonth(y);
            if (!isAdd) {
                if (leap <= i && leap > 0) {
                    offset += leapDays(y);
                    isAdd = true;
                }
            }
            offset += monthDays(y, i);
        }
        if (isLeapMonth) {
            offset += day;
        }
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.set(1900, 1, 30, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long stmap = cal.getTimeInMillis();
        cal.clear();
        cal.setTimeInMillis((offset + d - 31) * 86400000 + stmap);
        int cY = cal.get(Calendar.YEAR);
        int cM = cal.get(Calendar.MONTH) + 1;
        int cD = cal.get(Calendar.DAY_OF_MONTH);
        return solar2lunarDate(cY, cM, cD);
    }

    public static LunarDate solar2lunarDate(int y, int m, int d) {
        if (y < 1900 || y > 2100) {
            return null;
        }
        if (y == 1900 && m == 1 && d < 31) {
            return null;
        }
        Calendar objDate = Calendar.getInstance();
        objDate.set(y, m - 1, d);
        int i, leap = 0, temp = 0;
        y = objDate.get(Calendar.YEAR);
        m = objDate.get(Calendar.MONTH) + 1;
        d = objDate.get(Calendar.DAY_OF_MONTH);

        Calendar tempCalendar = Calendar.getInstance(TimeZone
                .getTimeZone("GMT"));
        tempCalendar.set(y, m - 1, d, 0, 0, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        final long sTimeMillis = tempCalendar.getTimeInMillis();
        tempCalendar.set(1900, 0, 31, 0, 0, 0);
        final long eTimeMillis = tempCalendar.getTimeInMillis();
        int offset = (int) ((sTimeMillis - eTimeMillis) / 86400000);
        for (i = 1900; i < 2101 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
        }
        if (offset < 0) {
            offset += temp;
            i--;
        }
        int year = i;
        leap = leapMonth(i);
        boolean isLeap = false;
        for (i = 1; i < 13 && offset > 0; i++) {
            if (leap > 0 && i == (leap + 1) && isLeap == false) {
                --i;
                isLeap = true;
                temp = leapDays(year);
            } else {
                temp = monthDays(year, i);
            }
            if (isLeap == true && i == (leap + 1)) {
                isLeap = false;
            }
            offset -= temp;
        }
        if (offset == 0 && leap > 0 && i == leap + 1)
            if (isLeap) {
                isLeap = false;
            } else {
                isLeap = true;
                --i;
            }
        if (offset < 0) {
            offset += temp;
            --i;
        }
        int month = i;
        int day = offset + 1;
        return new LunarDate(year, month, day, y, m, d);
    }

    public static final class LunarDate {
        public int year;
        public int month;
        public int day;

        public int lYear;
        public int lMonth;
        public int lDay;

        public LunarDate(int year, int month, int day, int lYear, int lMonth,
                int lDay) {
            this.year = year;
            this.month = month;
            this.day = day;

            this.lYear = lYear;
            this.lMonth = lMonth;
            this.lDay = lDay;
        }

        @Override
        public String toString() {
            return year + "-" + month + "-" + day + ",lunar:" + lYear + "-"
                    + lMonth + "-" + lDay;
        }
    }

    public static final class LunarInfo {
        public int lYear;
        public int lMonth;
        public int lDay;
        public String animal;
        public String iMonthCn;
        public String iDayCn;
        public int cYear;
        public int cMonth;
        public int cDay;
        public String gzYear;
        public String gzMonth;
        public String gzDay;
        public boolean isToday;
        public boolean isLeap;
        public int nWeek;
        public String ncWeek;
        public boolean isTerm;
        public String Term;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(cYear).append("-").append(cMonth).append("-")
                    .append(cDay);
            return sb.toString();
        }
    }
}
