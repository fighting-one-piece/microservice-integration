package org.cisiondata.utils.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
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
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class HttpClientUtils {

	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	
	/**  定义编码格式 UTF-8*/  
    public static final String ENCODE_UTF8 = "UTF-8";  
    /**  定义编码格式 GBK */  
    public static final String ENCODE_GBK = "GBK"; 
    /**  定义编码格式 GB2312 */  
    public static final String ENCODE_GB2312 = "GB2312"; 
    
    public static final String URL_PARAM_CONNECT_FLAG = "&";  
    
    public static final String EMPTY = "";  
	
	private static PoolingHttpClientConnectionManager connManager = null;
	
	private static CloseableHttpClient httpclient = null;
	
	public final static int connectTimeout = 5000;
	
	public final static int soTimeout = 5000;
	
	public static RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout)
			.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
	
	static {
		try {
			/**
			SSLContext sslContext = SSLContexts.custom().useTLS().build();
			*/
			SSLContext sslContext = SSLContext.getInstance("TLS");  
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} }, null);
			
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslContext)).build();
			
			connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			
			httpclient = HttpClients.custom().setConnectionManager(connManager).build();
			
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom()
					.setTcpNoDelay(true).build();
			connManager.setDefaultSocketConfig(socketConfig);
			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom()
					.setMaxHeaderCount(200).setMaxLineLength(2000).build();
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE)
					.setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(200);
			connManager.setDefaultMaxPerRoute(20);
		} catch (KeyManagementException e) {
			logger.error("KeyManagementException", e);
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException", e);
		}
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param encode
	 * @return
	 */
	public static String sendGet(String url, String encode) {
		return sendGet(url, null, encode, connectTimeout, soTimeout);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String sendGet(String url, String[] headers) {
		return sendGet(url, null, ENCODE_UTF8, connectTimeout, soTimeout, headers);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static String sendGet(String url, String encode, String... headers) {
		return sendGet(url, null, encode, connectTimeout, soTimeout, headers);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params, String encode) {
		return sendGet(url, params, encode, connectTimeout, soTimeout);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params, String... headers) {
		return sendGet(url, params, ENCODE_UTF8, headers);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params, String encode, String... headers) {
		return sendGet(url, params, encode, connectTimeout, soTimeout, headers);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params, String encode, 
			int connectTimeout, int soTimeout, String... headers) {
		String responseString = null;
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();
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
					logger.warn("encode http get params error, value is " + value, e);
					sb.append(URLEncoder.encode(value));
				}
				i++;
			}
		}
		logger.info("[HttpClientUtils Get] begin invoke:" + sb.toString());
		HttpGet httpGet = new HttpGet(sb.toString());
		httpGet.setConfig(requestConfig);
		if (null != headers) {
			for (int j = 0, len = headers.length; j < len;) {
        		httpGet.setHeader(headers[j], headers[j + 1]);
        		j = j + 2;
        	}
		}
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						responseString = EntityUtils.toString(entity, encode);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpClientUtils Get]get response error, url:%s", sb.toString()), e);
				return responseString;
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info(String.format("[HttpClientUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpClientUtils Get]invoke get timout error, url:%s", sb.toString()), e);
			return responseString;
		} catch (Exception e) {
			logger.error(String.format("[HttpClientUtils Get]invoke get error, url:%s", sb.toString()), e);
		} finally {
			httpGet.releaseConnection();
		}
		return responseString;
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @return
	 */
	public static Map<String, Object> sendGetWithHeaders(String url) {
		return sendGetWithHeaders(url, null, ENCODE_UTF8);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendGetWithHeaders(String url, String[] headers) {
		return sendGetWithHeaders(url, null, ENCODE_UTF8, headers);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static Map<String, Object> sendGetWithHeaders(String url, Map<String, String> params) {
		return sendGetWithHeaders(url, params, ENCODE_UTF8);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @return
	 */
	public static Map<String, Object> sendGetWithHeaders(String url, Map<String, String> params, String encode) {
		return sendGetWithHeaders(url, params, encode, connectTimeout, soTimeout);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendGetWithHeaders(String url, Map<String, String> params, 
			String encode, String... headers) {
		return sendGetWithHeaders(url, params, encode, connectTimeout, soTimeout, headers);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param soTimeout
	 * @return Response Header Infomations
	 */
	public static Map<String, Object> sendGetWithHeaders(String url, Map<String, String> params, 
			String encode, int connectTimeout, int soTimeout, String... headers) {
		Map<String, Object> result = new HashMap<String, Object>();
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();
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
					logger.warn("encode http get params error, value is " + value, e);
					sb.append(URLEncoder.encode(value));
				}
				i++;
			}
		}
		logger.info("[HttpClientUtils Get] begin invoke:" + sb.toString());
		HttpGet httpGet = new HttpGet(sb.toString());
		httpGet.setConfig(requestConfig);
		if (null != headers) {
			for (int j = 0, len = headers.length; j < len;) {
        		httpGet.setHeader(headers[j], headers[j + 1]);
        		j = j + 2;
        	}
		}
		Header[] reqHeaders = httpGet.getAllHeaders();
		for (Header reqHeader : reqHeaders) {
			System.err.println(reqHeader.getName() + " == " + reqHeader.getValue());
		}
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			try {
				Map<String, String> respheaders = new HashMap<String, String>();
				Header[] allHeaders = response.getAllHeaders();
				for (int j = 0, len = allHeaders.length; j < len; j++) {
					String headerName = allHeaders[j].getName();
					String headerValue = allHeaders[j].getValue();
					if (respheaders.containsKey(headerName)) {
						headerValue = respheaders.get(headerName) + " ; " + headerValue;
					}
					respheaders.put(headerName, headerValue);
				}
				result.put("headers", respheaders);
				HttpEntity entity = response.getEntity();
				ByteArrayOutputStream baos = null;
				try {
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
				} finally {
					if (entity != null) {
						entity.getContent().close();
						baos.close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpClientUtils Get]get response error, url:%s", sb.toString()), e);
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info(String.format("[HttpClientUtils Get]Debug url:%s , response string %s:", sb.toString(), response.toString()));
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpClientUtils Get]invoke get timout error, url:%s", sb.toString()), e);
		} catch (Exception e) {
			logger.error(String.format("[HttpClientUtils Get]invoke get error, url:%s", sb.toString()), e);
		} finally {
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
	public static String sendPost(String url, String encode) {
		return sendPost(url, null, connectTimeout, encode);
	}
	
	/**
	  * HTTP POST请求，默认超时为5S
	  * @param url
	  * @param params
	  * @param encode
	  * @return
	 */
	public static String sendPost(String url, Map<String, String> params, String encode) {
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
	public static String sendPost(String url, Map<String, String> params, String encode, String... headers) {
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
	public static String sendPost(String url, Map<String, String> params, int connectTimeout, String encode, String... headers) {
		String responseContent = null;
		HttpPost httpPost = new HttpPost(url);
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();
			httpPost.setConfig(requestConfig);
			if (null == encode) encode = ENCODE_UTF8;
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			if (null != params && params.size() > 0) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			// 绑定到请求 Entry
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, Charset.forName(encode)));
			if (null != headers) {
				for (int i = 0, len = headers.length; i < len;) {
            		httpPost.setHeader(headers[i], headers[i + 1]);
            		i = i + 2;
            	}
			}
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				// 执行POST请求
				HttpEntity entity = response.getEntity(); // 获取响应实体
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Charset.forName(encode));
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI() + ", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;
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
	public static Map<String, Object> sendPostWithHeaders(String url, Map<String, String> params, 
			String encode, String... headers) {
		Map<String, Object> result = new HashMap<String, Object>();
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setConfig(defaultRequestConfig);
			if (null == encode) encode = ENCODE_UTF8;
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			if (null != params && params.size() > 0) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			// 绑定到请求 Entry
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, Charset.forName(encode)));
			if (null != headers) {
				for (int i = 0, len = headers.length; i < len;) {
					httpPost.setHeader(headers[i], headers[i + 1]);
					i = i + 2;
				}
			}
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				Map<String, String> respheaders = new HashMap<String, String>();
				Header[] allHeaders = response.getAllHeaders();
				for (int j = 0, len = allHeaders.length; j < len; j++) {
					String headerName = allHeaders[j].getName();
					String headerValue = allHeaders[j].getValue();
					if (respheaders.containsKey(headerName)) {
						headerValue = respheaders.get(headerName) + " ; " + headerValue;
					}
					respheaders.put(headerName, headerValue);
				}
				result.put("headers", respheaders);
				HttpEntity entity = response.getEntity(); // 获取响应实体
				try {
					if (null != entity) {
						result.put("content", EntityUtils.toString(entity, Charset.forName(encode)));
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI() + ", responseContent: " + result.get("content"));
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return result;
	}
	
	public static String sendPostWithFile(String url, String path, String encode, String... headers) {
		String responseContent = null;
		HttpPost httpPost = new HttpPost(url);
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();
			httpPost.setConfig(requestConfig);
			if (null == encode) encode = ENCODE_UTF8;
			// 绑定到请求 Entry
			if (null != headers) {
				for (int i = 0, len = headers.length; i < len;) {
            		httpPost.setHeader(headers[i], headers[i + 1]);
            		i = i + 2;
            	}
			}
			HttpEntity httpEntity = MultipartEntityBuilder.create()
				.addBinaryBody("file", new File(path)).build();  
			httpPost.setEntity(httpEntity);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				// 执行POST请求
				HttpEntity entity = response.getEntity(); // 获取响应实体
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Charset.forName(encode));
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI() + ", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;
	}

}
