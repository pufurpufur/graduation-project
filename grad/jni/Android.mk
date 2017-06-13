LOCAL_PATH  := $(call my-dir)
OPENCV_PATH := C:/Users/anilyildiz/Downloads/android/opencv/android/OpenCV-2.4.11-android-sdk/OpenCV-android-sdk/sdk/native/jni

include $(CLEAR_VARS)
LOCAL_MODULE    := nonfree
LOCAL_SRC_FILES := libnonfree.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES  := on
include $(OPENCV_PATH)/OpenCV.mk

LOCAL_C_INCLUDES :=				\
	$(LOCAL_PATH)				\
	$(OPENCV_PATH)/include

LOCAL_SRC_FILES :=				\
	grad.cpp
	
LOCAL_MODULE := grad
LOCAL_CFLAGS := -Werror -O3 -ffast-math
LOCAL_LDLIBS := -llog -ldl
LOCAL_SHARED_LIBRARIES += nonfree

include $(BUILD_SHARED_LIBRARY)