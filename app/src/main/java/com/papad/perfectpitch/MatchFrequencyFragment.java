package com.papad.perfectpitch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MatchFrequencyFragment.MatchFrequencyFragListener} interface
 * to handle interaction events.
 * Use the {@link MatchFrequencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFrequencyFragment extends Fragment {
    private final String TAG= getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Random rand;

    private Thread mDispatcherThread;
    private AudioDispatcher mDispatcher;
    private PitchDetectionHandler pdh;

    private MatchFrequencyFragListener mListener;

    private MainActivity mActivity;

    public MatchFrequencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchPitchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchFrequencyFragment newInstance(String param1, String param2) {
        MatchFrequencyFragment fragment = new MatchFrequencyFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        mActivity= (MainActivity) getActivity();
        rand= new Random();

        pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                Log.i(TAG, ""+pitchInHz);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) getView().findViewById(R.id.last_result);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_frequency, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.startPitchDetector();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MatchFrequencyFragListener) {
            mListener = (MatchFrequencyFragListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListeners() {
        Button newFreqBtn= (Button) getView().findViewById(R.id.new_freq_btn);
        newFreqBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                randRange();
            }
        });

        Button captureBtn= (Button) getView().findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                captureFreq();
            }
        });
    }

    private void randRange() {
        int range= 0;
        RadioGroup difficulties= (RadioGroup) getView().findViewById(R.id.radio_container);
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

        TextView freqRangeLow= (TextView) getView().findViewById(R.id.freq_range_low);
        freqRangeLow.setText(""+mActivity.frequencies[lowIndex]);
        TextView freqRangeHigh= (TextView) getView().findViewById(R.id.freq_range_high);
        freqRangeHigh.setText(""+mActivity.frequencies[highIndex]);
    }

    private void captureFreq() {

        mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024,
                0);
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        mDispatcher.addAudioProcessor(p);

        mDispatcherThread = new Thread(mDispatcher, "Audio Dispatcher");

        final Timer countdown= new Timer();
        countdown.scheduleAtFixedRate(new TimerTask() {
            private int counter= 3;
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (counter==0) {
                            mDispatcherThread.start();
                            countdown.cancel();
                        }
                        TextView lastResult= (TextView) getView().findViewById(R.id.last_result);
                        lastResult.setText(""+counter--);
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MatchFrequencyFragListener {
        // TODO: Update argument type and name
//        void startPitchDetector();
//        void disableButton(String id);
    }
}
