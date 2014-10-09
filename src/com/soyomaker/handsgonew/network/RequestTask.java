package com.soyomaker.handsgonew.network;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.soyomaker.handsgonew.network.httpUtil.SafeHttpClient;
import com.soyomaker.handsgonew.network.parser.IParser;
import com.soyomaker.handsgonew.util.LogUtil;

public class RequestTask extends GenericTask {

	private static final String TAG = "RequestTask";

	/**
	 * 一般get请求.
	 */
	public static final String HTTP_GET = "GET";

	/**
	 * 一般post请求.
	 */
	public static final String HTTP_POST = "POST";

	/**
	 * post请求,传json参数.
	 */
	public static final String HTTP_POST_JSON = "POST_JSON";

	public static final String PARAM_URL = "url";

	public static final String PARAM_HTTP_METHOD = "httpmethod";

	/**
	 * 解析器.
	 */
	private IParser mParser;

	private List<NameValuePair> mPostParams;

	private String mPostString;

	private Bitmap mImage;

	private String mImageName;

	private TaskParams mParams;

	private String mType;

	private String mUrl;

	/**
	 * 相关对象的引用.
	 */
	private Object mExtra;

	public RequestTask(final int threadPriority, IParser parser) {
		super(threadPriority);
		this.mParser = parser;
	}

	public RequestTask(IParser parser) {
		super();
		this.mParser = parser;
	}

	public RequestTask(IParser parser, Bitmap image) {
		super();
		this.mParser = parser;
		this.mImage = image;
	}

	public RequestTask(IParser parser, Bitmap image, String bitmapName) {
		super();
		this.mParser = parser;
		this.mImage = image;
		this.mImageName = bitmapName;
	}

	public void setPostParams(List<NameValuePair> params) {
		mPostParams = params;
	}

	public void setPostString(String param) {
		this.mPostString = param;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public void setExtra(Object relativeObj) {
		this.mExtra = relativeObj;
	}

	public IParser getParser() {
		return mParser;
	}

	public TaskParams getParams() {
		return mParams;
	}

	public String getType() {
		return mType;
	}

	public String getRequestUrl() {
		return mUrl;
	}

	public Object getExtra() {
		return mExtra;
	}

	@Override
	protected void onPostExecute(TaskResult result) {
		// 当task没被取消时，调用父类回调taskFinished
		if (!isCancelled()) {
			super.onPostExecute(result);
		}
	}

	public void cancel() {
		onCancelled();
	}

	@Override
	protected TaskResult doInBackground(TaskParams... params) {
		TaskResult result = new TaskResult(-1, this, null);
		mParams = params[0];
		if (mParams == null) {
			LogUtil.e(TAG, "params is null");
			return result;
		}

		mUrl = mParams.getString(PARAM_URL);
		LogUtil.i(TAG, "request url: " + mUrl);

		HttpClient client = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			client = SafeHttpClient.createHttpClient();

			if (HTTP_POST.equals(mParams.getString(PARAM_HTTP_METHOD))) {
				if (mImage != null) {
					if (TextUtils.isEmpty(mImageName)) {
						mImageName = "uploadfile";
					}
					response = HttpUtil.doFilePostRequest(client, mUrl, mPostParams, mImage,
							mImageName);
				} else {
					response = HttpUtil.doPostRequest(client, mUrl, mPostParams);
				}
			} else if (HTTP_POST_JSON.equals(mParams.getString(PARAM_HTTP_METHOD))) {
				response = HttpUtil.doPostRequest(client, mUrl, mPostString);
			} else {
				response = HttpUtil.doGetRequest(client, mUrl);
			}
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode == HttpStatus.SC_OK || stateCode == HttpStatus.SC_PARTIAL_CONTENT) {
				result.setCode(HttpStatus.SC_OK);
				if (!this.isCancelled()) {
					entity = response.getEntity();
					String inputContent = EntityUtils.toString(entity, HTTP.UTF_8);
					if (inputContent != null && mParser != null) {
						Object obj = mParser.parse(inputContent);
						result.setContent(obj);
					}
				}
			}
			LogUtil.d(TAG, "result.stateCode:" + stateCode);
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return result;
	}
}
