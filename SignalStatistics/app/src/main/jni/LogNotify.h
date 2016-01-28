#ifndef LOGNOTIFY_H_
#define LOGNOTIFY_H_


#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>

char str[50];
void notify(JNIEnv *env, jobject main, const char* format, ...);

#endif
