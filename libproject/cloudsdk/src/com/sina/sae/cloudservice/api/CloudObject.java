package com.sina.sae.cloudservice.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.callback.DeleteCallback;
import com.sina.sae.cloudservice.callback.GetCallback;
import com.sina.sae.cloudservice.callback.DetailCallback;
import com.sina.sae.cloudservice.callback.SaveCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * CloudObject 提供了将本地对象同步至云端的接口 云端是SAE的KVDB服务。需要注意的是储在云端的数据是对象的JSON形式 形如一个
 * User对象存储在KVDB中的数据为{"name":"zhiyun","sex":"man","zhiyun":30}
 * 
 * 此工具类的各接口都提供了同步和异步两种方式，建议使用异步方式调用
 * 
 * @author zhiyun
 */
public class CloudObject {

    /**
     * 同步方式将对象存至云端存储中（自动生成key值） 需要注意的是本方法没有key参数，但会自动生成一个key值作为返回值
     * 这里key值生成规则为className_hashCode(形如User_1539201)
     * 
     * @param value
     *            需要存储的对象
     * @return 成功返回保存的key值，否则返回null
     */
    public static String save(Object value) throws CloudServiceException {
        String key = value.getClass().getName() + "_" + value.hashCode();
        if (save(key, value)) {
            return key;
        }
        return null;
    }

    /**
     * 同步方式将对象存至云端存储中 建议key值使用className_id的形式（形如User_123）
     * 使用这种key的组织方式可将不同类型对象更好的区分，方便前缀查找 这样通过User_前缀可以准确的查找所有的User对象
     * 
     * @param key
     *            存储象的key值
     * @param value
     *            需要存储的对象
     * @return 返回是否存储成功
     */
    public static boolean save(String key, Object value) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        Gson gson = new Gson();
        String data = gson.toJson(value);
        params.put("data", data);
        JsonObject json = CloudClient.post(CloudClient.REST_OBJECT, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (code == 0 && "success".equalsIgnoreCase(message)) {
            return true;
        } else {
            String errorMessage = "CloudService.save(" + key + "," + value + ") Error!Code: "
                    + code + "  Message:" + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }
    }

