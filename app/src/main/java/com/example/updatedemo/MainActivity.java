package com.example.updatedemo;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnUpdate;
    private boolean isRegisterReceiver;
    private String APK_TITLE = "体验新版本";//应用描述
    private String APK_NAME = "shopuU.apk"; //应用名
    private String APK_DESC = "更多更全";//应用描述

    //下载地址
    private String DOWNLOAD_URL = "https://cdn.uduojin.com/download/ShopuU_sign.apk";
    private long downloadID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpdate = findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateApp();
            }
        });
    }

    /**
     * 弹出窗口
     */
    private void updateApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("下载新版本")
                .setMessage("检查到有更新")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= 23) {//此处做动态权限申请
                            checkPermissionIsHave();//检查是否已经获取
                        } else {//低于23 不需要特殊处理
                            downloadApk();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }


    /**
     * 检查权限
     */
    private void checkPermissionIsHave() {

        //READ_EXTERNAL_STORAGE ,WRITE_EXTERNAL_STORAGE
        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            downloadApk();
        } else {//申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
        }
    }


    //下载apk
    private void downloadApk() {
        //下载前注册广播
        setReceiver();

        if (downloadID != 0) {//如果之前此任务,先移除掉之前的
            DownLoadUtil.clearCurrentTask(this, downloadID);
        }
        //调取下载方法
        downloadID = DownLoadUtil.downloadApk(this, DOWNLOAD_URL, APK_TITLE, APK_NAME, APK_DESC);
    }

    /**
     * 获取检测权限返回的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 999 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // && 0成功,-1失败
            downloadApk();
        }


    }


    /**
     * 注册下载成功的广播监听
     */
    private void setReceiver() {
        if (!isRegisterReceiver) {
            InstallReceiver receiver = new InstallReceiver();
            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(receiver, intentFilter);
            isRegisterReceiver = true;
        }
    }
}
