package com.jd.ssl_cydia;

import java.lang.reflect.Method;

import com.saurik.substrate.MS;

import android.util.Log;

public class Main {

    /**
     * substrate 初始化后的入口
     */
    public static void initialize() {
        //Hook IMEI
        Log.d("jw", "hook imei initialize...");
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
                                Log.d("jw", "hook imei start...");
                                result =  old.invoke(object, args);
                                Log.d("jw", "hook imei before value:"+result);
                                result = "fourbrother";
                            }catch(Throwable e){
                                Log.d("jw", "hook imei err:"+Log.getStackTraceString(e));
                            }
                            return result;
                        }
                    }, old);
                }else{
                    Log.d("jw", "getDeviceId == null");
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
                                return color & ~0x0000ff00 | 0x00ff0000;
                            }catch(Throwable e){
                                Log.d("jw", "hook color err:"+Log.getStackTraceString(e));
                            }
                            return 0xFFFFFFFF;
                        }
                    }, old);
                }else{
                    Log.d("jw", "getColor == null");
                }
            }
        });
    }
}
