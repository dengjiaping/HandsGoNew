package com.sina.sae.cloudservice.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sina.sae.cloudservice.callback.DeleteCallback;
import com.sina.sae.cloudservice.callback.GetCallback;
import com.sina.sae.cloudservice.callback.ListFileCallback;
import com.sina.sae.cloudservice.callback.SaveCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * CloudFile 提供了操作云端文件的各类接口，通过这个接口可以将本地文件同步到云端 对应SAE的Storage服务
 * 
 * @author zhiyun
 */
public class CloudFile {

	private String filepath;// 云端文件路径
	private String url;// 文件对应可访问的url

	/**
	 * 同步方式将本地文件上传至云端存储中
	 * 
	 * @param path
	 *            需要上传文件的本地路径（注意云端也以这个路径存储）
	 * @return 是否上传成功
	 */
	public static boolean upload(String localpath) throws CloudServiceException {
		return upload(localpath, localpath);
	}

	/**
	 * 异步方式将本地文件上传至云端存储中
	 * 
	 * @param path
	 *            需要上传文件的本地路径（注意云端也以这个路径存储）
	 * @param callback
	 *            回调方法参数有是否上传成功一项
	 */
	public static void upload(String localpath, SaveCallback callback) throws CloudServiceException {
		upload(localpath, localpath, callback);
	}

	/**
	 * 同步方式将本地文件上传至云端存储中
	 * 
	 * @param path
	 *            需要上传文件的本地路径
	 * @param cloudpath
	 *            存储在云端的路径
	 * @return 上传成功返回true
	 */
	public static boolean upload(String localpath, String cloudpath) throws CloudServiceException {
		JsonObject json = CloudClient.upload(CloudClient.REST_FILE, localpath, cloudpath, null);
		if (json != null) {
			int code = json.get("code").getAsInt();
			String message = json.get("message").getAsString();
			if (0 == code && "success".equalsIgnoreCase(message)) {
				return true;
			} else {
				String errorMessage = "CloudFile.upload(" + localpath + "," + cloudpath
						+ ") Error!Code: " + code + " message:" + message;
				Log.e("CloudService", errorMessage);
				throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
			}
		} else {
			return false;
		}
	}

	/**
	 * 异步方式将本地文件上传至云端存储中
	 * 
	 * @param path
	 *            需要上传文件的本地路径
	 * @param cloudpath
	 *            存储在云端的路径
	 * @param callback
	 *            回调方法参数有是否上传成功一项
	 */
	public static void upload(String localpath, String cloudpath, SaveCallback callback)
			throws CloudServiceException {
		CloudClient.upload(CloudClient.REST_FILE, localpath, cloudpath, callback);
	}

	/**
	 * 同步方式删除云端文件
	 * 
	 * @param cloudpath
	 *            云端文件路径
	 * @return 返回是否删除成功（文件不存在也返回true）
	 */
	public static boolean delete(String cloudpath) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("path", cloudpath);
		JsonObject json = CloudClient.delete(CloudClient.REST_FILE, params, null);
		if (json != null) {
			int code = json.get("code").getAsInt();
			String message = json.get("message").getAsString();
			if (0 == code && "success".equalsIgnoreCase(message)) {
				return true;
			} else {
				String errorMessage = "CloudFile.delete(" + cloudpath + ") Error!Code: " + code
						+ " message:" + message;
				Log.e("CloudService", errorMessage);
				throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
			}
		} else {
			return false;
		}
	}

	/**
	 * 异步方式删除云端文件
	 * 
	 * @param cloudpath
	 *            云端文件路径
	 * @param callback
	 *            回调，参数中包含是否删除成功（文件不存在也返回true）
	 */
	public static void delete(String cloudpath, DeleteCallback callback)
			throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("path", cloudpath);
		CloudClient.delete(CloudClient.REST_FILE, params, callback);
	}

	/**
	 * 同步方式获取指定云端文件信息
	 * 
	 * @param cloudpath
	 *            云端文件路径
	 * @return 返回云端文件信息
	 */
	public static CloudFile fetch(String cloudpath) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("path", cloudpath);
		JsonObject json = CloudClient.get(CloudClient.REST_FILE, params, null);
		if (json != null) {
			int code = json.get("code").getAsInt();
			String message = json.get("message").getAsString();
			if (0 == code && "success".equalsIgnoreCase(message)) {
				JsonElement data = json.get("data");
				Gson gson = new Gson();
				return gson.fromJson(data, CloudFile.class);
			} else {
				String errorMessage = "CloudFile.fetch(" + cloudpath + ") Error!Code: " + code
						+ " message:" + message;
				Log.e("CloudService", errorMessage);
				throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
			}
		} else {
			return null;
		}
	}

	/**
	 * 异步方式获取指定云端文件信息
	 * 
	 * @param cloudpath
	 *            云端文件路径
	 * @param callback
	 *            回调参数中有云端文件信息
	 */
	public static void fetch(String cloudpath, GetCallback<CloudFile> callback)
			throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("path", cloudpath);
		CloudClient.get(CloudClient.REST_FILE, params, callback);
	}

	/**
	 * 同步方式前缀查找云端文件信息，默认前100条
	 * 
	 * @param prefix
	 *            文件路径前缀（可包含文件夹名）
	 * @return 返回文件列表信息
	 */
	public static List<CloudFile> list(String prefix) throws CloudServiceException {
		return list(prefix, 100);
	}

	/**
	 * 同步方式前缀查找云端文件信息
	 * 
	 * @param prefix
	 *            文件路径前缀
	 * @param count
	 *            列出文件的数量
	 * @return 返回文件列表信息
	 */
	public static List<CloudFile> list(String prefix, int count) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("prefix", prefix);
		params.put("count", count + "");
		JsonObject json = CloudClient.get(CloudClient.REST_FILE, params, null);
		if (json != null) {
			int code = json.get("code").getAsInt();
			String message = json.get("message").getAsString();
			if (0 == code && "success".equalsIgnoreCase(message)) {
				JsonElement data = json.get("data");
				Gson gson = new Gson();
				return gson.fromJson(data, new TypeToken<List<CloudFile>>() {
				}.getType());
			} else {
				String errorMessage = "CloudFile.list(" + prefix + "," + count + ") Error!Code: "
						+ code + " message:" + message;
				Log.e("CloudService", errorMessage);
				throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
			}
		} else {
			return null;
		}
	}

	/**
	 * 异步方式前缀查找云端文件信息
	 * 
	 * @param prefix
	 *            文件路径前缀
	 * @param count
	 *            列出文件的数量
	 * @return 返回文件列表信息
	 */
	public static void list(String prefix, int count, ListFileCallback callback)
			throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("prefix", prefix);
		params.put("count", count + "");
		CloudClient.get(CloudClient.REST_FILE, params, callback);
	}

	/**
	 * 异步方式前缀查找云端文件信息（默认取前100个）
	 * 
	 * @param prefix
	 *            文件路径前缀
	 * @return 返回文件列表信息
	 */
	public static void list(String prefix, ListFileCallback callback) throws CloudServiceException {
		list(prefix, 100, callback);
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "{\"filepath\":\"" + filepath + "\", \"url\"" + url + "\"}";
	}
}
