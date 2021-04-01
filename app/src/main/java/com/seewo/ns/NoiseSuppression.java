package com.seewo.ns;

public class NoiseSuppression {

    static {
        System.loadLibrary("NoiseSuppressionSDK");
    }   

    public native long WebRtcNsCreate();

    public native void WebRtcNsFree(long nsHandler);

    public native int WebRtcNsInit(long nsHandler, int sf);

    public native int WebRtcNsSetPolicy(long nsHandler, int mode);


    public native void WebRtcNsAnalyze(long nsHandler, short[] inFrame);

    public native void WebRtcNsProcess(long nsHandler, short[] inFrame, int num_bands, short[] outFrame);
}
