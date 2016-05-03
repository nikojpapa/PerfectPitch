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
import java.util.List;


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
    protected String mInstrument;
    protected double mWaveFreq;

    /**
     * Constructor initializes the field.
     */
    public PlayWave(String instrumentName,double freq ) {
        mInstrument = instrumentName;
        mWaveFreq = freq;
    }

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

    private ArrayList<double[]> makeSpectrum(String instrument, double freq){

        if(instrument.equals("clarinet")){
            ArrayList<double[]> clarinet_ = new ArrayList<double[]>();
            double[] f1= {freq,0.314,0.0};
            double[] f2= {freq*3,.236,0.0};
            double[] f3= {freq*5,0.157,0.0};
            double[] f4= {freq*7,0.044,0.0};
            double[] f5= {freq*9,0.157,0.0};
            double[] f6= {freq*11,0.038,0.0};
            double[] f7= {freq*13,0.053,0.0};

            clarinet_.add(f1);
            clarinet_.add(f2);
            clarinet_.add(f3);
            clarinet_.add(f4);
            clarinet_.add(f5);
            clarinet_.add(f6);
            clarinet_.add(f7);

            return clarinet_;
        }
        else if(instrument.equals("bell")){
            ArrayList<double[]> bell = new ArrayList<double[]>();
            double[] f1= {freq,0.1666,0.0};
            double[] f2= {freq*2,0.1666,0.0};
            double[] f3= {freq*3,0.1666,0.0};
            double[] f4= {freq*4.2,0.1666,0.0};
            double[] f5= {freq*5.4,0.1666,0.0};
            double[] f6= {freq*6.8,0.1666,0.0};

            bell.add(f1);
            bell.add(f2);
            bell.add(f3);
            bell.add(f4);
            bell.add(f5);
            bell.add(f6);

            return bell;
        }
        else if(instrument.equals("guitar")){
            ArrayList<double[]> guitar = new ArrayList<double[]>();
            double[] f1= {freq*0.7272, .00278,0.0};
            double[] f2= {freq, 0.0598,0.0};
            double[] f3= {freq*2, .2554,0.0};
            double[] f4= {freq*3, .0685,0.0};
            double[] f5= {freq*4, .0029,0.0};
            double[] f6= {freq*5, .0126,0.0};
            double[] f7= {freq*6, .0154,0.0};
            double[] f8= {freq*7, .0066,0.0};
            double[] f9= {freq*8, .0033,0.0};
            double[] f10= {freq*11.0455, .0029,0.0};
            double[] f11= {freq*12.0455, .0094,0.0};
            double[] f12= {freq*13.0455, .0010,0.0};
            double[] f13= {freq*14.0455, .0106,0.0};
            double[] f14= {freq*15.0455, .0038,0.0};

            guitar.add(f1);
            guitar.add(f2);
            guitar.add(f3);
            guitar.add(f4);
            guitar.add(f5);
            guitar.add(f6);
            guitar.add(f7);
            guitar.add(f8);
            guitar.add(f9);
            guitar.add(f10);
            guitar.add(f11);
            guitar.add(f12);
            guitar.add(f13);
            guitar.add(f14);

            return guitar;


        }
        else{
            return null;
        }
    }

    protected short[] createSineWave(ArrayList<double[]> spectrum, int duration){
        short[] line = new short[duration*44100];

        for(double[] wave : spectrum){
            short[] line2 = createSimpleSineWave(wave[0], wave[1], wave[2], duration);
            //Log.i("line2 Length", "line2 length = " + line2.length);
            for(int i = 0; i<line.length; i++){
                line[i] += line2[i];
            }
        }

       /* for(int i = 0; i <= 20; i++){
            Log.i("Sine Array", "Index at " + i + " == " + line[i]);
        }*/

        return line;
    }

    protected short[] createSimpleSineWave(double frequency, double amp, double phase, int duration){
        int length = duration * 44100;
        double a = amp*(Math.pow(2,15)-1);
        short[] wave = new short[length];

        double angle = 0;
        double increment = (2 * Math.PI * frequency / 44100); // angular increment

        for(int i = 0; i < length; i++){
            wave[i] =   (short) (Math.sin(angle) * Short.MAX_VALUE);
            angle += increment;
        }

        return wave;
    }

    protected Void doInBackground(Double... frequency) {

        int sampleRate = 44100;
        int duration= 2;
        double freqOfTone = mWaveFreq;
        AudioTrack track;

        short[] samples = new short[sampleRate*duration];


        // Generates Sin Wave
        double angle = 0;
        double increment = (2 * Math.PI * freqOfTone / sampleRate); // angular increment

        for (int i = 0; i < samples.length-1; i++) {
            samples[i] =   (short) (Math.sin(angle) * Short.MAX_VALUE);
            angle += increment;
        }

        //Log.d("myTag", "This is my message");

        ArrayList<double[]> spectrum = makeSpectrum(mInstrument, mWaveFreq);
        //Log.d("Value", "Hello World -- " + spectrum);
        short[] samples2 = createSineWave(spectrum, 2);

        track = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, samples2.length,
                AudioTrack.MODE_STATIC);

        /*for(int i = 0; i <= 20; i++){
            Log.i("SAMPLE Sine Array", "Index at " + i + " == " + samples[i]);
        }*/

        //Log.d("myTag2", "This is my message2");

        track.write(samples2, 0, samples2.length); // write data to audio hardware
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