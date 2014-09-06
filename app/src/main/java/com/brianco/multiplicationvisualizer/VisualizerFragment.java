package com.brianco.multiplicationvisualizer;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brianco.multiplicationvisualizer.view.MultiplicationTable;

public class VisualizerFragment extends Fragment {

    private static final String KEY_TABLE_NUM = "KEY_TABLE_NUM";
    private static final int DELAY_MILLIS = 400;
    private static final int MAX_NUMBER = 63;
    private boolean mIsRunningForward;
    private boolean mIsRunningBackward;
    private MultiplicationTable mTable;
    private TextView mCurrentNumberView;
    private Button mStartForwardButton;
    private Button mStartBackwardButton;

    private final MultiplicationTable.OnDrawFinishListener mOnDrawFinishListener
            = new MultiplicationTable.OnDrawFinishListener() {
        @Override
        public void onDrawFinished(final int number) {
            mCurrentNumberView.setText(String.valueOf(mTable.getNumber()));
            if (mIsRunningForward && mIsRunningBackward) {
                throw new RuntimeException("Cannot run forward and backward at the same time");
            }
            if (mIsRunningForward) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runForward(number);
                    }
                }, DELAY_MILLIS);
            } else if (mIsRunningBackward) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runBackward(number);
                    }
                }, DELAY_MILLIS);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vis, container, false);
        mTable = (MultiplicationTable) view.findViewById(R.id.table);
        mCurrentNumberView = (TextView) view.findViewById(R.id.current_number);
        mStartForwardButton = (Button) view.findViewById(R.id.start_forward_button);
        mStartBackwardButton = (Button) view.findViewById(R.id.start_backward_button);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIsRunningForward = false;
        mIsRunningBackward = false;

        mTable.setOnDrawFinishedListener(mOnDrawFinishListener);
        mStartForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRewind();
                mIsRunningForward = !mIsRunningForward;
                runForward(mTable.getNumber());
            }
        });
        mStartBackwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlay();
                mIsRunningBackward = !mIsRunningBackward;
                runBackward(mTable.getNumber());
            }
        });

        if (savedInstanceState != null) {
            mTable.setNumber(savedInstanceState.getInt(KEY_TABLE_NUM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_TABLE_NUM, mTable.getNumber());
        super.onSaveInstanceState(outState);
    }

    private void runForward(final int number) {
        if (mIsRunningForward) {
            if (number >= MAX_NUMBER) {
                stopPlay();
            } else {
                mStartForwardButton.setText(R.string.stop);
                incrementTable();
            }
        } else {
            stopPlay();
        }
    }

    private void runBackward(final int number) {
        if (mIsRunningBackward) {
            if (number <= MultiplicationTable.MIN_NUMBER) {
                stopRewind();
            } else {
                mStartBackwardButton.setText(R.string.stop);
                decrementTable();
            }
        } else {
            stopRewind();
        }
    }

    private void stopPlay() {
        mIsRunningForward = false;
        mStartForwardButton.setText(R.string.play);
    }

    private void stopRewind() {
        mIsRunningBackward = false;
        mStartBackwardButton.setText(R.string.rewind);
    }

    private void incrementTable() {
        mTable.setNumber(mTable.getNumber() + 1);
    }

    private void decrementTable() {
        mTable.setNumber(mTable.getNumber() - 1);
    }
}
