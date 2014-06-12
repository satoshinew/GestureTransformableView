LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := GestureTransformableView
LOCAL_SRC_FILES := GestureTransformableView.cpp
LOCAL_LDLIBS    := -llog
APP_ABI := armeabi armeabi-v7a

include $(BUILD_SHARED_LIBRARY)