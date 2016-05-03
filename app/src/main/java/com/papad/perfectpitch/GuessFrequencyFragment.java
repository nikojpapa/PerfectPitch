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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuessFrequencyFragment.GuessFrequencyFragListener} interface
 * to handle interaction events.
 * Use the {@link GuessFrequencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuessFrequencyFragment extends Fragment {
    private final String TAG= getClass().getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private Random rand;
    private MainActivity mActivity;
    private String instrument = "";
    private double current_freq;
    private String current_name;

    private GuessFrequencyFragListener mListener;

    public GuessFrequencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuessFrequencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuessFrequencyFragment newInstance(String param1, String param2) {
        GuessFrequencyFragment fragment = new GuessFrequencyFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guess_frequency, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.setPlayButtonListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GuessFrequencyFragListener) {
            mListener = (GuessFrequencyFragListener) context;
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

    private void playWave(String instrument, double waveFreq, String noteName) {
//        Toast.makeText(getContext(), "Playing " + noteName + " at " + waveFreq + "Hz", Toast
//                .LENGTH_SHORT).show();
        new PlayWave(instrument, waveFreq).execute(waveFreq);
    }

    public void setListeners() {
        Log.i(TAG, "play button initiated");

        RadioGroup instruments= (RadioGroup) getView().findViewById(R.id.instrument_group);
        instruments.check(R.id.clarinet_button);
        switch (instruments.getCheckedRadioButtonId()) {
            case (R.id.guitar_button):
                instrument = "guitar";
                break;
            case (R.id.clarinet_button):
                instrument = "clarinet";
                break;
            case (R.id.bell_button):
                instrument = "bell";
                break;
        }


        Button play440= (Button) getView().findViewById(R.id.playButton);
        play440.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int newIndex= rand.nextInt(88);
                final double waveFreq= mActivity.frequencies[newIndex];
                current_freq = waveFreq;
                current_name= mActivity.noteNames[newIndex];
                playWave(instrument, waveFreq, current_name);
            }
        });

        Button replayBtn= (Button) getView().findViewById(R.id.replayButton);
        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWave(instrument, current_freq, current_name);
            }
        });

        Button submit_freq = (Button)getView().findViewById(R.id.submit_freq);
        submit_freq.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                EditText current_guess = (EditText)getView().findViewById(R.id.guess_freq);
                double user_guess = Double.parseDouble(current_guess.getText().toString());
                Log.i("GUESS", "current guess = " + user_guess);
                TextView guess_message = (TextView) getView().findViewById(R.id.guess_message);
                if(user_guess < current_freq-30){
                    guess_message.setText("Try A Higher Frequency!");
                    Log.i("HIGHER", "TRY HIGHER!");
                }
                else if(user_guess > current_freq+30){
                    guess_message.setText("Try A Lower Frequency!");
                    Log.i("LOWER", "TRY LOWER!");
                }
                else{
                    guess_message.setText("Close Enough! The correct frequency is " + current_freq + " Hz.");
                    Log.i("CORRECT", "CLOSE ENOUGH!");
                }

            }

        });




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
    public interface GuessFrequencyFragListener {
        // TODO: Update argument type and name
//        void startGuessFrag();
//        void disableButton(String id);
    }
}
