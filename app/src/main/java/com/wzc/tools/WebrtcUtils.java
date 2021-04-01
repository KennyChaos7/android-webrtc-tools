package com.wzc.tools;

import android.util.Log;


public class WebrtcUtils {

    private final String TAG = this.getClass().getSimpleName();

    static {
        System.loadLibrary("wzc_webrtc_tools");
    }

    //agc实例
    private int agcInstance = -1;
    //agc配置
    private WebRtcAgcConfig config = null;
    //是否已初始化
    private boolean mIsInit = false;

    /**
     * 创建agc实例
     * @return :AGC instance if successful
     *         : 0 (i.e., a NULL pointer) if unsuccessful
     */
    public native int create();

    /**
     * 初始化agc
     * minLevel与maxLevel参数具体我不是很懂，这个知识点要更偏向硬件，这里我设置的是0-255区间
     * @param agcInstance agc实例 create()创建成功时返回
     * @param minLevel 最小电平 可能表示mic最小音量
     * @param maxLevel 最大电平 可能表示mic最大音量
     * @param agcMode : 0 - Unchanged
     *                : 1 - Adaptive Analog Automatic Gain Control -3dBOv
     *                : 2 - Adaptive Digital Automatic Gain Control -3dBOv
     *                : 3 - Fixed Digital Gain 0dB
     * @param fs 采样率
     */
    public native int init(int agcInstance,int minLevel,int maxLevel,int agcMode,int fs);

    /**
     * 销毁agc实例
     */
    public native int free(int agcInstance);

    /**
     * 核心处理
     Input:
     *      - agcInst           : AGC instance
     *      - inNear            : Near-end input speech vector (10 or 20 ms) for
     *                            L band
     *      - inNear_H          : Near-end input speech vector (10 or 20 ms) for
     *                            H band
     *      - samples           : Number of samples in input/output vector
     *      - inMicLevel        : Current microphone volume level
     *      - echo              : Set to 0 if the signal passed to add_mic is
     *                            almost certainly free of echo; otherwise set
     *                            to 1. If you have no information regarding echo
     *                            set to 0.
     *
     * Output:
     *      - outMicLevel       : Adjusted microphone volume level
     *      - out               : Gain-adjusted near-end speech vector (L band)
     *                          : May be the same vector as the input.
     *      - out_H             : Gain-adjusted near-end speech vector (H band)
     *      - saturationWarning : A returned value of 1 indicates a saturation event
     *                            has occurred and the volume cannot be further
     *                            reduced. Otherwise will be set to 0.
     *
     * Return value:
     *                          :  0 - Normal operation.
     *                          : -1 - Error
     */
    public native int process(int agcInstance,short[] inNear,int num_bands,int samples,short[] out,int inMicLevel,int outMicLevel,int echo,int saturationWarning);

    /***
     *
     * This function sets the config parameters (targetLevelDbfs,
     * compressionGaindB and limiterEnable).
     *
     *  Input:
     *        - agcInst           : AGC instance
     *        - config            : config struct
     *
     *  Output:
     *
     *  Return value:
     *                            :  0 - Normal operation.
     *                            : -1 - Error
     */
    public native int setConfig(int agcInstance, WebRtcAgcConfig agcConfig);


    public native int addFarend(int agcInstance,short[] inFar,int samples);
    public native int addMic(int agcInstance,short[] inMic,int num_bands,int samples);
    public native int getConfig();
    public native int virtualMic();
    public native int getAddFarendError();


    public WebrtcUtils() {
        config = new WebRtcAgcConfig();
        agcInstance = create();
        Log.e(TAG,"agcInstance = " + agcInstance);
    }

    private class WebRtcAgcConfig {
        private int targetLevelDbfs;
        private int compressionGaindB;
        private int limiterEnable;
    }

    public WebrtcUtils setAgcConfig (int targetLevelDbfs, int compressionGaindB, int limiterEnable ){
        config.targetLevelDbfs = targetLevelDbfs;
        config.compressionGaindB = compressionGaindB;
        config.limiterEnable = limiterEnable;

        return this;
    }

    public WebrtcUtils prepare(){
        if (mIsInit) {
            close();
            agcInstance = create();
        }

        int initStatus = init(agcInstance,0,255,3,8000);

        Log.e(TAG,"initStatus =  " + initStatus);

        mIsInit = true;
        int setStatus = setConfig(agcInstance,config);
        Log.e(TAG,"setStatus =  " + setStatus);
        return this;
    }

    public void close() {
        if (mIsInit) {
            free(agcInstance);
            agcInstance = -1;
            mIsInit = false;
        }
    }

    public int agcProcess (short[] inNear,int num_bands,int samples,short[] out,int inMicLevel,int outMicLevel,int echo,int saturationWarning){
        return process(agcInstance,inNear,num_bands,samples,out,inMicLevel,outMicLevel,echo,saturationWarning);
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

    public WebrtcUtils useNs(){
        nsInstance = nsCreate();
        Log.d(TAG,"nsInstance = " + nsInstance);
        return this;
    }

    public WebrtcUtils setNsConfig(int frequency,int mode){
        this.mFrequency = frequency;
        this.mMode = mode;
        return this;
    }

    public WebrtcUtils prepareNs(){
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

    public WebrtcUtils useNsx(){
        nsxInstance = nsxCreate();
        Log.d(TAG,"nsxInstance = " + nsxInstance);
        return this;
    }

    public WebrtcUtils setNsxConfig(int frequency,int mode){
        this.mFrequency = frequency;
        this.mMode = mode;
        return this;
    }

    public WebrtcUtils prepareNsx(){
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
