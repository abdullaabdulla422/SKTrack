LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# OpenCV-trunk and NDK r5b.
LOCAL_C_INCLUDES += $(ANDROID_OPENCV_ROOT)/modules/highgui/include $(ANDROID_OPENCV_ROOT)/modules/calib3d/include $(ANDROID_OPENCV_ROOT)/modules/objdetect/include $(ANDROID_OPENCV_ROOT)/modules/flann/include $(ANDROID_OPENCV_ROOT)/modules/pics/include $(ANDROID_OPENCV_ROOT)/modules/contrib/include $(ANDROID_OPENCV_ROOT)/modules/imgproc/include $(ANDROID_OPENCV_ROOT)/modules/ffmpeg/include $(ANDROID_OPENCV_ROOT)/modules/ml/include $(ANDROID_OPENCV_ROOT)/modules/traincascade/include $(ANDROID_OPENCV_ROOT)/modules/legacy/include $(ANDROID_OPENCV_ROOT)/modules/features2d/include $(ANDROID_OPENCV_ROOT)/modules/core/include $(ANDROID_OPENCV_ROOT)/3rdparty/include $(ANDROID_OPENCV_ROOT)/include
LOCAL_LDLIBS += -L$(ANDROID_OPENCV_ROOT)/android/build-$(TARGET_ARCH_ABI)/lib/ -L$(ANDROID_OPENCV_ROOT)/android/build-$(TARGET_ARCH_ABI)/3rdparty/lib/ -lopencv_calib3d -lopencv_features2d -lopencv_objdetect -lopencv_imgproc -lopencv_video  -lopencv_highgui -lopencv_ml -lopencv_legacy -lopencv_core -lopencv_flann -lzlib -llibpng -llibjpeg -llibjasper -llibtiff -llog


LOCAL_CPP_EXTENSION := cpp
LOCAL_MODULE    := signakey
LOCAL_SRC_FILES := main.cpp Context.cpp Blob.cpp data_processing.cpp image_processing.cpp diagnostic.cpp encoded_mark_translator.cpp

include $(BUILD_SHARED_LIBRARY)
