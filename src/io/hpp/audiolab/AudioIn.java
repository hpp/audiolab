package io.hpp.audiolab;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;


public class AudioIn {
	private static final String TAG = "io.hpp.AudioIn";
	//some useful constants
	private static int sampleRate =44100;
	private static int numBytePerFrame = 2;
	private static int numChannels = 1;
	private volatile int bufferSize = 1024;
	
	//instances
	private AudioRecord mAudioRecord;
	protected ByteBuffer inputByteBuffer;
	HandlerThread audioInThread;
	audioInByteBufferListener audioProcessCB;
	
	/**
	 * A public class for setting up capture from mic.	
	 */
	public AudioIn(audioInByteBufferListener cb){
		audioProcessCB = cb;
		inputByteBuffer = ByteBuffer.allocateDirect(bufferSize);
		
		int AudioRecordBufferSize = calcRecordBufferSize(sampleRate,bufferSize); 
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,  sampleRate, 
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, AudioRecordBufferSize);
		
		int setPositionOK = mAudioRecord.setPositionNotificationPeriod(bufferSize/numBytePerFrame); // in frames
		audioInThread = new HandlerThread("AudioIn");
		audioInThread.start();
		mAudioRecord.setRecordPositionUpdateListener(mRecordListener, new Handler(audioInThread.getLooper()));
		if (setPositionOK != AudioRecord.SUCCESS){
			Log.d(TAG,"Recording not initiallized properly!");
		}
	}
	
	/*==========================================================================
	 * Public Methods
	 *==========================================================================
	 */
	
	public void start(){
		mAudioRecord.startRecording();
		mAudioRecord.read(inputByteBuffer, sampleRate);
	}
	
	public void stop(){
		mAudioRecord.stop();
	}
	
	public void release(){
		mAudioRecord.release();
		mAudioRecord = null;
		audioInThread = null;
	}
	
	/*==========================================================================
	 * Listeners and interfaces
	 *==========================================================================
	 */
	
	private OnRecordPositionUpdateListener mRecordListener = new OnRecordPositionUpdateListener(){
		@Override
		public void onMarkerReached(AudioRecord recorder) {}
		@Override
		public void onPeriodicNotification(AudioRecord recorder) {
			mAudioRecord.read(inputByteBuffer, bufferSize);
			audioProcessCB.audioInByteBuffer(inputByteBuffer.array());
		}
	};
	
	/**
	 * An Interface for dealing with the input audio.
	 * @author izzy
	 *
	 */
	public interface audioInByteBufferListener{
		void audioInByteBuffer(byte[] inAudio);
	}
	
	/*==========================================================================
	 * Utility Functions
	 *==========================================================================
	 */
	
	private int calcRecordBufferSize(int sample_rate, int buffer_size) {
		
		int recordBufferSize = AudioRecord.getMinBufferSize(sample_rate, AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		
		while (recordBufferSize<buffer_size){
			recordBufferSize *= 2;
		}
		recordBufferSize += (recordBufferSize%buffer_size);
		return recordBufferSize;
	}
	

}
