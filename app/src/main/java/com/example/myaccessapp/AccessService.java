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
 * 授权后自动打开目标应用，刷一段时间后关闭目标应用休息，休息一段时间后再次打开目标应用刷
 */
public class AccessService extends AccessibilityService {
//    private static final String appPackageName = "com.kuaishou.nebula";
    public static AccessService mService;
    private AccessibilityNodeInfo nodeInfo;
    private int[] allTimes=new int[]{5,10,20,30,40,50,60,2*60,3*60};//连续刷的总时长(最多3h) 分-时
    private int[] times=new int[]{5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,25,30,35,40};//每个视频停留的时长(最多20s)-秒
    private int[] restTimes=new int[]{1*60,2*60,3*60,4*60,5*60,6*60,7*60,8*60,9*60,10*60};//休息的时长(最多10min)-分
    Random random=new Random();
    long startTime;
    int allTime;
    boolean needOpen=true;//控制休息时不触发自动打开目标应用

    private Handler handler=new Handler(){
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 0:
                    try {
                        AccessibilityUtil.INSTANCE.scrollByNode(mService,nodeInfo);
                        if (System.currentTimeMillis()-startTime> allTime*60*1000){//大于5h退出应用
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            needOpen=false;
                            handler.sendEmptyMessageDelayed(1,restTimes[random.nextInt(restTimes.length)]*1000);
                        } else{
                            int index=random.nextInt(times.length);
                            handler.sendEmptyMessageDelayed(0,times[index]*1000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    needOpen=true;
                    Intent intent =new Intent();
                    intent.setPackage("com.kuaishou.nebula");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
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
//        if (event.getPackageName()==null){
//            return;
//        }
        if (event.getClassName()==null){
            return;
        }
//        String packageName=event.getPackageName().toString();
        String className = event.getClassName().toString();

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                Log.i("====Accessibility", className);
                try {
                    if (className.equals("com.android.settings.SubSettings")){
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }
                    if (className.equals("com.android.settings.accessibility.MiuiAccessibilitySettingsActivity")){
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }
                    if (className.equals("com.example.myaccessapp.MainActivity")) {//点击按钮打开应用
                        List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.example.myaccessapp:id/tv");
                        list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                     if (needOpen&&className.equals("com.android.internal.app.ResolverActivity")){//选择弹窗中选择一个打开
                         List<AccessibilityNodeInfo> list =rootNodeInfo.findAccessibilityNodeInfosByText("快手极速版");
                         list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                         return;
                     }
                    if (className.equals("com.yxcorp.gifshow.HomeActivity")) {
                        allTime=allTimes[random.nextInt(allTimes.length)];
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
