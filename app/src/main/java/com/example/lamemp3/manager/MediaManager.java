package com.example.lamemp3.manager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import java.io.IOException;

/**
 * MediaPlayer管理类
 */
public class MediaManager {
	
	private static MediaPlayer mediaPlayer;
	private static boolean isPause;

	/**
	 * 播放声音
	 * @param path 文件录音
	 * @param onCompletionListener 播放完成回调
	 */
	public static void playSound(String path, OnCompletionListener onCompletionListener) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mediaPlayer.reset();
					return false;
				}
			});
		} else {
			mediaPlayer.reset();
		}

		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(onCompletionListener);
		mediaPlayer.setDataSource(path);
		mediaPlayer.prepare();
		mediaPlayer.start();
	}

	/**
	 * 暂停播放
	 */
	public static void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}
	
	/**
	 * 重新播放
	 */
	public static void resume(){
		if (mediaPlayer != null && isPause) {
			mediaPlayer.start();
			isPause = false;
		}
	}
	
	/**
	 * 释放资源
	 */
	public static void release(){
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}
