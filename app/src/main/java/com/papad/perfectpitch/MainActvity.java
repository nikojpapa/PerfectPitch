package com.papad.perfectpitch;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActvity extends AppCompatActivity implements GuessFrequencyFragment
        .GuessFrequencyFragListener, MatchFrequencyFragment.MatchFrequencyFragListener {
    protected final String TAG = getClass().getSimpleName();

    private double[] frequencies= {27.5, 29.1352, 30.8677, 32.7032, 34.6478, 36.7081, 38.8909, 41.2034, 43.6535, 46.2493, 48.9994, 51.9131, 55, 58.2705, 61.7354, 65.4064, 69.2957, 73.4162, 77.7817, 82.4069, 87.3071, 92.4986, 97.9989, 103.826, 110, 116.541, 123.471, 130.813, 138.591, 146.832, 155.563, 164.814, 174.614, 184.997, 195.998, 207.652, 220, 233.082, 246.942, 261.626, 277.183, 293.665, 311.127, 329.628, 349.228, 369.994, 391.995, 415.305, 440, 466.164, 493.883, 523.251, 554.365, 587.33, 622.254, 659.255, 698.456, 739.989, 783.991, 830.609, 880, 932.328, 987.767, 1046.5, 1108.73, 1174.66, 1244.51, 1318.51, 1396.91, 1479.98, 1567.98, 1661.22, 1760, 1864.66, 1975.53, 2093, 2217.46, 2349.32, 2489.02, 2637.02, 2793.83, 2959.96, 3135.96, 3322.44, 3520, 3729.31, 3951.07, 4186.01};
    private String[] noteNames= {"a0", "as0", "b0", "c1", "cs1", "d1", "ds1", "e1", "f1", "fs1", "g1", "gs1", "a1", "as1", "b1", "c2", "cs2", "d2", "ds2", "e2", "f2", "fs2", "g2", "gs2", "a2", "as2", "b2", "c3", "cs3", "d3", "ds3", "e3", "f3", "fs3", "g3", "gs3", "a3", "as3", "b3", "c4", "cs4", "d4", "ds4", "e4", "f4", "fs4", "g4", "gs4", "a4", "as4", "b4", "c5", "cs5", "d5", "ds5", "e5", "f5", "fs5", "g5", "gs5", "a5", "as5", "b5", "c6", "cs6", "d6", "ds6", "e6", "f6", "fs6", "g6", "gs6", "a6", "as6", "b6", "c7", "cs7", "d7", "ds7", "e7", "f7", "fs7", "g7", "gs7", "a7", "as7", "b7", "c8"};

    private Random rand= new Random();

    Thread mDispatcherThread;
    AudioDispatcher mDispatcher;
    PitchDetectionHandler pdh;

    private FragmentManager mFragmentManager;
    private Fragment mCurrentFrag;

//    TabLayout.Tab guessTab, matchTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_actvity);

        mCurrentFrag= (Fragment) new GuessFrequencyFragment();
        mFragmentManager= getSupportFragmentManager();
        mFragmentManager.enableDebugLogging(true);
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, mCurrentFrag);
        fragmentTransaction.commit();

//        TabLayout tabLayout = new TabLayout(getApplicationContext());
//
//        guessTab= tabLayout.newTab().setText("Guess Frequency");
//        matchTab= tabLayout.newTab().setText("Match Frequency");
//        tabLayout.addTab(guessTab);
//        tabLayout.addTab(matchTab);

//        tabLayout.setOnTabSelectedListener();

        pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                Log.i(TAG, ""+pitchInHz);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.last_result);
                        if (pitchInHz!=-1) {
                            text.setText("" + pitchInHz);
                            mDispatcher.stop();
                        } else {
                            text.setText("...");
                        }
                    }
                });
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        if (id==R.id.mic_button) {
            if (mCurrentFrag instanceof MatchFrequencyFragment) {
                return true;
            } else {
                Log.i(TAG, "new matchFrag");
                mCurrentFrag= new MatchFrequencyFragment();
            }
        } else if (id==R.id.ear_button) {
            if (mCurrentFrag instanceof GuessFrequencyFragment) {
                return true;
            } else {
                Log.i(TAG, "new freqFrag");
                mCurrentFrag= new GuessFrequencyFragment();
            }
        } else if (id==R.id.graph_button) {
            Intent graphIntent= new Intent(this, DynamicXYPlotActivity.class);
            startActivity(graphIntent);
            return true;
        }
        FragmentTransaction fragmentTransaction= mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, mCurrentFrag);
        fragmentTransaction.commit();

        return super.onOptionsItemSelected(item);
    }

    private void randRange() {
        int range= 0;
        RadioGroup difficulties= (RadioGroup) findViewById(R.id.radio_container);
        switch (difficulties.getCheckedRadioButtonId()) {
            case (R.id.radio_easy):
                range= 44;
                break;
            case (R.id.radio_medium):
                range= 22;
                break;
            case (R.id.radio_hard):
                range= 11;
                break;
        }
        int lowIndex= rand.nextInt(88-range);
        int highIndex= lowIndex+range;
//        HashMap<String, Integer> range= new HashMap<String, Integer>(2);
//        range.put("lowIndex", lowIndex);
//        range.put("highIndex", highIndex);

        TextView freqRangeLow= (TextView) findViewById(R.id.freq_range_low);
        freqRangeLow.setText(""+frequencies[lowIndex]);
        TextView freqRangeHigh= (TextView) findViewById(R.id.freq_range_high);
        freqRangeHigh.setText(""+frequencies[highIndex]);
    }

    private void captureFreq(final int delayInSeconds) {

        mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024,
                0);
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        mDispatcher.addAudioProcessor(p);

        mDispatcherThread = new Thread(mDispatcher, "Audio Dispatcher");

        final Timer countdown= new Timer();
        countdown.scheduleAtFixedRate(new TimerTask() {
            private int counter= delayInSeconds;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (counter==0) {
                            mDispatcherThread.start();
                            countdown.cancel();
                        }
                        TextView lastResult= (TextView) findViewById(R.id.last_result);
                        lastResult.setText(""+counter--);
                    }
                });
            }
        }, 0, 1000);
    }

    private void playWave() {
        int newIndex= rand.nextInt(88);
        double waveFreq= frequencies[newIndex];
        String noteName= noteNames[newIndex];

        Toast.makeText(this, "Playing " + noteName + " at " + waveFreq + "Hz", Toast
                .LENGTH_SHORT).show();
        new PlayWave().execute(waveFreq);
    }

    public void startGuessFrag() {
        Log.i(TAG, "play button initiated");
        Button play440= (Button) findViewById(R.id.playButton);
        play440.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                playWave();
            }
        });
    }

    public void startPitchDetector() {
        Button newFreqBtn= (Button) findViewById(R.id.new_freq_btn);
        newFreqBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                randRange();
            }
        });

        Button captureBtn= (Button) findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                captureFreq(3);
            }
        });
    }
}
