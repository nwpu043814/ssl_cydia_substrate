package com.jd.ssl_cydia;

import java.lang.reflect.Method;
import java.util.Random;

import com.saurik.substrate.MS;

import android.util.Log;

public class Main {
    final static String TAG = "denver";

    static  Random rand = new Random();
    /**
     * substrate 初始化后的入口
     */
    public static void initialize() {
        //Hook IMEI
        Log.d(TAG, "hook  ver 1.11");
        MS.hookClassLoad("android.telephony.TelephonyManager", new MS.ClassLoadHook() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public void classLoaded(Class<?> resources) {
                Method getDeviceId;
                try {
                    getDeviceId = resources.getMethod("getDeviceId");
                } catch (NoSuchMethodException e) {
                    getDeviceId = null;
                }
                if (getDeviceId != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();
                    MS.hookMethod(resources, getDeviceId, new MS.MethodHook() {
                        public Object invoked(Object object, Object...args){
                            Object result = null;
                            try{
                                result =  old.invoke(object, args);
                                int append = rand.nextInt(1000) + 1000;
                                if (result == null)
                                {
                                    return "";
                                }
                                result = String.format("%s%d" , result, append);
                                Log.d(TAG, "hook imei return value:"+((String) result).substring(4));
                            }catch(Throwable e){
                                Log.d(TAG, "hook imei err:"+Log.getStackTraceString(e));
                            }
                            return result;
                        }
                    }, old);
                }else{
                    Log.d(TAG, "getDeviceId == null");
                }
            }
        });

        //Hook System Color
        MS.hookClassLoad("android.content.res.Resources", new MS.ClassLoadHook() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public void classLoaded(Class<?> resources) {
                Method getColor;
                try {
                    getColor = resources.getMethod("getColor", Integer.TYPE);
                } catch (NoSuchMethodException e) {
                    getColor = null;
                }
                if (getColor != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();
                    MS.hookMethod(resources, getColor, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args){
                            try{
                                int color = (Integer) old.invoke(resources, args);
                                return color & ~0x0000ff00 | 0x00ee0000;
                            }catch(Throwable e){
                                Log.d(TAG, "hook color err:"+Log.getStackTraceString(e));
                            }
                            return 0xFFFFFFFF;
                        }
                    }, old);
                }else{
                    Log.d(TAG, "getColor == null");
                }
            }
        });
    }
}
