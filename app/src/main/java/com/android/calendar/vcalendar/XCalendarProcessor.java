/*=====================================================
when               who                         what, where, why                                                                           comment tag
2011-05-20    duanqingpeng         HCT_DQP_CQNJ00261184
=======================================================*/

package com.android.calendar.vcalendar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.android.calendar.vcalendar.ICalendar.Component;
import com.android.calendar.vcalendar.ICalendar.FormatException;

public class XCalendarProcessor {
    public static final String XCALENDAR_V10 = "XCAL_V10";
    public static final String XCALENDAR_V20 = "XCAL_V20";
    // public static final String VCAL_PATH_STR = "/mnt/sdcard/HCTCalendar/";
    private static String VCAL_PATH_STR_DATA;
    private static final String FILE_EXT = ".vcs";
    private String mstrVersion = "";

    static public class CreateProcessor {
        static public IXCalendarProcessor Creator(String strVersion) {
            IXCalendarProcessor Processor = null;
            if (strVersion.equals(XCALENDAR_V10)) {
                Processor = new XCalendarProcessor_V10();
            } else if (strVersion.equals(XCALENDAR_V20)) {
                Processor = null;
            }
            return Processor;
        }
    }

    private Context mContext;
    /** begin HCT_DQP_CQNJ00261184 **/
    private final String DEFAULT_CHARSET = "UTF-8";// "iso8859-1";
    /** end HCT_DQP_CQNJ00261184 **/
    private final String NEWLINE_STRING = "\n";

    public XCalendarProcessor(Context con, String strVersion) {
        mContext = con;
        mstrVersion = strVersion;
        VCAL_PATH_STR_DATA = mContext.getFilesDir().getPath();
    }

    public boolean ImportXCalFromFile(String strFilePath,
            ContentValues EventsInfo, ArrayList<ContentValues> arrReminderInfo,
            ContentValues AttendeeInfo, int nWeekStart) {
        // TODO Auto-generated method stub
        StringBuilder dbString = new StringBuilder();
        if (!ReadFile(strFilePath, dbString)) {
            Log.i("lwt", "importXCalFromFile false.1");
            return false;
        }
        String strVCalInfo = dbString.toString();
        StringBuilder builLogInfo = new StringBuilder();
        builLogInfo.append("The XCalendarFrom File is ");
        builLogInfo.append(strVCalInfo);
        VCalUtil.Log(builLogInfo.toString());
        Component VCalObject = null;
        VCalObject = ParseVCal(strVCalInfo);
        if (null == VCalObject) {
            Log.i("lwt", "importXCalFromFile false.2");
            return false;
        }

        IXCalendarProcessor Processor = CreateProcessor.Creator(mstrVersion);
        if (null != Processor) {
            Log.i("lwt", "importXCalFromFile true.1");
            return Processor.GetCalendarObject(VCalObject, EventsInfo,
                    arrReminderInfo, AttendeeInfo, nWeekStart);
        }
        Log.i("lwt", "importXCalFromFile false.3");
        return false;
    }

