package com.soyomaker.handsgo.network;

/**
 * Task执行完毕后返回的结果
 * 
 * @author Tsimle
 */
public class TaskResult {

    private int code; // 状态码，一般为HTTP的响应码
    private String msg = "";
    private GenericTask task; // 任务对象本身
    private Object content; // 任务处理结果对象。也可以是错误消息

    public TaskResult() {
    }

    public TaskResult(int stateCode, GenericTask task, Object result) {
        super();
        this.code = stateCode;
        this.task = task;
        this.content = result;
    }

    public GenericTask getTask() {
        return task;
    }

    public void setTask(GenericTask task) {
        this.task = task;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object retObj) {
        this.content = retObj;
    }
}
