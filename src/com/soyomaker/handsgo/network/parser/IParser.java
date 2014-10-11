package com.soyomaker.handsgo.network.parser;

import org.json.JSONException;

/**
 * 解析器接口
 * 
 * @author mxl
 */
public interface IParser {

	public Object parse(String jsonString) throws JSONException;
}
