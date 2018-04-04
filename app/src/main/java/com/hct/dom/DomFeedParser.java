package com.hct.dom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.calendar.R;
import com.android.calendar.chinesefestivalalert.ChineseFestivalRawData;
import com.android.calendar.chinesefestivalalert.basicfounction;

public class DomFeedParser {
    private static String TAG = "DomFeedParser";
    private static boolean DEBUG = false; // [!!!Attention!!! ]change to false
                                          // when commit.
    private static String FILE_DIR = "/data/data/com.android.calendar/files/";
    public static String URL_DOWNLOAD_PAHT = "http://down.hctms.com/CalendarServer/";
    static String DEFAULT_FILE_NAME = "ChineseFestivalRawData.xml";
    static String DEFAULT_FILE_PAHT = FILE_DIR + DEFAULT_FILE_NAME;
    static String CUR_FILE_PAHT = null;
    static String CUR_FILE_NAME = null;

    public static Rawdata parse() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Rawdata rawdata = new Rawdata();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(getFileName());
            InputStream is = new FileInputStream(file);
            Document dom = null;
            try {
                dom = builder.parse(is);
            } catch (SAXParseException ex) {
                Log.e(TAG, "ParseException: " + ex);
                deleteErrorFile();
                return rawdata;
            }
            Element element = dom.getDocumentElement();
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNodes.item(i);
                    Log.v(TAG, "Value = "
                            + childElement.getFirstChild().getNodeValue());
                    Log.v(TAG, "name = " + childElement.getNodeName());
                    if ("Year".equals(childElement.getNodeName())) {
                        rawdata.setYear(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month01".equals(childElement.getNodeName())) {
                        rawdata.setJan(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month02".equals(childElement.getNodeName())) {
                        rawdata.setFeb(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month03".equals(childElement.getNodeName())) {
                        rawdata.setMarch(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month04".equals(childElement.getNodeName())) {
                        rawdata.setApril(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month05".equals(childElement.getNodeName())) {
                        rawdata.setMay(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month06".equals(childElement.getNodeName())) {
                        rawdata.setJune(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month07".equals(childElement.getNodeName())) {
                        rawdata.setJuly(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month08".equals(childElement.getNodeName())) {
                        rawdata.setAug(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month09".equals(childElement.getNodeName())) {
                        rawdata.setSept(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month10".equals(childElement.getNodeName())) {
                        rawdata.setOct(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month11".equals(childElement.getNodeName())) {
                        rawdata.setNov(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("Month12".equals(childElement.getNodeName())) {
                        rawdata.setDec(childElement.getFirstChild()
                                .getNodeValue().toString());
                    } else if ("lastMonth12".equals(childElement.getNodeName())) {
                        rawdata.setlastDec(childElement.getFirstChild()
                                .getNodeValue().toString());
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return rawdata;
    }

    public static boolean isFileExist() {
        File file = new File(getFileName());
        if (file != null && file.exists()) {
            Log.v(TAG, "file exists");
            return true;
        } else {
            Log.v(TAG, "file isn't exists");
            return false;
        }
    }

    public void download(Context context, Handler handler) {
        download(context, handler, false);
    }

    public void download(Context context, Handler handler,
            boolean isManualDownload) {
        Log.v(TAG, "AsyncTask execute,handle=" + handler);
        new MyTask(context, handler, isManualDownload).execute();
    }

    private static String readbuffer(String s) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;
        try {
            URL url = new URL(s);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();
            if (HttpURLConnection.HTTP_OK != urlConn.getResponseCode()) {
                return "connect fail";
            }
            buffer = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(buffer != null)
                    buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    // public static boolean urlConnection(String s) {
    // try {
    // URL url = new URL(s);
    // HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
    // return HttpURLConnection.HTTP_OK == urlConn.getResponseCode();
    // } catch( Exception e){
    // e.printStackTrace();
    // return false;
    // }
    // }

    private class MyTask extends AsyncTask<Context, Handler, String> {
        Context mContext;
        Handler mHandler;
        boolean mIsManualDownload;

        public MyTask(Context context, Handler handle, boolean isManualDowmload) {
            mContext = context;
            mHandler = handle;
            mIsManualDownload = isManualDowmload;
        }

        @Override
        protected String doInBackground(Context... params) {
            FileOutputStream fos = null;
            String text = null;
            String urlDownload = null;
            if (DEBUG) {
                if (CUR_FILE_NAME == null) {
                    Time t = new Time();
                    t.setToNow();
                    CUR_FILE_NAME = "ChineseFestivalRawData" + t.year + ".xml";
                }
                urlDownload = URL_DOWNLOAD_PAHT + CUR_FILE_NAME;
            } else {
                urlDownload = URL_DOWNLOAD_PAHT + DEFAULT_FILE_NAME;
            }
            text = readbuffer(urlDownload);
            if (DEBUG) {
                Log.v(TAG, "AsyncTask downloadXML, text = " + text);
            }
            if (text == null || text.equals("") || text.equals("connect fail")
                    || text.contains("System busy")
                    || (!text.contains("ChineseFestivalRawData"))) {
                return "fail";
            }
            try {
                if (DEBUG) {
                    fos = mContext.openFileOutput(CUR_FILE_NAME,
                            Context.MODE_PRIVATE);
                } else {
                    fos = mContext.openFileOutput(DEFAULT_FILE_NAME,
                            Context.MODE_PRIVATE);
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return "fail";
            }
            try {
                fos.write(text.getBytes());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return "fail";
            }
            try {
                fos.flush();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return "fail";
            }
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
            return "succeed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (basicfounction.DEBUG) {
                Log.d(TAG, "download result = " + result
                        + ", isManualDownload = " + mIsManualDownload);
            }
            if (mIsManualDownload) {
                int toastRes = R.string.downlaod_success;
                if (result.equals("fail")) {
                    toastRes = R.string.downlaod_fail;
                }
                Toast.makeText(mContext, toastRes, Toast.LENGTH_LONG).show();

            }
            if (mHandler != null) {
                Message message = mHandler.obtainMessage();
                message.what = 2;
                message.arg1 = result.equals("fail") ? 0 : 1;
                mHandler.sendMessage(message);
            } else {
                Log.w(TAG, "handle is NULL!");
                if (!result.equals("fail")) {
                    try {
                        ChineseFestivalRawData.refrashData();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
            if (result.equals("fail")) {
                deleteErrorFile();
            }
        }
    }

    private static void deleteErrorFile() {
        File targetFile = new File(getFileName());
        if (targetFile.exists()) {
            Log.d(TAG, "file is delete!");
            targetFile.delete();
        }
    }

    private static String getFileName() {
        if (DEBUG) {
            Time t = new Time();
            t.setToNow();
            CUR_FILE_NAME = "ChineseFestivalRawData" + t.year + ".xml";
            CUR_FILE_PAHT = FILE_DIR + CUR_FILE_NAME;
            Log.v(TAG, "CUR_FILE_PAHT = " + CUR_FILE_PAHT);
            return CUR_FILE_PAHT;
        }
        return DEFAULT_FILE_PAHT;
    }

    // public static boolean isFileRight() {
    // boolean result = false;
    // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // try {
    // DocumentBuilder builder = factory.newDocumentBuilder();
    // File file = new File(getFileName());
    // if (isFileExist()) {
    // FileInputStream is = new FileInputStream(file);
    // try {
    // Document dom = builder.parse(is);
    // Element element = dom.getDocumentElement();
    // NodeList childNodes = element.getChildNodes();
    // for(int i =0;i< childNodes.getLength();i++){
    // if(childNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
    // Element childElement = (Element)childNodes.item(i);
    // //Log.v(TAG, "Value = "+childElement.getFirstChild().getNodeValue());
    // //Log.v(TAG,"name = "+childElement.getNodeName());
    // if("Year".equals(childElement.getNodeName())){
    // Log.d(TAG, "local ChineseFestivalRawData file is right, Year:"
    // + childElement.getFirstChild().getNodeValue());
    // result = true;
    // break;
    // }
    // }
    // }
    // if (result) {
    // //parse(dom);
    // }
    // } catch (SAXParseException ex) {
    // Log.e(TAG, "ParseException: " + ex);
    // deleteErrorFile();
    // ChineseFestivalRawData.refrashData();
    // }
    // }
    // } catch (Throwable e) {
    // throw new RuntimeException(e);
    // }
    // return result;
    // }
}
