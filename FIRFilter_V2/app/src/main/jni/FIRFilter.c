//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <android/log.h>

#define ORDER 32
#define WORDLENGTH 16 //Q FORMAT

typedef struct Variables {
	int frameSize;
	short* inputBuffer;
} Variables;

static const int16_t COEFFS[ORDER] = {
		//Quantized coefficients from MATLAB
};

unsigned int addGetStatus(short a, short b, short *c) {
	/*
	 * Converted from separate .S assembler file to inline assembly due to technical limitations
	 *
		addStatus:
			@ r0 = input opA and sum output address
			@ r1 = input opB and status output address
		ldr				r2,		[r0]
		ldr				r3,		[r1]
		adds			r2,		r2,		r3
		mrs				r3,		APSR
		str				r2,		[r0]
		str				r3,		[r1]
		bx				lr
	*/

	//performs the operation a+b=c and returns the status register
	int A = a << 16;
	int B = b << 16;
	int C = 0;
	unsigned int status = 0;
	asm volatile(
		"adds %[opC], %[opA], %[opB];"
		"mrs %[stat], CPSR;"
		:[opC] "=r" (C), [stat] "=r" (status) 	//output registers
		:[opA] "r" (A), [opB] "r" (B)			//input registers
		:"cc"									//clobber list
	);
	(*c) = C >> 16;
	return status;
}

jshortArray
compute(JNIEnv *env, jobject thiz, jlong memoryPointer, jshortArray input)
{
	Variables* inParam = (Variables*) memoryPointer;
	int temp;
	int i, j, idx, n;
	n = inParam->frameSize;

	short *_in = (*env)->GetShortArrayElements(env, input, NULL);

	for(i=0; i<n; i++)
	{
		inParam->inputBuffer[i] = inParam->inputBuffer[n + i];
		inParam->inputBuffer[n + i] = _in[i];
	}

	(*env)->ReleaseShortArrayElements(env, input, _in, 0);

	jshortArray output = (*env)->NewShortArray(env, n);
	short *_output = (*env)->GetShortArrayElements(env, output, NULL);

	for(i=0;i<n;i++)
	{
		temp = 0;
		for(j=0;j<ORDER;j++)
		{
			idx = n + (i - j);
			temp += (inParam->inputBuffer[idx]*COEFFS[j])<<(16-(WORDLENGTH)+1);
		}

		_output[i] = (short) (temp>>16);
	}

	(*env)->ReleaseShortArrayElements(env, output, _output, 0);
	return output;
}

jlong
initialize(JNIEnv *env, jobject thiz, jint framesize)
{
	Variables* inParam = (Variables*) malloc(sizeof(Variables));
	inParam->frameSize = framesize;
	inParam->inputBuffer = (short*) calloc(2*framesize,sizeof(short));

	//example for status register
	unsigned int status;
	short result;

	short A = 32767;
	short B = 32767;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	A = 32767;
	B = -32768;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	A = 10;
	B = 11;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	A = -10;
	B = -10;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	A = 100;
	B = -1000;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	A = -100;
	B = -32768;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	A = 32767;
	B = 1000;
	status = addGetStatus(A, B, &result);
	__android_log_print(ANDROID_LOG_ERROR, "Add Status", "A: %d, B: %d, C: %d, Status: %#010x", A, B, result, status);

	return (jlong)inParam;
}

void
finish(JNIEnv* env, jobject thiz, jlong memoryPointer)
{
	Variables* inParam = (Variables*) memoryPointer;
	if(inParam != NULL){
		if(inParam->inputBuffer != NULL){
			free(inParam->inputBuffer);
			inParam->inputBuffer = NULL;
		}
		free(inParam);
		inParam = NULL;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////
// JNI Setup
////////////////////////////////////////////////////////////////////////////////////////////

static JNINativeMethod nativeMethods[] =
	{//		Name					Signature		Pointer
			{"compute", 			"(J[S)[S",		(void *)&compute		},
			{"initialize",			"(I)J",			(void *)&initialize		},
			{"finish",				"(J)V",			(void *)&finish			}

	};

jint
JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env;
	jint result;
	//get a hook to the environment
	result = (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6);
	if (result == JNI_OK) {
		//find the java class to hook the native methods to
		jclass filters = (*env)->FindClass(env, "com/dsp/firfilter/FIRFilter");
		if (filters != NULL) {
			result = (*env)->RegisterNatives(env, filters, nativeMethods, sizeof(nativeMethods)/sizeof(nativeMethods[0]));
			(*env)->DeleteLocalRef(env, filters);
			if(result == JNI_OK){
				return JNI_VERSION_1_6;
			} else {
				//something went wrong with the method registration
				return JNI_ERR;
			}
		} else {
			//class wasn't found
			return JNI_ERR;
		}
	} else {
		//could not get environment
		return JNI_ERR;
	}
}
