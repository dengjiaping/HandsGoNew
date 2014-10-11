package com.soyomaker.handsgo.network;

public interface ITaskFinishListener {

	/**
	 * 上报的任务处理完成事件
	 */
	public void onTaskFinished(TaskResult taskResult);
}
