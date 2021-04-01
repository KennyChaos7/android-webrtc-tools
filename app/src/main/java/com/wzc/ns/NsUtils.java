package com.wzc.ns;

import android.util.Log;

/**
 * Created by wangzhengcheng on 2017/10/30.
 */
public class NsUtils {
    private static final String TAG = NsUtils.class.getSimpleName();

    static {
        System.loadLibrary("wzc_webrtc_ns");
    }

    private int mFrequency;
    private int mMode;

    //-----------------------------------------NS 定点数运算----------------------------------------------//
    private int nsInstance = -1;
    private boolean isNsInit = false;

    /**
     * 创建ns实例
     * @return 成功时返回ns实例，失败返回-1
     */
    public native int nsCreate ();

    /**
     * 初始化ns
     * @param frequency 采样率
     */
    public native int nsInit(int nsInstance,int frequency);

    /**
     * 设置降噪策略 等级越高，效果越明显
     * @param mode 0: Mild, 1: Medium , 2: Aggressive
     */
    public native int nsSetPolicy(int nsInstance,int mode);

    /**
     * 核心处理方法
     * sample_H与outData_H 我不是很懂，希望有明白的可以指点下
     * @param sample 低频段音频数据-输入
     * @param sample_H 高频段音频数据-输入(demo中传的是null)
     * @param outData 低频段音频数据-输出
     * @param outData_H 高频段音频数据-输出(demo中传的是null)
     */
    public native int nsProcess(int nsInstance,short[] sample,short[] sample_H,short[] outData, short[] outData_H);

    /**
     * 销毁实例
     */
    public native int nsFree(int nsInstance);

    public NsUtils useNs(){
        nsInstance = nsCreate();
        Log.d(TAG,"nsInstance = " + nsInstance);
        return this;
    }

    public NsUtils setNsConfig(int frequency,int mode){
        this.mFrequency = frequency;
        this.mMode = mode;
        return this;
    }

    public NsUtils prepareNs(){
        if (isNsInit){
            closeNs();
            nsInstance = nsCreate();
        }
        int initStatus = nsInit(nsInstance,mFrequency);
        Log.e(TAG,"nsInitStatus = " + initStatus);
        isNsInit = true;
        int setStatus = nsSetPolicy(nsInstance,mMode);
        Log.e(TAG,"nsSetStatus = " + setStatus);
        return this;
    }

    public int nsProcess(short[] sample,short[] sample_H,short[] outData, short[] outData_H){
        return nsProcess(nsInstance, sample, sample_H, outData, outData_H);
    }

    public void closeNs(){
        if (isNsInit){
            nsFree(nsInstance);
            nsInstance = -1;
            isNsInit = false;
        }
    }

    //-------------------------------------------NSX 浮点数运算------------------------------------------//

    private int nsxInstance = -1;
    private boolean isNsxInit = false;

    public native int nsxCreate ();

    public native int nsxInit(int nsxInstance,int frequency);

    public native int nsxSetPolicy(int nsxInstance,int mode);

    public native int nsxProcess(int nsxInstance,short[] sample,short[] sample_H,short[] outData, short[] outData_H);

    public native int nsxFree(int nsxInstance);

    public NsUtils useNsx(){
        nsxInstance = nsxCreate();
        Log.d(TAG,"nsxInstance = " + nsxInstance);
        return this;
    }

    public NsUtils setNsxConfig(int frequency,int mode){
        this.mFrequency = frequency;
        this.mMode = mode;
        return this;
    }

    public NsUtils prepareNsx(){
        if (isNsxInit){
            closeNsx();
            nsxInstance = nsxCreate();
        }
        int initStatus = nsxInit(nsxInstance,mFrequency);
        Log.e(TAG,"nsxInitStatus = " + initStatus);
        isNsxInit = true;
        int setStatus = nsxSetPolicy(nsxInstance,mMode);
        Log.e(TAG,"nsxSetStatus = " + setStatus);
        return this;
    }

    public int nsxProcess(short[] sample,short[] sample_H,short[] outData, short[] outData_H){
        return nsxProcess(nsxInstance, sample, sample_H, outData, outData_H);
    }


    public void closeNsx(){
        if (isNsxInit){
            nsxFree(nsxInstance);
            nsxInstance = -1;
            isNsxInit = false;
        }
    }


}
