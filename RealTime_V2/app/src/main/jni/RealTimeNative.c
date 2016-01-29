//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

static int frameSize, delayTime;
static int order = 12;
static float *buffer = NULL;

static double coeffs[] = {0.0311877009838756, -0.0146995512360664, -0.0819729557864041, 0.00273929607168683, 0.186691140568207, 0.378547772724477, 0.378547772724477, 0.186691140568207, 0.00273929607168683, -0.0819729557864041, -0.0146995512360664, 0.0311877009838756};

void
checkRange(double *value)
{
	if (*value > 1.0){
		*value = 1.0;
	} else if (*value < -1.0) {
		*value = -1.0;
	}
}

jfloatArray
Java_com_dsp_realtime_Filters_compute(JNIEnv *env, jobject thiz, jshortArray input)
{
	double temp;
	int i, j, idx;

	short *_in = (*env)->GetShortArrayElements(env, input, NULL);

	for(i=0; i<frameSize; i++)
	{
		buffer[i] = buffer[frameSize + i];
		buffer[frameSize + i] = _in[i]/32768.0f;
	}

	(*env)->ReleaseShortArrayElements(env, input, _in, 0);

	jfloatArray output = (*env)->NewFloatArray(env, frameSize);
	float *_output = (*env)->GetFloatArrayElements(env, output, NULL);

	for(i=0;i<frameSize;i++)
	{
		temp = 0.0;
		for(j=0;j<order;j++)
		{
			idx = frameSize + (i - j);
			temp += buffer[idx]*coeffs[j];
		}

		checkRange(&temp);
		_output[i] = temp;
	}

	(*env)->ReleaseFloatArrayElements(env, output, _output, 0);
	usleep(delayTime);
	return output;
}

void
Java_com_dsp_realtime_Filters_initialize(JNIEnv *env, jobject thiz, int fsize, int delay)
{
	frameSize = fsize;
	delayTime = delay;
	if(buffer != NULL){
		free(buffer);
		buffer = NULL;
	}
	buffer = (float *) calloc(2*fsize,sizeof(float));
}

void
Java_com_dsp_realtime_Filters_finish(JNIEnv *env, jobject thiz)
{
	if(buffer != NULL){
		free(buffer);
		buffer = NULL;
	}
}
