package com.wzc.tools;

import android.util.Log;


public class WebrtcUtils {

    private final String TAG = this.getClass().getSimpleName();
    private final static int AGC_MIN_VOL = 0;
    private final static int AGC_MAX_VOL = 255;

    static {
        System.loadLibrary("wzc_webrtc_tools");
    }

    //agc实例
    private int agcInstance = -1;
    //agc配置
    private WebRtcAgcConfig webRtcAgcConfig = null;
    // agc增益模式
    private int agcMode = 0;
    //是否agc已初始化
    private boolean isAgcInit = false;


    //ns实例
    private int nsInstance = -1;
    //是否ns已初始化
    private boolean isNsInit = false;
    //nsx实例
    private int nsxInstance = -1;
    //是否nsx已初始化
    private boolean isNsxInit = false;
    //ns采样率
    private int bitSample;
    //ns降噪模式
    private int nsMode;


    //-----------------------------------------Aecm 消除回声----------------------------------------------//




    //-----------------------------------------Agc 增益----------------------------------------------//


    private void setAgcConfig(WebrtcUtils.WebRtcAgcConfig webRtcAgcConfig, int agcMode) {
        this.webRtcAgcConfig = webRtcAgcConfig;
        this.agcMode = agcMode;
    }

    private WebrtcUtils prepareAgc(){
        if (isAgcInit) {
            close();
        }
        agcInstance = createAgc();
        int initStatus = initAgc(agcInstance,AGC_MIN_VOL,AGC_MAX_VOL,this.agcMode,this.bitSample);

        Log.e(TAG,"initStatus =  " + initStatus);

        isAgcInit = true;
        int setStatus = setAgcConfig(agcInstance,webRtcAgcConfig);
        Log.e(TAG,"setStatus =  " + setStatus);
        return this;
    }

    private void close() {
        if (isAgcInit) {
            freeAgc(agcInstance);
            agcInstance = -1;
            isAgcInit = false;
        }
    }

    public int processAgc (short[] inNear,int num_bands,int samples,short[] out,int inMicLevel,int outMicLevel,int echo,int saturationWarning){
        return processAgc(agcInstance,inNear,num_bands,samples,out,inMicLevel,outMicLevel,echo,saturationWarning);
    }


    //-----------------------------------------NS 定点数运算----------------------------------------------//

    private void setNsConfig(int bitSample,int mode){
        this.bitSample = bitSample;
        this.nsMode = mode;
    }

    private void prepareNs(){
        if (isNsInit){
            closeNs();
        }
        nsInstance = createNs();
        int initStatus = initNs(nsInstance, bitSample);
        Log.e(TAG,"nsInitStatus = " + initStatus);
        isNsInit = true;
        int setStatus = setNsPolicy(nsInstance, nsMode);
        Log.e(TAG,"nsSetStatus = " + setStatus);
    }

    public int processNs(short[] sample,short[] sample_H,short[] outData, short[] outData_H){
        return processNs(nsInstance, sample, sample_H, outData, outData_H);
    }

    private void closeNs(){
        if (isNsInit){
            freeNs(nsInstance);
            nsInstance = -1;
            isNsInit = false;
        }
    }

    //-------------------------------------------NSX 浮点数运算------------------------------------------//

    private void setNsxConfig(int bitSample,int mode){
        this.bitSample = bitSample;
        this.nsMode = mode;
    }

    private WebrtcUtils prepareNsx(){
        if (isNsxInit){
            closeNsx();
            nsxInstance = createNsx();
        }
        int initStatus = initNsx(nsxInstance, bitSample);
        Log.e(TAG,"nsxInitStatus = " + initStatus);
        isNsxInit = true;
        int setStatus = setNsxPolicy(nsxInstance, nsMode);
        Log.e(TAG,"nsxSetStatus = " + setStatus);
        return this;
    }

    private void closeNsx(){
        if (isNsxInit){
            freeNsx(nsxInstance);
            nsxInstance = -1;
            isNsxInit = false;
        }
    }

