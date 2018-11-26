package com.example.updatedemo;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author 王鹏
 * @date 2018/11/26
 */
public class DownLoadUtil {

    /**
     * 下载APK
     * @param context
     * @param DOWNLOAD_URL  下载地址
     * @param APK_NAME      应用名字 .apk
     * @param APK_DESC      下载描述
     * @param APK_TITLE     下载标题
     * @return
     */
    public static long downloadApk(Context context, String DOWNLOAD_URL, String APK_TITLE, String APK_NAME, String APK_DESC) {
        Uri uri = Uri.parse(DOWNLOAD_URL);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(request.NETWORK_MOBILE | request.NETWORK_WIFI);
        //设置是否允许漫游
        request.setAllowedOverRoaming(true);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(DOWNLOAD_URL));
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setNotificationVisibility(request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(APK_TITLE);
        request.setDescription(APK_DESC);
        request.setVisibleInDownloadsUi(true);
        //sdcard目录下的download文件夹
        request.setDestinationInExternalPublicDir("/download", APK_NAME);
        // 将下载请求放入队列
        return downloadManager.enqueue(request);
        

    }


    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public static void clearCurrentTask(Context context,long downloadId) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
