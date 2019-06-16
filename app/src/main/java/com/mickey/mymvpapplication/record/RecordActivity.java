package com.mickey.mymvpapplication.record;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mickey.mymvpapplication.R;

import java.lang.ref.WeakReference;

public class RecordActivity extends AppCompatActivity implements RecordManager.RecordStateListener {

    public static final int MSG_RECOED_PREPARE = 0x100;
    public static final int MSG_VOICE_CHANGE = 0x200;
    private RecordManager mRecordManager;
    private  MyHandler myHandler;
    private boolean isRecording;
    private float mTime;
    private int voiceLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        myHandler = new MyHandler(this);
        String dir = Environment.getExternalStorageDirectory() + "/mickey_record";
        mRecordManager = RecordManager.getInstance(dir);
        mRecordManager.setOnRecordStateListener(this);
    }

    private static class MyHandler extends Handler{

        private final WeakReference<RecordActivity> mActivity;

        private MyHandler(RecordActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecordActivity activity = mActivity.get();
            if (activity == null) return;

            switch (msg.what){
                case MSG_RECOED_PREPARE:
                    activity.isRecording = true;

                    new Thread(() -> {
                        while (activity.isRecording){
                            try {
                                Thread.sleep(100);
                                activity.mTime += 0.1f;
                                activity.myHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case MSG_VOICE_CHANGE:
                    activity.voiceLevel = activity.mRecordManager.getVoiceLevel(7);
                    break;
            }
        }
    }

    @Override
    public void wellPrepare() {
        if (myHandler != null) {
            myHandler.sendEmptyMessage(MSG_RECOED_PREPARE);
        }
    }

    @Override
    protected void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
        myHandler = null;
        super.onDestroy();
    }
}
