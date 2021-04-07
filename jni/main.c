/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <stdlib.h> // for NULL
#include <assert.h>
#include <string.h>
#include "gain_control.h"
#include "noise_suppression_x.h"
#include "noise_suppression.h"
#include "echo_control_mobile.h"
#include "Platform.h"
#ifndef _Included_com_wzc_tools_WebrtcUtils
#define _Included_com_wzc_tools_WebrtcUtils

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    createAgc
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_createAgc(JNIEnv *env, jclass jclazz){
    void *agcInstance = NULL;
    WebRtcAgc_Create(&agcInstance);
    return ((int) agcInstance);

}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    initAgc
 * Signature: (IIIII)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_initAgc(JNIEnv *env, jclass jclazz, jint agcInstance, jint minLevel, jint maxLevel, jint agcMode, jint fs){
    void *agcInst = (void *) agcInstance;
    if (agcInst == NULL)
        return -5;

    return WebRtcAgc_Init(agcInst,minLevel,maxLevel,kAgcModeFixedDigital,fs);
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    freeAgc
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_freeAgc(JNIEnv *env, jclass jclazz, jint agcInstance){
    void *agcInst = (void *) agcInstance;
    if (agcInst == NULL)
        return -2;
    return WebRtcAgc_Free(agcInst);

}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    processAgc
 * Signature: (I[SII[SIIII)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_processAgc
        (JNIEnv *env, jclass jclazz, jint agcInstance, const jshortArray inNear, jint num_bands, jint samples, jshortArray out, jint inMicLevel, jint outMicLevel, jint echo, jint saturationWarning){

        int16_t *arrNear = NULL;
        int16_t *arrOut = NULL;
        int32_t *outMicLevel1 = NULL;
        uint8_t *test = NULL;
        const int16_t* inNear_H = NULL;
        int16_t* out_H = NULL;


        void *agcInst = (void *) agcInstance;
        if(agcInst == NULL)
        return -1;

        int ret = -1;


        arrNear = (*env)->GetShortArrayElements(env,inNear, NULL);
        arrOut = (*env)->GetShortArrayElements(env,out, NULL);

        ret = WebRtcAgc_Process(agcInst,arrNear,inNear_H,samples,arrOut,out_H,inMicLevel,&outMicLevel1,echo,&test);

        //release and send the changes back to java side.
        (*env)->ReleaseShortArrayElements(env,inNear, arrNear, 0);
        (*env)->ReleaseShortArrayElements(env,out, arrOut, 0);

         return ret;
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    setAgcConfig
 * Signature: (ILcom/wzc/tools/WebrtcUtils/WebRtcAgcConfig;)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_setAgcConfig(JNIEnv *env, jclass jclazz, jint agcInstance, jobject agcConfig){

        void * agcInst = (void *) agcInstance;
        if (agcInst == NULL)
        return -1;

        jclass JavaWebRtcAgcConfig = (*env)->GetObjectClass(env,agcConfig);
        assert(JavaWebRtcAgcConfig != NULL);
        jfieldID targetLevelDbfsID = (*env)->GetFieldID(env,JavaWebRtcAgcConfig, "targetLevelDbfs",
                                       "I");
        jfieldID compressionGaindBID = (*env)->GetFieldID(env,JavaWebRtcAgcConfig, "compressionGaindB",
                                      "I");
        jfieldID limiterEnableID = (*env)->GetFieldID(env,JavaWebRtcAgcConfig, "limiterEnable",
                                      "I");
        if (targetLevelDbfsID == NULL || compressionGaindBID == NULL || limiterEnableID == NULL)
        return -1;

        int targetLevelDbfsMode = (*env)->GetIntField(env,agcConfig, targetLevelDbfsID);
        int compressionGaindBMode = (*env)->GetIntField(env,agcConfig, compressionGaindBID);
        int limiterEnableMode = (*env)->GetIntField(env,agcConfig, limiterEnableID);

        WebRtcAgc_config_t config;
        config.targetLevelDbfs = targetLevelDbfsMode;
        config.compressionGaindB = compressionGaindBMode;
        config.limiterEnable = limiterEnableMode;

        return WebRtcAgc_set_config(agcInst,config);
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    addFarend
 * Signature: (I[SI)I
 */
// JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_addFarend(JNIEnv *env, jclass jclazz, jint agcInstance, jshortArray inFar, jint samples){
//     return 0;
// }

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    addMic
 * Signature: (I[SII)I
 */
// JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_addMic(JNIEnv *env, jclass jclazz, jint agcInstance, jshortArray inMic, jint num_bands, jint samples){
//     return 0;
// }

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    getConfig
 * Signature: ()I
 */
// JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_getConfig(JNIEnv *, jclass);

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    virtualMic
 * Signature: ()I
 */
// JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_virtualMic(JNIEnv *, jclass);

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    getAddFarendError
 * Signature: ()I
 */
// JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_getAddFarendError(JNIEnv *, jclass);

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    createNs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_createNs(JNIEnv *env, jobject obj){
    NsHandle* handle = NULL;
    WebRtcNs_Create(&handle);

    return (int)handle;
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    initNs
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_initNs(JNIEnv *env, jobject obj, jint nsHandler, jint frequency){
    NsHandle *handler = (NsHandle *) nsHandler;
    if (handler == NULL){
        return -3;
    }
    return WebRtcNs_Init(handler,frequency);
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    setNsPolicy
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_setNsPolicy(JNIEnv *env, jobject obj, jint nsHandler, jint mode){

    NsHandle *handle = (NsHandle *) nsHandler;

    if (handle == NULL){
        return -3;
    }
    return WebRtcNs_set_policy(handle,mode);

}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    processNs
 * Signature: (I[SI[S)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_processNs
        (JNIEnv *env, jobject obj, jint nsHandler, jshortArray before, jshortArray before_H, jshortArray out,jshortArray out_H){
    NsHandle *handle = (NsHandle *) nsHandler;
    if(handle == NULL){
        return -3;
    }
    short* spframe = NULL;
    short* outframe = NULL;
    short* spframe_H = NULL;
    short* outframe_H = NULL;

    int ret = -1;
    spframe = (*env)->GetShortArrayElements(env,before, NULL);
    outframe = (*env)->GetShortArrayElements(env,out, NULL);
    if(before_H != NULL){
        spframe_H = (*env)->GetShortArrayElements(env,before_H, NULL);
    }
    if(out_H != NULL){
        outframe_H = (*env)->GetShortArrayElements(env,out_H, NULL);
    }

    ret = WebRtcNs_Process(handle,spframe,spframe_H,outframe,outframe_H);

    (*env)->ReleaseShortArrayElements(env,before, spframe, 0);
    (*env)->ReleaseShortArrayElements(env,out, outframe, 0);

    if(before_H != NULL){
        (*env)->ReleaseShortArrayElements(env,before_H, spframe_H, 0);
    }
    if(out_H != NULL){
        (*env)->ReleaseShortArrayElements(env,out_H, outframe_H, 0);
    }

    return ret;

}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    freeNs
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_freeNs(JNIEnv *env, jobject obj, jint nsHandler){
    NsHandle *handle = (NsHandle *) nsHandler;
    if(handle == NULL){
        return -3;
    }

    return WebRtcNs_Free(handle);

}




                            /** NSX PROCESS*/



/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    createNsx
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_createNsx(JNIEnv *env, jobject obj){

    NsHandle* handle = NULL;
    WebRtcNsx_Create(&handle);

    return (int)handle;

}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    initNsx
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_initNsx(JNIEnv *env, jobject obj, jint nsxHandler, jint frequency ){
    NsHandle *handler = (NsHandle *) nsxHandler;
    if (handler == NULL){
        return -3;
    }
    return WebRtcNsx_Init(handler,frequency);

}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    setNsxPolicy
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_setNsxPolicy(JNIEnv *env, jobject obj, jint nsxHandler, jint mode){
    NsHandle *handle = (NsHandle *) nsxHandler;

    if (handle == NULL){
        return -3;
    }
    return WebRtcNsx_set_policy(handle,mode);
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    processNsx
 * Signature: (I[SI[S)I
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_processNsx
        (JNIEnv *env, jobject obj, jint nsxHandler, jshortArray sample, jshortArray sample_H, jshortArray out,jshortArray out_H){
    NsHandle *handle = (NsHandle *) nsxHandler;
    if(handle == NULL){
        return -3;
    }
    short* spframe = NULL;
    short* outframe = NULL;
    short* spframe_H = NULL;
    short* outframe_H = NULL;

    int ret = -1;
    spframe = (*env)->GetShortArrayElements(env,sample, NULL);
    outframe = (*env)->GetShortArrayElements(env,out, NULL);
    if(sample_H != NULL){
        spframe_H = (*env)->GetShortArrayElements(env,sample_H, NULL);
    }
    if(out_H != NULL){
        outframe_H = (*env)->GetShortArrayElements(env,out_H, NULL);
    }

    ret = WebRtcNsx_Process(handle,spframe,spframe_H,outframe,outframe_H);

    (*env)->ReleaseShortArrayElements(env,sample, spframe, 0);
    (*env)->ReleaseShortArrayElements(env,out, outframe, 0);

    if(sample_H != NULL){
        (*env)->ReleaseShortArrayElements(env,sample_H, spframe_H, 0);
    }
    if(out_H != NULL){
        (*env)->ReleaseShortArrayElements(env,out_H, outframe_H, 0);
    }

    return ret;
}

/*
 * Class:     com_wzc_tools_WebrtcUtils
 * Method:    freeNsx
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_freeNsx(JNIEnv *env, jobject obj, jint nsxHandler){
    NsHandle *handle = (NsHandle *) nsxHandler;
    if(handle == NULL){
        return -3;
    }

    return WebRtcNsx_Free(handle);
}



JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_createAecm(JNIEnv *env) {
     return WebRtcAecm_Create();
}




JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_initAecm(JNIEnv *env, jint aecmInstance, jint sampleFrequency) {
    void* instance = (void*)aecmInstance;
    int32_t samFreq = (int32_t)sampleFrequency;
    return WebRtcAecm_Init(instance, samFreq);
}




JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_freeAecm(JNIEnv *env, jint aecmInstance) {
     void* instance = (void*)aecmInstance;
     WebRtcAecm_Free(instance);
}



JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_bufferFarendAecm(JNIEnv *env, jint aecmInstance, jshortArray farend, jint farendSize) {
    void* instance = (void*)aecmInstance;
    short* fraendData = NULL;
    int sizeFraendData = (int)farendSize;
    int ret = -1;
    fraendData = (*env)->GetShortArrayElements(env,farend, NULL);
    
    ret = WebRtcAecm_BufferFarend(instance, fraendData, sizeFraendData);

    (*env)->ReleaseShortArrayElements(env,farend, fraendData, 0);

    return ret;
}


JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_processAecm
    (JNIEnv *env, jint aecmInstance, jshortArray noisyData, jshortArray cleanData, jshortArray outData, jint dataSize, jshort msInSndCardBuf) {
    void* instance = (void*)aecmInstance;
    short*  _noisyData= NULL;
    short*  _cleanData= NULL;
    short*  _outData= NULL;
    int _sizeData = (int)dataSize;  
    short _msInSndCardBuf = (int16_t)msInSndCardBuf;
    int ret = -1;
   
    if (noisyData != NULL)
    {
        _noisyData = (*env)->GetShortArrayElements(env,noisyData, NULL);
    }
    if (cleanData != NULL)
    {
        _cleanData = (*env)->GetShortArrayElements(env,cleanData, NULL);
    }
    _outData = (*env)->GetShortArrayElements(env,outData, NULL);


    ret = WebRtcAecm_Process(instance, _noisyData, _cleanData, _outData, _sizeData, _msInSndCardBuf);
    
    if (noisyData != NULL)
    {
         (*env)->ReleaseShortArrayElements(env,noisyData, _noisyData, 0);
    }
    if (_cleanData != NULL) 
    {
         (*env)->ReleaseShortArrayElements(env,cleanData, _cleanData, 0);
    }
    (*env)->ReleaseShortArrayElements(env,outData, _outData, 0);

    return ret;
    
}


JNIEXPORT jint JNICALL Java_com_wzc_tools_WebrtcUtils_setAecmConfig
    (JNIEnv *env, jint aecmInstance, jclass aecmConfig)
{
    void* instance = (void*)aecmInstance;
    jclass JavaWebRtcAecmConfig = (*env)->GetObjectClass(env,aecmConfig);
    jfieldID fCngMode = (*env)->GetFieldID(env, JavaWebRtcAecmConfig, "cngMode", "I");
    jfieldID fEchoMode = (*env)->GetFieldID(env, JavaWebRtcAecmConfig, "echoMode", "I");

    int cngMode = (*env)->GetIntField(env,aecmConfig, fCngMode);
    int echoMode = (*env)->GetIntField(env,aecmConfig, fEchoMode);

    AecmConfig _config;
    _config.cngMode = cngMode;
    _config.echoMode = echoMode;
    
    int ret = -1;
    ret = WebRtcAecm_set_config(instance, _config);
    
    return ret;
}    


#ifdef __cplusplus
}
#endif
#endif
