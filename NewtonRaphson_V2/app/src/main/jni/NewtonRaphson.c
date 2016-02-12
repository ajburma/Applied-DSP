//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include "arm_neon.h"
#include "LogNotify.h"


void
computeFloat(JNIEnv *env, jobject thiz, jobject main, jfloat numerator, jfloat denominator, jint iterations)
{
	//Example that implements Numerator*(1/Denominator) using NEON intrinsics.

	float32x2_t operandA = {numerator, numerator};					//doubleword register
	float32x2_t operandB = {denominator, denominator};				//doubleword register

	float32x2_t temp1 = vrecpe_f32(operandB);						//compute the reciporical estimate
	//notify(env, main, "Estimate: %.12g", temp1[0]);

	int i;
	float32x2_t temp2;
	for(i=0; i<iterations; i++) {									//Loop to increase precision
		temp2 = vrecps_f32(operandB, temp1);						//Newton-Raphson iteration temp2 = 2.0-operandB*temp1;
		temp1 = vmul_f32(temp1, temp2);								//Newton-Raphson iteration temp1 = temp1*temp2
		//notify(env, main, "Iteration: %.12g", temp1[0]);
	}

	temp2 = vmul_f32(temp1, operandA);								//multiply by the numerator
	//notify(env, main, "Reciprocal: %.12g", 1.0/((double)denominator));
	//notify(env, main, "Difference: %.12g", (1.0/((double)denominator))-temp1[0]);
	notify(env, main,"Floating Newton - Result: %.12g", temp2[0]);
}

void
computeSQRT(JNIEnv *env, jobject thiz, jobject main, jfloat numerator, jfloat denominator, jint iterations)
{
	//Example that implements Numerator*(1/Denominator) using NEON intrinsics.

	float32x2_t operandA = {numerator, numerator};					//doubleword register
	float32x2_t operandB = {denominator, denominator};				//doubleword register


	float32x2_t temp1 = vrsqrte_f32(operandB);						//compute the reciporical sqrt estimate

	int i;
	float32x2_t temp2;
	for(i=0; i<iterations; i++) {									//Loop to increase precision
		float32x2_t tempSQ = temp1 * temp1;							//square to get w[n]
		temp2 = vrsqrts_f32(operandB,tempSQ);						//Newton-Raphson iteration for sqrt
		temp1 = vmul_f32(temp1, temp2);								//Newton-Raphson iteration temp1 = temp1*temp2
	}

	temp2 = vmul_f32(temp1, operandA);								//multiply by the numerator
	notify(env, main, "Floating Newton 1/SQRT(%f) = %.12g", denominator, temp1[0]);
}

void
computeFixed(JNIEnv *env, jobject thiz, jobject main, jshort numerator, jshort denominator, jint iterations)
{
	//Fixed point Newton Raphson method
	short v_prev = 0x2000;      //1 in Q2.13
	short two = 0x4000;         //2in Q2.13 format

	short temp;
	int mem_shift = 15, i;		//For Q15 format offset is 15
	int mult_reg;
	double reciprocal,divisor, estimate ;			//For printing

	while (!(denominator & 0x4000)) {		//Round up to 0.5bit and store
		denominator = denominator << 1;
		mem_shift = mem_shift - 1;
	}


	for (i = 0; i < iterations; i++) {		//For each iteration, compute the newton raphson
		mult_reg = denominator * v_prev;
		temp = ((short)(mult_reg >> 15));
		temp = two - temp;
		mult_reg = temp * v_prev;
		v_prev = ((short)(mult_reg >> 15));
		v_prev = v_prev << 2;
	}

	divisor = pow(2.0, -(mem_shift + 13));	//Shift the system back to its original place
	reciprocal = v_prev * divisor;
	estimate = numerator * reciprocal;
	notify(env, main,"Fixed16 Result: %f", estimate);
	notify(env, main, "Fixed16 Reciprocal: %f",reciprocal);
}
//Same Thing as previous function but in 32 bits
void
computeFixedLong(JNIEnv *env, jobject thiz, jobject main, jshort numerator, jshort denominator, jint iterations)
{
	int n = (int) numerator;
	int d = (int) denominator;
	int v_prev = 0x20000000;      //1 in Q2.13 format
	int two =   0x40000000;      //2 in Q2.13 format
	int temp;
	int mem_shift = 31, i;
	unsigned long long int mult_reg = 0;
	double reciprocal,divisor, estimate ;			//For printing

	while (!(d & 0x40000000)) {
		d = d << 1;
		mem_shift = mem_shift - 1;
	}

	for (i = 0; i < iterations; i++) {
		mult_reg = ((unsigned long long int) d) * v_prev;
		temp = ((int)(mult_reg >> 31));
		temp = two - temp;
		mult_reg = ((unsigned long long int) temp) * v_prev;
		v_prev = ((int)(mult_reg >> 31));
		v_prev = v_prev << 2;
	}

	divisor = pow(2.0, -(mem_shift + 29));	//Shift the system back to its original place
	reciprocal = v_prev * divisor;
	estimate = numerator * reciprocal;
	notify(env, main,"Fixed32 Result: %f", estimate);
	notify(env, main, "Fixed32 Reciprocal: %f",reciprocal);

}

////////////////////////////////////////////////////////////////////////////////////////////
// JNI Setup
////////////////////////////////////////////////////////////////////////////////////////////

static JNINativeMethod nativeMethods[] =
	{//		Name						Signature				Pointer
			{"computeFloat", 			"(Lcom/dsp/newtonraphson/Monitor;FFI)V",				(void *)&computeFloat	},
			{"computeFixed", 			"(Lcom/dsp/newtonraphson/Monitor;SSI)V",				(void *)&computeFixed	},
			{"computeFixedLong", 		"(Lcom/dsp/newtonraphson/Monitor;SSI)V",				(void *)&computeFixedLong	},
			{"computeSQRT", 			"(Lcom/dsp/newtonraphson/Monitor;FFI)V",				(void *)&computeSQRT    }
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
		jclass filters = (*env)->FindClass(env, "com/dsp/newtonraphson/NewtonRaphson");
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
