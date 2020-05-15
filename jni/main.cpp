#include <jni.h>
#include <string.h>
#include <stdio.h>

#include <opencv2/core/core_c.h>
#include <opencv2/imgproc/imgproc_c.h>
#include <opencv2/highgui/highgui_c.h>

#include "encoded_mark_translator.h"


const int KEY_WIDTH = 16;
const int KEY_HEIGHT = 16;

int debug_frame_count = 0;

const int MODE_Y = 1;
const int MODE_V = 2;

jint recognize(JNIEnv* env, jobject thiz, CvMat* image, jbyteArray output_key, bool crop, int shrink_percent, bool blackbg, bool enhance, int symbol_mode) {
    // Prepare context for SignaKey recognition.
    void *cxt = mark_finder_init(KEY_WIDTH, KEY_HEIGHT);
    strcpy(((Context *)cxt)->symbolName, (MODE_Y==symbol_mode) ? "Y" : "V");
    fill_mark_finder(cxt, image, false, blackbg);

    Context* cxt_ = (Context*) cxt;
    if (crop) {
        int size = image->rows > image->cols ? image->cols : image->rows;
        // Set region of interest.
        cxt_->regionUpperLeft.x = (image->cols-size)/2;
        cxt_->regionUpperLeft.y = (image->rows-size)/2;
        cxt_->regionLowerRight.x = cxt_->regionUpperLeft.x + size - 1;
        cxt_->regionLowerRight.y = cxt_->regionUpperLeft.y + size - 1;
    }
    else {
        cxt_->regionUpperLeft.x = 0;
        cxt_->regionUpperLeft.y = 0;
        cxt_->regionLowerRight.x = image->cols - 1;
        cxt_->regionLowerRight.y = image->rows - 1;
    }

    if (shrink_percent > 0) {
        int w = cxt_->regionLowerRight.x - cxt_->regionUpperLeft.x + 1;
        int h = cxt_->regionLowerRight.y - cxt_->regionUpperLeft.y + 1;
        int step_x = w * double(shrink_percent) / 100.0;
        int step_y = h * double(shrink_percent) / 100.0;
        cxt_->regionUpperLeft.x += step_x;
        cxt_->regionUpperLeft.y += step_y;
        cxt_->regionLowerRight.x -= step_x;
        cxt_->regionLowerRight.y -= step_y;
    }

    // Recognize SignaKey.
    char* result = (char*)mark_finder_translate(cxt, image, enhance);

    // If result is not null.
    jint confidence = -1;  // -1 means that key was not recognized (result is null)
    if (result != NULL) {
        const size_t key_size = KEY_WIDTH*KEY_HEIGHT;

        // Copy result to output buffer.
        env->SetByteArrayRegion(output_key, 0, (jsize)key_size, (jbyte*)result);

        // Calculate confidence.
        confidence = 0; // 0 means that key was not recognized, but result is not null
        for (int i=0; i<key_size; i++) {
            if ((result[i] >= 0) && (result[i] <= 3)) {
                confidence++;
            }
        }

        // Free memory of result.
        free(result);
    }

    // Free memory of context and image.
    mark_finder_free(cxt);

    return confidence;
}



extern "C"
jint Java_com_signakey_skscanner_SignaKeyRecognizer_recognizeFromBuffer(JNIEnv* env, jobject thiz, jbyteArray input_image, jint h, jint w, jbyteArray output_key, bool crop, bool blackbg, jint symbol_mode) {
    // Make OpenCV image from buffer.
    CvMat* image = cvCreateMat(h, w, CV_8UC1);
    env->GetByteArrayRegion(input_image, (jint)0, (jint)(w*h), (jbyte*)(image->data.ptr));

    // Recognize.
    int shrink = 0;
    jint confidence = -1;
    bool enhance = false;
    while ((shrink <= 30) && (confidence <= 106)) {
        confidence = recognize(env, thiz, image, output_key, crop, shrink, blackbg, enhance, symbol_mode);
		shrink += 15;
    }

    // Release image.
    cvReleaseMat(&image);

    debug_frame_count++;
    return confidence;
}



extern "C"
jint Java_com_signakey_skscanner_SignaKeyRecognizer_recognizeFromPath(JNIEnv* env, jobject thiz, jstring path, jbyteArray output_key, bool crop, bool blackbg, jint symbol_mode) {
    // Load image.
    const char* sz_path = env->GetStringUTFChars(path, 0);
    CvMat* image = cvLoadImageM(sz_path, CV_LOAD_IMAGE_GRAYSCALE);

    jint confidence = 0;
    if (image != NULL) {
        // Recognize.
        int shrink = 0;
        bool enhance = false;
        while ((shrink <= 30) && (confidence <= 106)) {
            confidence = recognize(env, thiz, image, output_key, crop, shrink, blackbg, enhance, symbol_mode);
			shrink += 15;
        }

        // Release image.
        cvReleaseMat(&image);
    }

    debug_frame_count++;
    return confidence;
}
