package com.android.calendar.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public final class ExtendConfigHelper {
    private final static String TAG = "ExtendConfigHelper";
    public final static String EXTEND_CONIFG_PATH = "/system/etc/custom_config/app";
    private final static String CONFIG_MIFAVOR_XML = EXTEND_CONIFG_PATH
            + "/HCTCalendar/configCalendar.xml";
    private static boolean DEBUG = true;
    private static boolean isReadXML = false;
    public static boolean ExtendedCustomEnable = false;

    public static boolean exCfgChineseFestivalFirst = true;

    public static boolean ExCfgChineseFestivalFirstEnable = false;

    public static void Check(Context context) {
        File file = new File(CONFIG_MIFAVOR_XML);
        if (!file.exists()) {
            if (DEBUG)
                Log.d(TAG, "file is not exist!");
            return;
        } else if (isReadXML) {
            return;
        } else {
            if (decodeXmlFileExtendCustom(context, file)) {
                ExtendedCustomEnable = true;
            }
        }

    }

    public static boolean decodeXmlFileExtendCustom(Context context, File file) {
        FileInputStream stream = null;
        boolean success = false;
        try {
            stream = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, null);
            int type;
            do {
                type = parser.next();
                if (type == XmlPullParser.START_TAG) {
                    final String name = parser.getName();
                    final String tag = parser.getAttributeValue(null, "name");
                    if ("config_chinese_festival_first".equals(tag)) {
                        if ("bool".equals(name)) {
                            String value = parser.nextText();
                            exCfgChineseFestivalFirst = Boolean.valueOf(value);
                            ExCfgChineseFestivalFirstEnable = true;
                        } else {
                            if (DEBUG)
                                Log.e(TAG, "other tyle=" + name);
                        }
                    }
                }
            } while (type != XmlPullParser.END_DOCUMENT);
            success = true;
        } catch (NullPointerException e) {
            Log.e(TAG, "failed parsing " + file, e);
        } catch (NumberFormatException e) {
            Log.e(TAG, "failed parsing " + file, e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "failed parsing " + file, e);
        } catch (IOException e) {
            Log.e(TAG, "failed parsing " + file, e);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "failed parsing " + file, e);
        }
        try {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        } catch (IOException e) {
            // Ignore
        }
        isReadXML = true;
        return success;
    }
}