    /**
     * 异步方式将对象存至云端存储中 建议key值使用className_id的形式（形如User_123）
     * 使用这种key的组织方式可将不同类型对象更好的区分，方便前缀查找 这样通过User_前缀可以准确的查找所有的User对象
     * 
     * @param key
     *            存储象的key值
     * @param value
     *            需要存储的对象
     * @param callback
     *            保存后的回调
     */
    public static void save(String key, Object value, SaveCallback callback)
            throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        Gson gson = new Gson();
        String data = gson.toJson(value);
        params.put("data", data);
        CloudClient.post(CloudClient.REST_OBJECT, params, callback);
    }

    /**
     * 异步方式将对象存至云端存储中 需要注意的是本方法没有key参数，但会自动生成一个key值作为返回值
     * 这里key值生成规则为className_hashCode(形如User_1539201)
     * 
     * @param value
     *            需要存储的对象
     * @param callback
     *            保存后的回调，回调方法中包含key值
     */
    public static void save(Object value, SaveCallback callback) throws CloudServiceException {
        String key = value.getClass().getName() + "_" + value.hashCode();
        save(key, value, callback);
    }

    /**
     * 同步方式根据key值从云端获取数据，根据传入的Class类型将数据转换成的对应的对象
     * 
     * @param key
     *            云端对象存储中对应的key值
     * @param clazz
     *            需要转换成的对象类型Class，如User.class，String.class等
     * @return 返回云端存储的对象
     */
    public static <T> T get(String key, Class<T> clazz) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        JsonObject json = CloudClient.get(CloudClient.REST_OBJECT, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (0 == code && "success".equalsIgnoreCase(message)) {
            JsonElement data = json.get("data");
            Gson gson = new Gson();
            return gson.fromJson(data, clazz);
        } else {
            String errorMessage = "CloudService.get(" + key + "," + clazz + ") Error!Code: " + code
                    + " message:" + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }
    }

    /**
     * 异步方式根据key值从云端获取数据，根据传入的Class类型将数据转换成的对应的对象
     * 
     * @param key
     *            云端对象存储中对应的key值
     * @param clazz
     *            需要转换成的对象类型Class，如User.class，String.class等
     * @param callback
     *            回调函数，参数中包含云端返回对象
     */
    public static <T> void get(String key, Class<T> clazz, GetCallback<T> callback)
            throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        CloudClient.get(CloudClient.REST_OBJECT, params, callback);
    }

    /**
     * 同步方式的前缀查找 查找云端key值前缀为prefix的数据，并将查找的数据转换为指定类型返回值List中，默认取前100条
     * 使用此方法必须确保list出来的数据都为clazz类型，否则会在转换过程中抛出异常。
     * 所以建议在存储数据时key值使用className_id的形式（形如User_123）这样list的前缀可使用User_，
     * 可保障list出来的数据都为同一类型，避免转换过程中出现异常
     * 
     * @param prefix
     *            查找的key值前缀
     * @param clazz
     *            转换类型
     * @return 返回云端查询的结果Map，Map中的key为对象存储时的key，value为对象数据
     */
    public static <T> Map<String, T> list(String prefix, Class<T> clazz)
            throws CloudServiceException {
        return list(prefix, 100, clazz);
    }

    /**
     * 同步方式的前缀查找 查找云端key值前缀为prefix的数据，并将查找的数据转换为指定类型返回值List中，
     * 使用此方法必须确保list出来的数据都为clazz类型，否则会在转换过程中抛出异常。
     * 所以建议在存储数据时key值使用class_key的形式（形如User_123）这样list的前缀可使用User_，
     * 可保障list出来的数据都为同一类型，避免转换过程中出现异常
     * 
     * @param prefix
     *            查找的key值前缀
     * @param count
     *            取数据的条数
     * @param clazz
     *            转换类型
     * @return 返回云端查询的结果Map，Map中的key为对象存储时的key，value为对象数据
     */
    public static <T> Map<String, T> list(String prefix, int count, Class<T> clazz)
            throws CloudServiceException {
        Map<String, T> ret = new HashMap<String, T>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("prefix", prefix);
        params.put("count", count + "");
        JsonObject json = CloudClient.get(CloudClient.REST_OBJECT, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (0 == code && "success".equalsIgnoreCase(message)) {
            JsonElement data = json.get("data");
            Gson gson = new Gson();
            Map<String, String> dataMap = gson.fromJson(data, Map.class);
            Set<String> keys = dataMap.keySet();
            for (String key : keys) {
                ret.put(key, gson.fromJson(dataMap.get(key), clazz));
            }
            return ret;
        } else {
            String errorMessage = "CloudService.list(" + prefix + "," + count + "," + clazz
                    + ") Error!Code: " + code + " message:" + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }
    }

    /**
     * 异步方式的前缀查找，查找云端key值前缀为prefix的数据，并将查找的数据转换为指定类型返回值List中，默认取前100条。
     * 使用此方法必须确保list出来的数据都为clazz类型，否则会在转换过程中抛出异常。
     * 所以建议在存储数据时key值使用class_key的形式（形如User_123）这样list的前缀可使用User_，
     * 可保障list出来的数据都为同一类型，避免转换过程中出现异常
     * 
     * @param prefix
     *            查找的key值前缀
     * @param clazz
     *            转换类型
     * @param callback
     *            回调，方法参数中返回云端查询的结果Map，Map中的key为对象存储时的key，value为对象数据
     */
    public static <T> void list(String prefix, Class<T> clazz, DetailCallback<T> callback)
            throws CloudServiceException {
        list(prefix, 100, clazz);
    }

    /**
     * 异步方式的前缀查找，查找云端key值前缀为prefix的数据，并将查找的数据转换为指定类型返回值List中，
     * 使用此方法必须确保list出来的数据都为clazz类型，否则会在转换过程中抛出异常。
     * 所以建议在存储数据时key值使用class_key的形式（形如User_123）这样list的前缀可使用User_，
     * 可保障list出来的数据都为同一类型，避免转换过程中出现异常
     * 
     * @param prefix
     *            查找的key值前缀
     * @param count
     *            取数据的条数
     * @param clazz
     *            转换类型
     * @param callback
     *            回调，方法参数中返回云端查询的结果Map，Map中的key为对象存储时的key，value为对象数据
     */
    public static <T> void list(String prefix, int count, Class<T> clazz, DetailCallback<T> callback)
            throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("prefix", prefix);
        params.put("count", count + "");
        CloudClient.get(CloudClient.REST_OBJECT, params, callback);
    }

    /**
     * 同步方式根据key值删除云端数据
     * 
     * @param key
     *            需要删除数据的key值
     * @return 删除成功返回true
     */
    public static boolean delete(String key) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        JsonObject json = CloudClient.delete(CloudClient.REST_OBJECT, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (0 == code && "success".equalsIgnoreCase(message)) {
            return true;
        } else {
            String errorMessage = "CloudService.delete(" + key + ") Error!Code: " + code
                    + " message:" + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }

    }

    /**
     * 同步方式根据key值删除云端数据
     * 
     * @param key
     *            需要删除数据的key值
     * @param callback
     *            回调，通过方法参数中的Exception来判断是否删除成功
     */
    public static void delete(String key, DeleteCallback callback) throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        CloudClient.delete(CloudClient.REST_OBJECT, params, callback);
    }

}
