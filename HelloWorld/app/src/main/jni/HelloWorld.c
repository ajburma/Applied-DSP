//
// Created by axb124530 on 1/15/2016.
//
#include <android/log.h>
#import <jni.h>

float computeValue(float y_old, float x, float a){
    return (a * y_old + x);
}

jfloatArray Java_com_example_axb124530_helloworld_MainActivity_getString (
        JNIEnv* env, jobject thiz ) {
    int i;
    float* response;
    jfloatArray arr = (*env)->NewFloatArray(env,21);

    response = malloc(sizeof(float)*21);
    response[0] = computeValue(0.0,1.0,0.5);


    __android_log_print(ANDROID_LOG_ERROR,
                        "HelloWorld", "Input:0 at 0 Response: 1.0");

    for (i=1; i < 21; i++){
        response[i] = computeValue(response[i-1],0.0,0.5);
        __android_log_print(ANDROID_LOG_ERROR,
                            "HelloWorld", "Input:0 at %i Response: %f", i, response[i]);
    }

    (*env)->SetFloatArrayRegion(env,arr,0,21,response);
    return arr;

}


