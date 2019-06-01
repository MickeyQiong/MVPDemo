package com.mickey.mymvpapplication.audio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:音频的信息
 * Created by Ming on 2019-6-1.
 */
public class AudioInfo implements Parcelable {

    public String audioId;
    public String audioName;
    public String audioCover;
    public String audioUrl;
    public String size;
    public long duration;

    public AudioInfo() {
    }

    protected AudioInfo(Parcel in) {
        this.audioId = in.readString();
        this.audioName = in.readString();
        this.audioCover = in.readString();
        this.audioUrl = in.readString();
        this.size = in.readString();
        this.duration = in.readLong();
    }

    public static final Creator<AudioInfo> CREATOR = new Creator<AudioInfo>() {
        @Override
        public AudioInfo createFromParcel(Parcel in) {
            return new AudioInfo(in);
        }

        @Override
        public AudioInfo[] newArray(int size) {
            return new AudioInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.audioId);
        dest.writeString(this.audioCover);
        dest.writeString(this.audioName);
        dest.writeString(this.audioUrl);
        dest.writeString(this.size);
        dest.writeLong(this.duration);
    }
}