    public static class WebRtcAecmConfig {
        private int cngMode;
        private int echoMode;

        public WebRtcAecmConfig(int cngMode, int echoMode) {
            this.cngMode = cngMode;
            this.echoMode = echoMode;
        }
    }

    public static class WebRtcAgcConfig {
        private int targetLevelDbfs;
        private int compressionGaindB;
        private int limiterEnable;

        public WebRtcAgcConfig(int targetLevelDbfs, int compressionGaindB, int limiterEnable) {
            this.targetLevelDbfs = targetLevelDbfs;
            this.compressionGaindB = compressionGaindB;
            this.limiterEnable = limiterEnable;
        }
    }

    public static class WebRtcFactory{
        private int bitSamples = 0;
        private int nsMode = 0;
        private int agcMode = 0;
        private WebRtcAgcConfig webRtcAgcConfig = null;
        
        private WebRtcFactory factory = null;

        public WebRtcFactory() {

        }

        public WebRtcFactory initNs(int bitSamples, int nsMode) {
            this.bitSamples = bitSamples;
            this.nsMode = nsMode;
            return this;
        }

        public WebRtcFactory setBitSamples(int bitSamples) {
            this.bitSamples = bitSamples;
            return this;
        }

        public WebRtcFactory setNsMode(int nsMode) {
            this.nsMode = nsMode;
            return this;
        }

        public WebRtcFactory initAgc(WebrtcUtils.WebRtcAgcConfig webRtcAgcConfig, int agcMode) {
            this.webRtcAgcConfig = webRtcAgcConfig;
            this.agcMode = agcMode;
            return this;
        }

        public WebrtcUtils build() {
            WebrtcUtils webrtcUtils = new WebrtcUtils();
            webrtcUtils.setNsConfig(this.bitSamples, this.nsMode);
            webrtcUtils.prepareNs();
            webrtcUtils.setAgcConfig(this.webRtcAgcConfig, this.agcMode);
            webrtcUtils.prepareAgc();
            return webrtcUtils;
        }
    }



    /*-------------------------------Native--------------------------*/
    /**
     * 创建agc实例
     * @return :AGC instance if successful
     *         : 0 (i.e., a NULL pointer) if unsuccessful
     */
    private native int createAgc();

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
    private native int initAgc(int agcInstance,int minLevel,int maxLevel,int agcMode,int fs);

    /**
     * 销毁agc实例
     */
    private native int freeAgc(int agcInstance);

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
    private native int processAgc(int agcInstance,short[] inNear,int num_bands,int samples,short[] out,int inMicLevel,int outMicLevel,int echo,int saturationWarning);

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
    private native int setAgcConfig(int agcInstance, WebRtcAgcConfig agcConfig);

    /**
     * 创建ns实例
     * @return 成功时返回ns实例，失败返回-1
     */
    private native int createNs();

    /**
     * 初始化ns
     * @param frequency 采样率
     */
    private native int initNs(int nsInstance,int frequency);

    /**
     * 设置降噪策略 等级越高，效果越明显
     * @param mode 0: Mild, 1: Medium , 2: Aggressive
     */
    private native int setNsPolicy(int nsInstance,int mode);

    /**
     * 核心处理方法
     * sample_H与outData_H 我不是很懂，希望有明白的可以指点下
     * @param sample 低频段音频数据-输入
     * @param sample_H 高频段音频数据-输入(demo中传的是null)
     * @param outData 低频段音频数据-输出
     * @param outData_H 高频段音频数据-输出(demo中传的是null)
     */
    private native int processNs(int nsInstance,short[] sample,short[] sample_H,short[] outData, short[] outData_H);

    /**
     * 销毁实例
     */
    private native int freeNs(int nsInstance);

    private native int createNsx();

    private native int initNsx(int nsxInstance,int frequency);

    private native int setNsxPolicy(int nsxInstance,int mode);

    private native int processNsx(int nsxInstance,short[] sample,short[] sample_H,short[] outData, short[] outData_H);

    private native int freeNsx(int nsxInstance);
}
