package  cn.ipmsgchat.android.util;

import android.content.Context;

import java.util.LinkedList;

import cn.ipmsgchat.android.BaseActivity;
import cn.ipmsgchat.android.BaseApplication;
import cn.ipmsgchat.android.socket.udp.UDPSocketThread;


/**
 * @fileName ActivityCollectorUtils.java
 * @package szu.wifichat.android.util
 * @description 活动管理类
 **/
public class ActivityCollectorUtils {

    private static LinkedList<BaseActivity> queue = new LinkedList<BaseActivity>();

    public static void addActivity(BaseActivity activity) {
        queue.add(activity);
    }

    public static void removeActivity(BaseActivity activity) {
        queue.remove(activity);
    }

    public static void finishAllActivities(BaseApplication mApplication, Context context) {
        UDPSocketThread udpSocketThread = UDPSocketThread.getInstance(mApplication, context);
        udpSocketThread.notifyOffline();
        udpSocketThread.stopUDPSocketThread();
        for (BaseActivity activity : queue) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }    
    
    public static int getActivitiesNum(){
        if(!queue.isEmpty()){
            return queue.size();
        }
        return 0;
    }

    public static BaseActivity getLastActivity(){
        if(!queue.isEmpty()){
            return queue.getLast();
        }
        return null;
    }
}
