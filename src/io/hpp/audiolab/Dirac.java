package io.hpp.audiolab;

public class Dirac {
	
	public Dirac(){
		init();
	}
    // Native interface function that returns the frequency of a Byte[].
    // This invokes the native c++ routine defined in "dirac-jni.cpp".
	public native final void init();
	public native final double getFrequencyAnalysis();

    // Load the native library upon startup
    static
    {
        System.loadLibrary("dirac");
    }
}
