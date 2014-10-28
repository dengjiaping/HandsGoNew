package com.soyomaker.handsgo.network;

import android.os.Process;

public abstract class GenericTask extends AbsNormalAsyncTask<TaskParams, Object, TaskResult> {

    private ITaskFinishListener mTaskFinishListener;

    public GenericTask(final int threadPriority) {
        super(threadPriority);
    }

    public GenericTask() {
        super(Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        if (mTaskFinishListener != null) {
            mTaskFinishListener.onTaskFinished(result);
        }
    }

    @Override
    protected void onCancelled() {
        mTaskFinishListener = null;
        super.onCancelled();
        // 当任务被取消后，不再回调listener
    }

    public ITaskFinishListener getTaskFinishListener() {
        return mTaskFinishListener;
    }

    public void setTaskFinishListener(ITaskFinishListener taskFinishListener) {
        this.mTaskFinishListener = taskFinishListener;
    }
}
