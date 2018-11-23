package org.platform.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
	
	public static String sendGet(String url) {
		return sendGet(url, null);
	}
	
	public static String sendGet(String url, String[] headers) {
        String result = "";
        BufferedReader in = null;
        try {
        	trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {  
            	public boolean verify(String urlHostName, SSLSession session) {  
            		LOG.info("Warning: URL Host: " + urlHostName + " Peer Host: " + session.getPeerHost());  
            		return true;  
            	}  
            };  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (null != headers && headers.length != 0) {
            	for (int i = 0, len = headers.length; i < len;) {
            		conn.setRequestProperty(headers[i], headers[i + 1]);
            		i = i + 2;
            	}
            }
            // 建立实际的连接
            conn.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception e1) {
            	LOG.error(e1.getMessage(), e1);
            }
        }
        return result;
    }
	
	/**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param params 请求参数
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, Map<String, Object> params, String... headers) {
    	StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return sendGet(url, sb.toString(), headers);
    }

	/**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String params, String... headers) {
        String result = "";
        BufferedReader in = null;
        try {
        	trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {  
            	public boolean verify(String urlHostName, SSLSession session) {  
            		LOG.info("Warning: URL Host: " + urlHostName + " Peer Host: " + session.getPeerHost());  
            		return true;  
            	}  
            };  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            String urlAppendString = StringUtils.isBlank(params) ? url : url + "?" + params;
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) new URL(urlAppendString).openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (null != headers && headers.length != 0) {
            	for (int i = 0, len = headers.length; i < len;) {
            		conn.setRequestProperty(headers[i], headers[i + 1]);
            		i = i + 2;
            	}
            }
            // 建立实际的连接
            conn.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception e1) {
            	LOG.error(e1.getMessage(), e1);
            }
        }
        return result;
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param params 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, String> params, String... headers) {
    	StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
    	return sendPost(url, sb.toString(), headers);
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String params, String... headers) {
        InputStream is = null;
        BufferedReader in = null;
        PrintWriter out = null;
        String result = "";
        try {
        	trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {  
            	public boolean verify(String urlHostName, SSLSession session) {  
            		LOG.info("Warning: URL Host: " + urlHostName + " Peer Host: " + session.getPeerHost());  
            		return true;  
            	}  
            };  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (null != headers && headers.length != 0) {
            	for (int i = 0, len = headers.length; i < len;) {
            		conn.setRequestProperty(headers[i], headers[i + 1]);
            		i = i + 2;
            	}
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            if (conn.getResponseCode() >= 400) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }
            in = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
            	if (null != is) is.close();
            	if (null != in) in.close();
                if (null != out) out.close();
            } catch (IOException ioe){
            	LOG.error(ioe.getMessage(), ioe);
            }
        }
        return result;
    }   
    
    private static void trustAllHttpsCertificates() throws Exception {  
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];  
        javax.net.ssl.TrustManager tm = new miTM();  
        trustAllCerts[0] = tm;  
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");  
        sc.init(null, trustAllCerts, null);  
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());  
    }  
    
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {  
    	
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
		    return null;  
		}  
		
		public boolean isServerTrusted(  
		        java.security.cert.X509Certificate[] certs) {  
		    return true;  
		}  
		
		public boolean isClientTrusted(  
		        java.security.cert.X509Certificate[] certs) {  
		    return true;  
		}  
		
		public void checkServerTrusted(  
		        java.security.cert.X509Certificate[] certs, String authType)  
		        throws java.security.cert.CertificateException {  
		    return;  
		}  
		
		public void checkClientTrusted(  
		        java.security.cert.X509Certificate[] certs, String authType)  
		        throws java.security.cert.CertificateException {  
		    return;  
		}  
	}  
	
}
