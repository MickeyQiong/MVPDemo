package com.mickey.mymvpapplication.record;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Describe:
 * Created by Administrator on 2019/6/16.
 */
public class RecordManager {
    private static RecordManager mInstance;
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private boolean isPrepare;

    private RecordManager(String dir){
        mDir = dir;
    }

    public interface RecordStateListener{
        void wellPrepare();
    }

    private RecordStateListener mListener;

    void setOnRecordStateListener(RecordStateListener listener){
        mListener = listener;
    }

    public static RecordManager getInstance(String dir){
        if (mInstance == null){
            synchronized (RecordManager.class){
                if (mInstance == null){
                    mInstance = new RecordManager(dir);
                }
            }
        }
        return mInstance;
    }

    public void prepareRecorder(){
        try {
            isPrepare = false;
            File dir = new File(mDir);
            if (!dir.exists()){
                dir.mkdir();
            }
            String fileName = generateFileName();
            File file = new File(dir,fileName);
            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepare = true;

            if (mListener != null){
                mListener.wellPrepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel){
        if (isPrepare){
            return maxLevel * mMediaRecorder.getMaxAmplitude()/32768 + 1;
        }
        return 1;
    }

    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel(){
       release();
       if (mCurrentFilePath != null){
           File file = new File(mCurrentFilePath);
           file.delete();
           mCurrentFilePath = null;
       }

    }
}
