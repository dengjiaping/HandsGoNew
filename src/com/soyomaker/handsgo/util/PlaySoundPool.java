/*
 * 
 */
package com.soyomaker.handsgo.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class PlaySoundPool {

	private Context mContext;

	/** 音效的音量. */
	private int mStreamVolume;

	private SoundPool mSoundPool;

	private SparseIntArray mSoundPoolMap;

	public PlaySoundPool(Context context) {
		this.mContext = context;
		initSounds();
	}

	public void initSounds() {
		// 初始化soundPool 对象,第一个参数是允许有多少个声音流同时播放,第2个参数是声音类型,第三个参数是声音的品质
		mSoundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 100);

		// 初始化HASH表
		mSoundPoolMap = new SparseIntArray();

		// 获得声音设备和设备音量
		AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mStreamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void loadSfx(int raw, int ID) {
		// 把资源中的音效加载到指定的ID(播放的时候就对应到这个ID播放就行了)
		mSoundPoolMap.put(ID, mSoundPool.load(mContext, raw, ID));
	}

	public void play(int sound, int uLoop) {
		mSoundPool.play(mSoundPoolMap.get(sound), mStreamVolume, mStreamVolume, 1, uLoop, 1f);
	}
}
