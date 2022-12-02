package com.example.myaccessapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 具体的无障碍处理逻辑
 */
public class AccessService extends AccessibilityService {
//    private static final String appPackageName = "com.kuaishou.nebula";
    public static AccessService mService;
    private AccessibilityNodeInfo nodeInfo;
    private int[] times=new int[]{3,5,8,10,13,15,17,20};
    Random random=new Random();
    long startTime;

    private Handler handler=new Handler(){
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 0:
                    try {
                        AccessibilityUtil.INSTANCE.scrollByNode(mService,nodeInfo,0,-800);
                        int index=random.nextInt(times.length);
                        if (System.currentTimeMillis()-startTime>5*60*60*1000){//大于5h退出应用
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        } else{
                            handler.sendEmptyMessageDelayed(0,times[index]*1000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
//                case 1:
//                    Intent intent =new Intent();
//                    intent.setPackage("com.example.myaccessapp");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    break;
            }
        }
    };


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;

    }

    //实现辅助功能
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        int eventType = event.getEventType();
        String className = event.getClassName().toString();

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                Log.i("====Accessibility", className);
                try {
                    if (className.equals("com.example.myaccessapp.MainActivity")) {
                        List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.example.myaccessapp:id/tv");
                        list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                    if (className.equals("com.yxcorp.gifshow.HomeActivity")) {
                        List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.kuaishou.nebula:id/nasa_groot_view_pager");
                        nodeInfo = list.get(0);
                        startTime=System.currentTimeMillis();
                        handler.sendEmptyMessageDelayed(0, 10 * 1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

//                if (className.equals("com.yxcorp.gifshow.detail.PhotoDetailActivity")) {//个人作品页点击进入的播放
//                    //自弹出分享
////                    List<AccessibilityNodeInfo> list=rootNodeInfo.findAccessibilityNodeInfosByViewId("com.kuaishou.nebula:id/forward_button");//分享
////                    list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//
//                    //向下播放作品
//                    List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.kuaishou.nebula:id/nasa_groot_view_pager");
//                    nodeInfo = list.get(0);
//                    handler.sendEmptyMessageDelayed(0, 5 * 1000);
//
////                boolean bool = rootNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
////                bool = rootNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
////                bool = rootNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
//                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Log.i("====change",className);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                Log.i("====scroll",  className);
//                List<AccessibilityNodeInfo> list=rootNodeInfo.findAccessibilityNodeInfosByViewId("com.kuaishou.nebula:id/layout_root_hot_live_play");
//                for (int i=0;i<list.size();i++){
//                    AccessibilityNodeInfo info=list.get(i);
//                    Log.i("====scroll"+i,  info.getParent()+"--"+info.getParent().getParent());
//                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onInterrupt() {
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }

    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }

}