    private boolean ReadFile(String strFilePath, StringBuilder bdString) {

        Uri fileUri = null;
        try {
            fileUri = Uri.parse(strFilePath);
            strFilePath = fileUri.getPath().toString();
        } catch (Exception e) {
            VCalUtil.Log(e, "parse file path to uri error ,is not a uri string");
        }

        File f = null;
        if (!strFilePath.contains("@gmail")) {
            f = new File(strFilePath);
            Log.i("lwt", "ReadFile strFilepath " + strFilePath);
        }

        InputStream stream = null;
        try {
            if (!strFilePath.contains("@gmail")) {
                if (fileUri != null
                        && ContentResolver.SCHEME_CONTENT.equals(fileUri
                                .getScheme())) {
                    stream = mContext.getContentResolver().openInputStream(
                            fileUri);
                } else {
                    stream = new BufferedInputStream(new FileInputStream(f));
                }
            } else {
                /*
                 * only for Gmailadd by zhangwei 20130415
                 */
                stream = mContext.getContentResolver().openInputStream(
                        Uri.parse("content://gmail-ls" + strFilePath));
            }

        } catch (FileNotFoundException e3) {
            // TODO Auto-generated catch block
            VCalUtil.Log(e3, "Read file error");
            Log.i("lwt", "ReadFile false.1");
            return false;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(stream,
                    DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.i("lwt", "ReadFile false.2");
            return false;
        }
        try {
            String strLine = null;
            boolean bFirstLine = true;
            while (null != (strLine = br.readLine())) {
                bdString.append(strLine);
                bdString.append(NEWLINE_STRING);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i("lwt", "ReadFile false.3");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Component ParseVCal(String strVCalInfo) {
        Component VCalObject = null;
        try {
            VCalObject = ICalendar.parseComponent(strVCalInfo);
        } catch (FormatException e) {
            // TODO Auto-generated catch block
            VCalUtil.Log(e, "ParseVCal failed");
            e.printStackTrace();
            return null;

        }
        return VCalObject;
    }

    public String OutportXCalToFile(ContentValues EventsInfo,
            ArrayList<ContentValues> arrReminderInfo,
            ContentValues AttendeeInfo, int nWeekStart) {
        // TODO Auto-generated method stub
        IXCalendarProcessor Processor = CreateProcessor.Creator(mstrVersion);
        StringBuilder buidWrtieString = new StringBuilder();
        StringBuilder buidFileName = new StringBuilder();
        if (null != Processor) {
            if (!Processor.GetCalendarString(buidFileName, buidWrtieString,
                    EventsInfo, arrReminderInfo, AttendeeInfo, nWeekStart)) {
                VCalUtil.Log("Processor.GetCalendarString fail");
                return null;
            }
            String strToWrite = buidWrtieString.toString();
            VCalUtil.Log("OutportXCalToFile string is " + strToWrite);
            String strFileName = buidFileName.toString();
            String tmpStrFileName = buidFileName.toString();
            int length = strFileName.length() - 4;
            while (strFileName.getBytes().length > 254) {
                length--;
                strFileName = tmpStrFileName.substring(0, length);
                strFileName += FILE_EXT;
            }
            // File file = new File(VCAL_PATH_STR);
            // file.mkdirs();
            // if (WriteFile(VCAL_PATH_STR + strFileName, strToWrite)) {
            // return VCAL_PATH_STR + strFileName;
            // } else {
            // return null;
            // }
            strFileName = strFileName.replaceAll("/", "");
            String strFileFullName = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                strFileFullName = writeFileExternalStorage(strFileName,
                        strToWrite);
            } else {
                strFileFullName = WriteFileDataDir(strFileName, strToWrite);
            }
            VCalUtil.Log("write temp file file name is " + strFileFullName);
            return strFileFullName;
        }
        return null;
    }

    private String WriteFileDataDir(String strFileName, String strToWrite) {
        // delete ori file
        String[] arrFileList = mContext.fileList();
        for (String strFile : arrFileList) {
            mContext.deleteFile(strFile);
        }

        FileOutputStream outStream = null;
        try {
            outStream = mContext.openFileOutput(strFileName,
                    Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            VCalUtil.Log(e, "WriteFileDataDir open file exception");
            return null;
        }

        if (null == outStream) {
            VCalUtil.Log("WriteFileDataDir outStream is null");
            return null;
        }

        try {
            outStream.write(strToWrite.getBytes());
            outStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            VCalUtil.Log(e, "WriteFileDataDir write file exception");
            return null;
        }

        return VCAL_PATH_STR_DATA + "/" + strFileName;
    }

    private String writeFileExternalStorage(String strFileName,
            String strToWrite) {
        File realPath = new File(mContext.getExternalCacheDir(), strFileName);
        FileOutputStream outStream = null;
        String pathStr = null;
        try {
            pathStr = realPath.getAbsolutePath();
            outStream = new FileOutputStream(realPath);
        } catch (FileNotFoundException e) {
            VCalUtil.Log(e, "writeFileExternalStorage FileNotFoundException");
            return null;
        }
        try {
            outStream.write(strToWrite.getBytes());
            outStream.close();
        } catch (IOException e) {
            VCalUtil.Log(e, "writeFileExternalStorage write file exception");
            return null;
        }
        return pathStr;
    }

    private boolean WriteFile(String strFilePath, String strToWrite) {
        File f = null;
        f = new File(strFilePath);

        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(f));
        } catch (FileNotFoundException e3) {
            // TODO Auto-generated catch block
            VCalUtil.Log(e3, "write file error");
            return false;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(stream,
                    DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            VCalUtil.Log(e1, "write file error");
            e1.printStackTrace();
            return false;
        }
        try {
            bw.write(strToWrite);
            bw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            VCalUtil.Log(e, "write file error");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
