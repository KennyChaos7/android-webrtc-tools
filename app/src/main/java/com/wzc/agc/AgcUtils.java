package com.wzc.agc;

import android.util.Log;

/**
 * Created by wangzhengcheng on 2017/10/23.
 */

public class AgcUtils {
    private static final String TAG = AgcUtils.class.getSimpleName();

    static {
        System.loadLibrary("wzc_webrtc_agc");
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
    public native int setConfig(int agcInstance,WebRtcAgcConfig agcConfig);


    public native int addFarend(int agcInstance,short[] inFar,int samples);
    public native int addMic(int agcInstance,short[] inMic,int num_bands,int samples);
    public native int getConfig();
    public native int virtualMic();
    public native int getAddFarendError();


    public AgcUtils() {
        config = new WebRtcAgcConfig();
        agcInstance = create();
        Log.e(TAG,"agcInstance = " + agcInstance);
    }

    private class WebRtcAgcConfig {
        private int targetLevelDbfs;
        private int compressionGaindB;
        private int limiterEnable;
    }

    public AgcUtils setAgcConfig (int targetLevelDbfs, int compressionGaindB, int limiterEnable ){
        config.targetLevelDbfs = targetLevelDbfs;
        config.compressionGaindB = compressionGaindB;
        config.limiterEnable = limiterEnable;

        return this;
    }

    public AgcUtils prepare(){
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

}
