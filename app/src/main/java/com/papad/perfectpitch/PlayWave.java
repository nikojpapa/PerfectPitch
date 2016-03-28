package com.papad.perfectpitch;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.AsyncTask;
import android.util.Log;
import java.util.ArrayList;

import java.lang.reflect.Array;


/**
 * Skeleton written by Dr. Douglas C. Schmidt at Vanderbilt University
 *
 * Defines a generic framework for running an AsyncTask that delegates
 * its operations to the @a Ops parameter.
 */
public class PlayWave
        extends AsyncTask<Double, Void, Void> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Generate instance of AudioManager
     */
//    AudioManager am= Context.getSystemService(Context.AUDIO_SERVICE);

    /**
     * Reference to the enclosing Ops object.
     */
//    protected Ops mOps;

    /**
     * Constructor initializes the field.
     */
//    public GenericAsyncTask(Ops ops) {
//        mOps = ops;
//    }

    /**
     * Called in the UI thread prior to running doInBackground() in a
     * background thread.
     */
    // @@ Omit until Android supports default methods in interfaces..
    // @Override
    // protected void onPreExecute() {
    //     mOps.onPreExecute();
    // }

    /**
     * Converts Arraylist<Float> to float[]
     */
//    protected float[] toFloatArray(ArrayList<Float> arrayList) {
//        float[] array= new float[arrayList.size()];
//        for (int i=0; i<arrayList.size(); i++) {
//            array[i]= arrayList.get(i);
//        }
//
//        return array;
//    }

    /**
     * Called in a background thread to process the @a params.
     *
     * wave generation and audio mangement taken from http://stackoverflow.com/questions/14842754/duration-of-a-generated-audio-sine-wave
     */
    @SuppressWarnings("unchecked")
    protected Void doInBackground(Double... frequency) {

        int sampleRate = 44100;
        int duration= 2;
        double freqOfTone = frequency[0];
        AudioTrack track;

        short[] samples = new short[sampleRate*duration*2];
        track = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, samples.length,
                AudioTrack.MODE_STATIC);

        double angle = 0;
        double increment = (2 * Math.PI * freqOfTone / sampleRate); // angular increment

        for (int i = 0; i < samples.length-1; i++) {
            samples[i] =   (short) (Math.sin(angle) * Short.MAX_VALUE);
            angle += increment;
        }
        track.write(samples, 0, samples.length); // write data to audio hardware
        track.play();

        return null;
    }

    /**
     * Called in the UI thread to process the @a result.
     */
    protected void onPostExecute(String result) {
        Log.i(TAG, "Probably worked...?");
    }
}