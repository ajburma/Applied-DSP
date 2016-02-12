//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
#include "LogNotify.h"

// use vsnprintf to create char* with the line to be appended
// truncated to the size of the str buffer

//USAGE: notify(env, "%d processes completed in  %f ms", 19, 482.1904443);
void notify(JNIEnv *env, jobject main, const char* format, ...)
{
	jmethodID notifyID;
	va_list args;
	jstring jText;

	notifyID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, main), "notify", "(Ljava/lang/String;)V");
	if (notifyID != NULL) {
		va_start(args, format);
		vsnprintf(str, sizeof(str), format, args);
		va_end(args);
		jText = (*env)->NewStringUTF(env, str);
		(*env)->CallVoidMethod(env, main, notifyID, jText);
		if ((*env)->ExceptionCheck(env)) {
			return;
		}
		(*env)->DeleteLocalRef(env, jText);
	} else {
        if ((*env)->ExceptionCheck(env)) {
        	(*env)->ExceptionClear(env);
        }
	}
}
