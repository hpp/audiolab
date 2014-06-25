    LOCAL_PATH := $(call my-dir)

    include $(CLEAR_VARS)

    LOCAL_MODULE    := dirac-jni
    LOCAL_SRC_FILES := dirac-jni.cpp
    
    LOCAL_INCLUDES := Dirac.h
