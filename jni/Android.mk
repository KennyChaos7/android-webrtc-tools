LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := wzc_webrtc_ns
LOCAL_CFLAGS += -DWEBRTC_POSIX

MY_C_LIST := $(wildcard $(LOCAL_PATH)/*.c)
LOCAL_SRC_FILES := $(MY_C_LIST:$(LOCAL_PATH)/%=%)

include $(BUILD_SHARED_LIBRARY)
