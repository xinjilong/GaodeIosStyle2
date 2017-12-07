package com.xinyang.alienware.gaodeiosstyle.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Alienware on 2017/12/7.
 * 适用系统6.0以上的动态权限获取功能
 * 当前类只是检测是否已经申请某个权限
 */

public class PermissionsChecker {
    private final Context mContext ;

    public PermissionsChecker(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }
    /**
     * 判断权限集合
     */
    public boolean lacksPermissions(String... permissions){
        for (String permission : permissions){
            if(lacksPermission(permission)){
                return true ;
            }
        }
        return false ;
    }
    /**
     * 判断是否缺少权限
     */
    private boolean lacksPermission(String permission){
        return ContextCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_DENIED;
    }
}


