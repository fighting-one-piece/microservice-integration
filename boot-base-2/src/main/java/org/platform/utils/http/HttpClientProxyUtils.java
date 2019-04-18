package org.platform.utils.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientProxyUtils {

	private final static Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

	/** 定义编码格式 UTF-8 */
	public static final String ENCODE_UTF8 = "UTF-8";
	/** 定义编码格式 GBK */
	public static final String ENCODE_GBK = "GBK";
	/** 定义编码格式 GB2312 */
	public static final String ENCODE_GB2312 = "GB2312";

	public static final String URL_PARAM_CONNECT_FLAG = "&";

	public final static int connectTimeout = 15000;
	
	public final static int socketTimeout = 5000;
	
	private static CloseableHttpClient httpClient = null;
	
	private static PoolingHttpClientConnectionManager connectionManager = null;

	public static RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
		.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();

	static {
		try {
			/**
			 * SSLContext sslContext = SSLContexts.custom().useTLS().build();
			 */
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, null);

			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslContext)).build();

			connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true)
					.setSoKeepAlive(false).setSoLinger(1).setSoReuseAddress(true)
					.setSoTimeout(socketTimeout).build();
			
			connectionManager.setDefaultSocketConfig(socketConfig);

			MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
					.setMaxLineLength(2000).build();

			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE)
					.setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			connectionManager.setDefaultConnectionConfig(connectionConfig);

			connectionManager.setMaxTotal(200);
			connectionManager.setDefaultMaxPerRoute(100);
			
			httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
		} catch (KeyManagementException e) {
			LOG.error("KeyManagementException", e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException", e);
		}
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param encode
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, String encode) throws ClientProtocolException, IOException {
		return sendGet(url, null, encode, connectTimeout, socketTimeout);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, String[] headers) throws ClientProtocolException, IOException {
		return sendGet(url, null, ENCODE_UTF8, connectTimeout, socketTimeout, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param encode
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, String encode, String... headers)
			throws ClientProtocolException, IOException {
		return sendGet(url, null, encode, connectTimeout, socketTimeout, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, Map<String, String> params, String encode)
			throws ClientProtocolException, IOException {
		return sendGet(url, params, encode, connectTimeout, socketTimeout);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, Map<String, String> params, String... headers)
			throws ClientProtocolException, IOException {
		return sendGet(url, params, ENCODE_UTF8, headers);
	}
	
	public static String sendGet(String url, int connectTimeout, String... headers) throws ClientProtocolException, IOException {
		return sendGet(url, null, HttpClientProxyUtils.ENCODE_UTF8, connectTimeout, socketTimeout, null, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, Map<String, String> params, String encode, String... headers)
			throws ClientProtocolException, IOException {
		return sendGet(url, params, encode, connectTimeout, socketTimeout, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param socketTimeout
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, Map<String, String> params, String encode, int connectTimeout,
			int socketTimeout, String... headers) throws ClientProtocolException, IOException {
		return sendGet(url, params, encode, connectTimeout, socketTimeout, null, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param socketTimeout
	 * @param proxy
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendGet(String url, Map<String, String> params, String encode, int connectTimeout,
			int socketTimeout, HttpHost proxy, String... headers) throws ClientProtocolException, IOException {
		String responseTxt = null;
		StringBuilder sb = new StringBuilder(url);
		int i = 0;
		if (null != params && params.size() > 0) {
			for (Entry<String, String> entry : params.entrySet()) {
				sb.append(i == 0 && !url.contains("?") ? "?" : "&");
				sb.append(entry.getKey()).append("=");
				String value = entry.getValue();
				try {
					sb.append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					sb.append(value);
				}
				i++;
			}
		}
		HttpGet httpGet = new HttpGet(sb.toString());
		RequestConfig.Builder builder = RequestConfig.custom().setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
		if (null != proxy) builder.setProxy(proxy);
		RequestConfig requestConfig = builder.build();
		httpGet.setConfig(requestConfig);
		if (null != headers) {
			for (int j = 0, len = headers.length; j < len;) {
				httpGet.setHeader(headers[j], headers[j + 1]);
				j = j + 2;
			}
		}
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(httpGet);
			entity = response.getEntity();
			if (entity != null) {
				if (null == encode || "".equals(encode)) encode = ENCODE_UTF8;
				responseTxt = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (entity != null) entity.getContent().close();
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
				response.close();
			}
			httpGet.releaseConnection();
		}
		return responseTxt;
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @return
	 */
	public static Map<String, Object> sendGetThenRespAndHeaders(String url) throws ClientProtocolException, IOException {
		return sendGetThenRespAndHeaders(url, null, ENCODE_UTF8);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendGetThenRespAndHeaders(String url, String[] headers) throws ClientProtocolException, IOException {
		return sendGetThenRespAndHeaders(url, null, connectTimeout, socketTimeout, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static Map<String, Object> sendGetThenRespAndHeaders(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		return sendGetThenRespAndHeaders(url, params, connectTimeout, socketTimeout);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendGetThenRespAndHeaders(String url, Map<String, String> params,
			String... headers) throws ClientProtocolException, IOException {
		return sendGetThenRespAndHeaders(url, params, connectTimeout, socketTimeout, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param socketTimeout
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendGetThenRespAndHeaders(String url, Map<String, String> params,
			int connectTimeout, int socketTimeout, String... headers) throws ClientProtocolException, IOException {
		return sendGetThenRespAndHeaders(url, params, connectTimeout, socketTimeout, null, headers);
	}

	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param socketTimeout
	 * @return proxy
	 * @return headers
	 */
	public static Map<String, Object> sendGetThenRespAndHeaders(String url, Map<String, String> params,
			int connectTimeout, int socketTimeout, HttpHost proxy, String... headers) throws ClientProtocolException, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		RequestConfig.Builder builder = RequestConfig.custom().setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
		if (null != proxy) builder.setProxy(proxy);
		RequestConfig requestConfig = builder.build();
		StringBuilder sb = new StringBuilder(url);
		int i = 0;
		if (null != params && params.size() > 0) {
			for (Entry<String, String> entry : params.entrySet()) {
				sb.append(i == 0 && !url.contains("?") ? "?" : "&");
				sb.append(entry.getKey()).append("=");
				String value = entry.getValue();
				try {
					sb.append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					sb.append(value);
				}
				i++;
			}
		}
		HttpGet httpGet = new HttpGet(sb.toString());
		httpGet.setConfig(requestConfig);
		if (null != headers) {
			for (int j = 0, len = headers.length; j < len;) {
				httpGet.setHeader(headers[j], headers[j + 1]);
				j = j + 2;
			}
		}
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(httpGet);
			Map<String, String> respHeaders = new HashMap<String, String>();
			Header[] allHeaders = response.getAllHeaders();
			for (int j = 0, len = allHeaders.length; j < len; j++) {
				String headerName = allHeaders[j].getName();
				String headerValue = allHeaders[j].getValue();
				if (respHeaders.containsKey(headerName)) {
					headerValue = respHeaders.get(headerName) + " ; " + headerValue;
				}
				respHeaders.put(headerName, headerValue);
			}
			result.put("headers", respHeaders);
			entity = response.getEntity();
			ByteArrayOutputStream baos = null;
			if (entity != null) {
				InputStream in = entity.getContent();
				baos = new ByteArrayOutputStream();  
		        byte[] buff = new byte[1024];  
		        int rc = 0;  
		        while ((rc = in.read(buff, 0, 1024)) > 0) {  
		        	baos.write(buff, 0, rc);  
		        }  
				result.put("content", baos.toByteArray());
			}
			baos.close();
		} finally {
			if (entity != null) entity.getContent().close();
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
				response.close();
			}
			httpGet.releaseConnection();
		}
		return result;
	}

	/**
	 * HTTP POST请求，默认超时为5S
	 * @param url
	 * @param encode
	 * @return
	 */
	public static String sendPost(String url, String encode) throws IOException {
		return sendPost(url, null, connectTimeout, encode);
	}

	/**
	 * HTTP POST请求，默认超时为5S
	 * @param url
	 * @param params
	 * @param encode
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params, String encode) throws IOException {
		return sendPost(url, params, connectTimeout, encode);
	}

	/**
	 * HTTP POST请求，默认超时为5S
	 * @param url
	 * @param params
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params, String encode, String... headers) throws IOException {
		return sendPost(url, params, connectTimeout, encode, headers);
	}

	/**
	 * HTTP POST请求
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params, int connectTimeout, String encode,
			String... headers) throws IOException {
		return sendPost(url, params, connectTimeout, encode, null, headers);
	}

	/**
	 * HTTP POST请求
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param encode
	 * @param proxy
	 * @param headers
	 * @return
	 * @throws IOException 
	 * @throws  
	 */
	public static String sendPost(String url, Map<String, String> params, int connectTimeout, String encode,
			HttpHost proxy, String... headers) throws IOException {
		String responseTxt = null;
		if (null == encode) encode = ENCODE_UTF8;
		HttpPost httpPost = new HttpPost(url);
		RequestConfig.Builder builder = RequestConfig.custom().setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
		if (null != proxy) builder.setProxy(proxy);
		RequestConfig requestConfig = builder.build();
		httpPost.setConfig(requestConfig);
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if (null != params && params.size() > 0) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(formParams, Charset.forName(encode)));
		if (null != headers) {
			for (int i = 0, len = headers.length; i < len;) {
				httpPost.setHeader(headers[i], headers[i + 1]);
				i = i + 2;
			}
		}
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(httpPost);
			entity = response.getEntity();
			if (null != entity) {
				responseTxt = EntityUtils.toString(entity, Charset.forName(encode));
			}
		} finally {
			if (entity != null) entity.getContent().close();
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
				response.close();
			}
			httpPost.releaseConnection();
		}
		return responseTxt;
	}

	/**
	 * HTTP POST请求
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param encode
	 * @param proxy
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendPost(String url, String params, int connectTimeout, String encode, HttpHost proxy,
			String... headers) throws IOException {
		String responseTxt = null;
		if (null == encode) encode = ENCODE_UTF8;
		HttpPost httpPost = new HttpPost(url);
		RequestConfig.Builder builder = RequestConfig.custom().setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
		if (null != proxy) builder.setProxy(proxy);
		RequestConfig requestConfig = builder.build();
		httpPost.setConfig(requestConfig);
		StringEntity stringEntity = new StringEntity(params, ENCODE_UTF8);
		stringEntity.setContentType("application/x-www-form-urlencoded");
		httpPost.setEntity(stringEntity);
		if (null != headers) {
			for (int i = 0, len = headers.length; i < len;) {
				httpPost.setHeader(headers[i], headers[i + 1]);
				i = i + 2;
			}
		}
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(httpPost);
			entity = response.getEntity();
			if (null != entity)
				responseTxt = EntityUtils.toString(entity, Charset.forName(encode));
		} finally {
			if (entity != null) entity.getContent().close();
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
				response.close();
			}
			httpPost.releaseConnection();
		}
		return responseTxt;
	}

	/**
	 * HTTP POST请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendPostThenRespAndHeaders(String url, Map<String, String> params, String encode,
			String... headers) throws IOException {
		return sendPostThenRespAndHeaders(url, params, encode, null, headers);
	}

	/**
	 * HTTP POST请求
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendPostThenRespAndHeaders(String url, Map<String, String> params, String encode,
			HttpHost proxy, String... headers) throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		if (null == encode) encode = ENCODE_UTF8;
		HttpPost httpPost = new HttpPost(url);
		RequestConfig.Builder builder = RequestConfig.custom().setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
		if (null != proxy) builder.setProxy(proxy);
		RequestConfig requestConfig = builder.build();
		httpPost.setConfig(requestConfig);
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if (null != params && params.size() > 0) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(formParams, Charset.forName(encode)));
		if (null != headers) {
			for (int i = 0, len = headers.length; i < len;) {
				httpPost.setHeader(headers[i], headers[i + 1]);
				i = i + 2;
			}
		}
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(httpPost);
			Map<String, String> respHeaders = new HashMap<String, String>();
			Header[] allHeaders = response.getAllHeaders();
			for (int j = 0, len = allHeaders.length; j < len; j++) {
				String headerName = allHeaders[j].getName();
				String headerValue = allHeaders[j].getValue();
				if (respHeaders.containsKey(headerName)) {
					headerValue = respHeaders.get(headerName) + " ; " + headerValue;
				}
				respHeaders.put(headerName, headerValue);
			}
			result.put("headers", respHeaders);
			entity = response.getEntity();
			if (null != entity) {
				result.put("content", EntityUtils.toString(entity, Charset.forName(encode)));
			}
		} finally {
			if (entity != null) entity.getContent().close();
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
				response.close();
			}
			httpPost.releaseConnection();
		}
		return result;
	}

	/**
	 * 
	 * @param url
	 * @param path
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static String sendPostWithFile(String url, String path, String encode, String... headers) throws IOException {
		String responseTxt = null;
		if (null == encode) encode = ENCODE_UTF8;
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
		httpPost.setConfig(requestConfig);
		if (null != headers) {
			for (int i = 0, len = headers.length; i < len;) {
				httpPost.setHeader(headers[i], headers[i + 1]);
				i = i + 2;
			}
		}
		HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("file", new File(path)).build();
		httpPost.setEntity(httpEntity);
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(httpPost);
			entity = response.getEntity();
			if (null != entity)
				responseTxt = EntityUtils.toString(entity, Charset.forName(encode));
		} finally {
			if (entity != null) entity.getContent().close();
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
				response.close();
			}
			httpPost.releaseConnection();
		}
		return responseTxt;
	}

}
