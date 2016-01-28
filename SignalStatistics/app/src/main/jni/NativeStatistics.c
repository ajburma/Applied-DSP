#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include "LogNotify.h"
#include "randomNumbers.h"
#include <android/log.h>


//Insertion sort method
void insertionSort(float arr[]){
    int i;
    int j;
    float temp;

    //Sort high elements to the back
    for(i= 0; i < LENGTH - 2; i++){
        for(j = 0; j < LENGTH-1-i; j++){
            if(arr[j] > arr[j+1]){
                temp = arr[j];
                arr[j] = arr[j+1];
                arr[j+1] = temp;
            }
        }
    }
}

float calculateMean(float arr[]){
    int i;
    float sum = 0;

    for (i = 0; i < (int)LENGTH; i++)
        sum += arr[i];

    return (sum/LENGTH);
}

JNIEXPORT void JNICALL
Java_com_dsp_signalstatistics_NativeStatistics_mean(JNIEnv *env, jclass type,
                                                             jobject guiCallback) {
   notify(env, guiCallback,"Mean: %.12g", calculateMean(RND));
}

JNIEXPORT void JNICALL
Java_com_dsp_signalstatistics_NativeStatistics_median(JNIEnv *env, jclass type,
                                                             jobject guiCallback) {
    float median, arr[20];
    int i;

    for(i = 0; i < LENGTH; i++)
    arr[i] = RND[i];

    insertionSort(arr);

    if(LENGTH % 2 == 0)
        median = (arr[(LENGTH - 2)/2] + arr[LENGTH/2])/2.0;
    else
        median = arr[(LENGTH+1)/2];

    notify(env, guiCallback,"Median: %.12g", median);
}

JNIEXPORT void JNICALL
Java_com_dsp_signalstatistics_NativeStatistics_stdDev(JNIEnv *env, jclass type,
                                                             jobject guiCallback) {
    // Example showing how text is sent to the UI from native code
    float std = 0.0;
    float mean;
    int i;

    //Calculate Mean for STD DEV
    mean = calculateMean(RND);

    for(i = 0; i < LENGTH; i++)
        std += pow((RND[i] - mean),2.0);

    //Divide by N and sqrt
    std= std/(float)LENGTH;
    std= sqrt(std);

    notify(env, guiCallback,"STD: %.12g", std);
}

JNIEXPORT void JNICALL
Java_com_dsp_signalstatistics_NativeStatistics_cov(JNIEnv *env, jclass type,
                                                             jobject guiCallback) {
    float cov = 0;
    float xMean = 0;
    float yMean = 0;
    int i;

    //Calculate both means
    xMean = calculateMean(RND);
    yMean = calculateMean(RND2);

    for(i = 0; i < LENGTH; i++)
        cov += (RND[i] - xMean) * (RND2[i] - yMean);

    //Divide by N - 1
    cov = cov / (LENGTH - 1);

    notify(env, guiCallback,"Covariance: %.12g", cov);
}

