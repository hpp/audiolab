#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "Dirac.h"

using namespace dirac;
extern "C"

bool dataReady = false;
void* dirac;

typedef struct {
	jobject g_audioBufferFillCB;
	jmethodID g_method;
	JNIEnv *g_env;
} userData;


JNIEXPORT jdouble JNICALL Java_io_hpp_audiolab_Dirac_getFrequencyAnalysis(
		JNIEnv *env
		//jobject thiz,
		){

	//jbyte* bufferPtr = env->GetByteArrayElements(array, NULL);
	return DiracGetProperty(kDiracPropertyPitchCorrectionFundamentalFrequency, dirac);
}

JNIEXPORT void JNICALL Java_io_hpp_audiolab_Dirac_init(
		JNIEnv *env,
		//jobject thiz,
		jobject g_audioBufferFillCB){

	long double time = 1., pitch = 1., formant = 1.;
	int lambda = 1, slur = 10;
	long lambda = 1.0, quality = 0.0, numChannels = 1.0;
	float sampleRateHz = 44100;

	userData.g_method = method;
	userData.g_getSizeIface = getSizeInterface;
	userData.g_env = env;


	dirac = DiracCreate(kDiracLambdaPreview+lambda, kDiracQualityPreview+quality,
			numChannels, sampleRateHz, WrapperFunc, *userData);		// (1) fastest
	if (!dirac) {
		printf("!! ERROR !!\n\n\tCould not create DIRAC instance\n\tCheck number of channels and sample rate!\n");
		exit(-1);
	}

	// Pass the values to our DIRAC instance
	DiracSetProperty(kDiracPropertyDoPitchCorrection, 1, dirac);
	DiracSetProperty(kDiracPropertyPitchCorrectionSlurTime, slur, dirac);
	DiracSetProperty(kDiracPropertyTimeFactor, time, dirac);
	DiracSetProperty(kDiracPropertyPitchFactor, pitch, dirac);
	DiracSetProperty(kDiracPropertyFormantFactor, formant, dirac);
}

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
 This is the callback function that supplies data from the input stream/file(s) whenever needed.
 It should be implemented in your software by a routine that gets data from the input/buffers.
 The read requests are *always* consecutive, ie. the routine will never have to supply data out
 of order.
 */
long myReadData(float **chdata, long numFrames, void *userData)
{
	// The userData parameter can be used to pass information about the caller (for example, "this") to
	// the callback so it can manage its audio streams.

	if (!chdata)	return 0;
	long res = numFrames;


	long channel = 0;
	for (long v = 0; v < state->sNumFiles; v++) {
		mAiffReadData(state->sInFileNames[v], chdata+channel, state->sReadPosition, numFrames, state->sInFileNumChannels[v]);
		channel += state->sInFileNumChannels[v];
	}

	state->sReadPosition += numFrames;

	return res;

}

int WrapperFunc(int *id)
{
      jint retval;
      //marshalling an int* to a m_SizeClass boogy-woogy.
      //...
      g_env->ExceptionClear();
      retval = g_env->CallIntMethod(g_audioBufferFillCB, g_method,
                                    /*marshalled m_SizeClass*/);
      if(g_env->ExceptionOccured()){
          //panic! Light fires! The British are coming!!!
          //Log.d(TAG,"Runtime exception occured");
          g_env->ExceptionClear();
      }
      return rvalue;
}
