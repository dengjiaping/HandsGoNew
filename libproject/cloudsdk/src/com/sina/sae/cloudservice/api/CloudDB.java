package com.sina.sae.cloudservice.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sina.sae.cloudservice.callback.ExecuteCallback;
import com.sina.sae.cloudservice.callback.QueryCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * CloudDB 提供了在移动端调用云端数据库的接口， 对应SAE中的MySQL服务，包含两个接口：
 * 一个是用于查询(Select)操作的Query接口，返回值是查询得到的结果集。 另一个是Execute接口（执行INSERT、UPDATE 或
 * DELETE 语句以及 CREATE TABLE 和 DROP TABLE），接口返回值为影响云端数据库的行数 对应SAE的MySQL服务
 * 
 * @author zhiyun
 */
public class CloudDB {

    /**
     * 同步方式执行sql语句查询云端数据库信息（以select开头的语句）
     * 
     * @param sql
     *            查询的SQL语句
     * @param clazz
     *            查询的数据类型，此方法将把查询结果转换为此类型
     * @return 返回查询结果list，如果为空返回一个空的list
     */
    public static List<Map<String, String>> query(String sql) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sql", sql);
        JsonObject json = CloudClient.get(CloudClient.REST_DB, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (0 == code && "success".equalsIgnoreCase(message)) {
            JsonElement data = json.get("data");
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<Map<String, String>>>() {
            }.getType());
        } else {
            String errorMessage = "CloudDB.query(" + sql + ") Error!Code: " + code + " message:"
                    + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }
    }

    /**
     * 异步方式执行sql语句查询云端数据库信息（以select开头的语句）
     * 
     * @param sql
     *            查询的SQL语句
     * @param clazz
     *            查询的数据类型，此方法将把查询结果转换为此类型
     * @param callback
     *            回调，方法参数为查询结果list，如果为空返回一个空的list
     */
    public static <T> void query(String sql, QueryCallback callback) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sql", sql);
        CloudClient.get(CloudClient.REST_DB, params, callback);
    }

    /**
     * 同步方式执行sql语句操作云端数据库（INSERT、UPDATE、DELETE、CREATE TABLE 和 DROP TABLE）
     * 
     * @param sql
     *            查询的sql语句
     * @return 返回此条sql影响云端数据库的行数
     */
    public static int execute(String sql) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sql", sql);
        JsonObject json = CloudClient.post(CloudClient.REST_DB, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (0 == code && "success".equalsIgnoreCase(message)) {
            JsonObject data = json.get("data").getAsJsonObject();
            return data.get("rows").getAsInt();
        } else {
            String errorMessage = "CloudDB.execute(" + sql + ") Error!Code: " + code + " message:"
                    + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }
    }

    /**
     * 同步方式执行sql语句操作云端数据库（INSERT、UPDATE、DELETE、CREATE TABLE 和 DROP TABLE）
     * 
     * @param sql
     *            操作数据库的sql语句
     * @param callback
     *            回调，参数中有此条sql影响云端数据库的行数
     */
    public static void execute(String sql, ExecuteCallback callback) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sql", sql);
        CloudClient.post(CloudClient.REST_DB, params, callback);
    }

}
