package com.jogger.beautifulapp.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 侯建军
 * @data on 2018/1/4 10:40
 * @org www.hopshine.com
 * @function 请填写
 * @email 474664736@qq.com
 */
public class Ulog {
    /**
     * 日志的标记
     */
    public static final String TAG = "cc-";

    /**
     * 是否隐藏日志
     */
    public static final boolean IS_HIDE_LOG = false;

    /**
     * 本地日志目录名称
     */
    public static final String LogFileName = "BeautifulAppLog";
    /**
     * 文件编码方式
     */
//    public static final String CHARSET_NAME = "gbk";
    public static final String CHARSET_NAME = "utf-8";


    public static void i(Object tag, Object msg) {
        if (IS_HIDE_LOG) return;
        Log.i(TAG + tag, msg + "");
        writeToFile(tag + ":" + msg);
    }

    /**
     * 不保存到本地
     * @param tag
     * @param msg
     */
    public static void io(Object tag, Object msg) {
        if (IS_HIDE_LOG) return;
        Log.i(TAG + tag, msg + "");
    }

    public static void e(Object msg) {
        if (IS_HIDE_LOG) return;
        Log.e(TAG + "error", msg + "");
        writeToFile("error:" + msg);
    }

    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat mSimpleDateFormatTag = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 写入内存卡
     *
     * @param text
     */
    public static void writeToFile(String text) {
        //开始写入
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            //文件路径
            String fileRoot = Environment.getExternalStorageDirectory().getPath() + "/" + LogFileName + "/";
            String fileName = mSimpleDateFormatTag.format(new Date()) + ".txt";
            // 时间 + 内容
            String log = "\n\n-----------" + mSimpleDateFormat.format(new Date()) + "----------\n" + text;
            //检查父路径
            File fileGroup = new File(fileRoot);
            //创建根布局
            if (!fileGroup.exists()) {
                fileGroup.mkdirs();
            }
            //创建文件
            File fileChild = new File(fileRoot + fileName);
            if (!fileChild.exists()) {
                fileChild.createNewFile();
            }
            fileOutputStream = new FileOutputStream(fileRoot + fileName, true);
            //编码问题 GBK 正确的存入中文
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, Charset.forName(CHARSET_NAME)));
            bufferedWriter.write(log);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            e(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            e(e.toString());
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
