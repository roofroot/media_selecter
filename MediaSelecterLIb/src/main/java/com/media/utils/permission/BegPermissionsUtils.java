package com.media.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;


import androidx.activity.ComponentActivity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by YuX on 2018/7/9 13:24
 * ^_^ qq:565749553
 */
public class BegPermissionsUtils {
    private Context activity;
    private TodoBackFromBeg backDo;
    private static BegPermissionsUtils instance;
    public static int UPDATE_PERMISSIONS_CODE;
    public static int ALL_PERMISSIONS_CODE;
    public static int CAMERA_CODE;
    public static int AUDIO_CODE;


    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//    public static final String PERMISSION_GROUP_CONTRACT = Manifest.permission.READ_CONTACTS;

    public static final String[] allRequestPermissions = {
            PERMISSION_RECORD_AUDIO,
//            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
//            PERMISSION_GROUP_CONTRACT
    };
    public static BegPermissionsUtils getInstance(Context context) {
        if(instance==null){
            instance=new BegPermissionsUtils(context, new TodoBackFromBeg() {
                @Override
                public void backTodo(int requestCode) {

                }

                @Override
                public void cancelTodo(int requestCode) {

                }

                @Override
                public void settingBack(int requsetCode) {

                }
            });
        }
        return instance;
    }

    public static BegPermissionsUtils getInstance() {
        return instance;
    }
    private String []   camera_permissions={ Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String []   audio_permissions={ Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    private String []   update_permissions={ Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public BegPermissionsUtils(Context activity, TodoBackFromBeg backDo) {
        this.activity = activity;
        this.backDo = backDo;
        instance=this;
//        ALL_PERMISSIONS_CODE=addPermissions(allRequestPermissions);
//        UPDATE_PERMISSIONS_CODE= addPermissions(update_permissions);
        CAMERA_CODE=addPermissions(camera_permissions);
//        AUDIO_CODE=addPermissions(audio_permissions);
    }



    private ArrayList<String[]> permissionsList=new ArrayList<>();
    public int addPermissions(String [] permissions){
        permissionsList.add(permissions);
        return permissionsList.size()-1;
    }
    private String[] getPermissionsByCode(int code){
        return permissionsList.get(code);
    }

    /**
     * 这个方法用于动态申请权限
     *
     */
    public synchronized boolean checkPermissions( int requestCode) {
        String[] begPermissions=getPermissionsByCode(requestCode);
        boolean bool=true;
        if (Build.VERSION.SDK_INT >= 23) {
            int i = 0;
            for ( ;i < begPermissions.length; i++) {
                int checkedPermissionResult = activity.checkSelfPermission(begPermissions[i]);
                if (checkedPermissionResult != PackageManager.PERMISSION_GRANTED) {
                    goBegPermission(begPermissions, requestCode);
                    bool=false;
                    break;
                }
            }
            return bool;
        }
        return true;
    }


    public synchronized boolean onlyCheckPermissions( int requestCode) {
        String[] begPermissions=getPermissionsByCode(requestCode);
        boolean bool=true;
        if (Build.VERSION.SDK_INT >= 23) {
            int i = 0;
            for ( ;i < begPermissions.length; i++) {
                int checkedPermissionResult = activity.checkSelfPermission(begPermissions[i]);
                if (checkedPermissionResult != PackageManager.PERMISSION_GRANTED) {
                    bool=false;
                    break;
                }
            }
            return bool;
        }
        return true;
    }


    /**
     * 弹出请求权限对话框
     */
    private synchronized void goBegPermission(String[] begPermissions, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {
            if(activity instanceof Activity) {
                ((Activity)activity).requestPermissions(begPermissions, requestCode);
            }
        }
    }

    /**
     * 去设置
     */

    private void gotoSetting() {
        Intent intent = new Intent();
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(packageURI);
        activity.startActivity(intent);
    }

    /**
     * 弹出去设置界面对话框
     */
    private synchronized void showGotoSetting(int requestCode) {

            final int code = requestCode;
            HashMap<String, DialogInterface.OnClickListener> clickOptionMap = new HashMap<String, DialogInterface.OnClickListener>();
            clickOptionMap.put("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gotoSetting();
                    backDo.settingBack(code);
                    isshow=false;
                }
            });
            clickOptionMap.put("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    backDo.cancelTodo(code);
                    isshow=false;
                }
            });
            showButtonDialog("未开启用户权限"+rejectPermissionName+"去设置", "开启用户权限",
                    clickOptionMap);

    }
    private synchronized void showButtonDialog(String message,
                                               String title, HashMap<String, DialogInterface.OnClickListener> map) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setTitle(title);
            boolean bool = true;
            boolean bool2 = true;
            for (String key : map.keySet()) {
                if (bool) {
                    builder.setPositiveButton(key, map.get(key));
                    bool = false;
                } else if (bool2) {
                    builder.setNegativeButton(key, map.get(key));
                    bool2 = false;
                } else {
                    builder.setNeutralButton(key, map.get(key));
                }
            }
            builder.setCancelable(false);
            builder.create().show();
    }
    public static boolean isshow;
    String rejectPermissionName;
    public synchronized void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        boolean bool=true;
        Log.e("tag","方法执行了一次");
        for ( int i = 0;i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= 23) {

                if (grantResults[i]!= PackageManager.PERMISSION_GRANTED) {
                    rejectPermissionName=permissions[i];
                    bool=false;
                    if(activity instanceof Activity) {
                        if (!((Activity)activity).shouldShowRequestPermissionRationale(permissions[i])) {
                            if(!isshow) {
                                isshow = true;
                                showGotoSetting(requestCode);
                            }
                            break;
                        }
                    }
                    backDo.cancelTodo(requestCode);
                    break;
                }
            }
        }
        if(bool) {
            backDo.backTodo(requestCode);
        }
        Log.e("tag","方法结束了");

    }


    public interface TodoBackFromBeg {
        void backTodo(int requestCode);

        void cancelTodo(int requestCode);

        void settingBack(int requsetCode);
    }
}
