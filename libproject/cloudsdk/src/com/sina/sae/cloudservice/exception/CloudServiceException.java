package com.sina.sae.cloudservice.exception;

public class CloudServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final int NETWORK_ERROR = 10001;// 网络不通或异常
	public static final int CLIENT_ERROR = 10002;// 客户端处理异常
	public static final int SERVER_ERROR = 10003;// 服务端处理异常
	/**
	 * 用户名已存在
	 */
	public static final int CODE_USERNAME_ALREADY_EXISTS = 1004;

	/**
	 * 用户名不存在
	 */
	public static final int CODE_USERNAME_NOT_EXISTS = 1005;

	/**
	 * 成功
	 */
	public static final int CODE_SUCCESS = 0;

	/**
	 * 错误码
	 */
	private int code = 0;

	public CloudServiceException(String message) {
		super(message);
	}

	public CloudServiceException(String message, int code) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
