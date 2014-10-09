package com.soyomaker.handsgonew.network;

/**
 * 可取消的任务
 * 
 * @author Tsimle
 */
public interface ICancelable {

	boolean cancel(boolean mayInterruptIfRunning);

	boolean isCancelled();
}
