package com.sina.sae.cloudservice.api;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sina.sae.cloudservice.callback.ExecuteCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * Statistic 提供了各种统计接口
 * 
 * @author zhiyun
 */
public class Statistic {

    private static ConcurrentHashMap<String, Long> associatedEventBeginTime = new ConcurrentHashMap<String, Long>();

    /**
     * 单独事件统计，主要用于统计执行时间的次数
     * 
     * @param viewid
     *            组件唯一标示
     */
    public static void singleEvent(String viewid) throws CloudServiceException {
        String sql = "insert into statistic_clicks values('" + viewid
                + "', now(),now(),1) on duplicate key update count=count+1,updatetime=now();";
        commitStatisticData(sql);
    }

    /**
     * 关联统计事件（开始），主要用于统计view停留的时间，配合eventEnd使用
     * 
     * @param viewid
     *            组件唯一标示
     */
    public static void eventBegin(String viewid) {
        if (CloudClient.checkNetwork(CloudClient.context)) {// 网络不通不做操作
            associatedEventBeginTime.put(viewid, System.currentTimeMillis());
        }
    }

    /**
     * 关联统计事件（结束），主要用于统计view停留的时间，配合eventBegin使用
     * 
     * @param viewid
     *            组件唯一标示
     */
    public static void eventEnd(String viewid) throws CloudServiceException {
        Long beginTime = associatedEventBeginTime.get(viewid);
        if (null != beginTime) {
            long duration = System.currentTimeMillis() - beginTime;
            associatedEventBeginTime.remove(viewid);
            String sql = "insert into  statistic_stay(`viewid`,`inserttime`,`seconds`) values('"
                    + viewid + "',now()," + duration / 1000 + ");";
            commitStatisticData(sql);
        }
    }

    /**
     * 开启应用时的统计 主要统计应用被开启次数和时间
     */
    public static void launch(Context context) throws CloudServiceException {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String model = Build.MODEL;
        String osversion = Build.VERSION.RELEASE;
        String deviceid = tm.getDeviceId();
        String networktype = tm.getNetworkType() + "";
        if (null != deviceid) {// 能获取到设备号才做统计
            String sql = "insert into statistic_launch  values('" + deviceid + "','" + model
                    + "','" + osversion + "',1,now(),now(),'" + networktype
                    + "') on duplicate key update launchtimes=launchtimes+1,updatetime=now();";
            commitStatisticData(sql);
        }
    }

    /**
     * 统计类使用的提交sql
     * 
     * @param sql
     * @throws CloudServiceException
     */
    private static void commitStatisticData(final String sql) throws CloudServiceException {
        CloudDB.execute(sql, new ExecuteCallback() {
            @Override
            public void handle(int row, CloudServiceException e) {
                if (row > 0 && e == null) {
                    Log.v("CloudService", "Statistic success!Execute row:" + row + " .SQL =>" + sql);
                } else {
                    Log.e("CloudService", "Statistic error!Error message: " + e.getMessage()
                            + " .SQL =>" + sql);
                }
            }
        });
    }
}
