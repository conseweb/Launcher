package com.nd.launcherdev.framework.httplib;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class HttpRemoteRequest {
	private static final String TAG = "HttpRemoteRequest";

	private static final String UTF8_ENCODING = "utf-8";
	
	private static int CONNECTION_TIMEOUT = 10000;

	public static String sendRequest(String url) {
		URLConnection connection = null;
		BufferedReader reader = null;
		StringBuffer buf = new StringBuffer();

		try {
			connection = new URL(url).openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			// if(true)return null;

			InputStream is = connection.getInputStream();
			is.available();
			connection.getContent();

			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				buf.append(line);
			}
			return buf.toString();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获得网络的资�?
	 * @param url 地址
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromURL(String url) throws MalformedURLException, IOException {
		return getInputStreamFromURL(url, CONNECTION_TIMEOUT);
	}
	
	/**
	 * 获得网络的资�?
	 * @param url 地址
	 * @param timeOut 超时时间
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromURL(String url, long timeOut) throws MalformedURLException, IOException {
		URLConnection connection = null;
		InputStream is = null;
		connection = new URL(url).openConnection();
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		is = connection.getInputStream();
		return is;
	}

	/**
	 * post方法请求数据
	 * 
	 * @param url
	 *            请求的url
	 * @param postData
	 *            post数据
	 * @return
	 */
	public static String sendPostData(String url, String postData) {
		HttpURLConnection conn = null;
		OutputStream out = null;
		BufferedReader reader = null;
		StringBuffer sb = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");

			conn.setDoOutput(true);
			conn.setDoInput(true);

			out = conn.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(out);
	        wr.write(postData);
	        wr.flush();
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF8_ENCODING), 8 * 1024);
			sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
			return sb.toString();

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				out.close();
				reader.close();
				conn.disconnect();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
		return null;

	}
	
	/**
	 * post方法请求数据
	 * 
	 * @param url
	 *            请求的url
	 * @param postData
	 *            post数据
	 * @return
	 */
	public static Document getPostDataDocument(String url, String postData) {
		HttpURLConnection conn = null;
		OutputStream out = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");

			conn.setDoOutput(true);
			conn.setDoInput(true);

			out = conn.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(out);
	        wr.write(postData);
	        wr.flush();
			
	        InputStream stream = conn.getInputStream();
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource inputSource = new InputSource(stream);
			Document document = builder.parse(inputSource);
			return document;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				out.close();
				conn.disconnect();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
		return null;
	}

	public static Document getDocumentFromURL(String url) throws IOException, ParserConfigurationException, FactoryConfigurationError, SAXException {
		InputStream stream = getInputStreamFromURL(url);
		if (stream == null) {
			return null;
		}
//		ByteArrayOutputStream content = new ByteArrayOutputStream();
//		byte[] sBuffer = new byte[1024];
//		int readBytes = 0;
//		while ((readBytes = stream.read(sBuffer)) != -1) {
//			content.write(sBuffer, 0, readBytes);
//			Log.i("IHOME", ""+new String(sBuffer));
//		}
//		content.close();
//		stream.close();
//		// 网络上读取的XML在正文前如果有一行空格则�?��删去，否则会出错
//		String result = new String(content.toByteArray());
//		int startIdx = result.indexOf("<?");
//		String xmlStr = result.substring(startIdx);
//		ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlStr.getBytes());
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource inputSource = new InputSource(stream);
		Document document = builder.parse(inputSource);
		stream.close();
		return document;
	}

	/**
	 * 如果原URL带有参数则补&key=value,否则补上?key=value
	 * 
	 * @param sb
	 *            现有的URL的StringBuffer
	 * @param key
	 *            参数
	 * @param value
	 *            参数
	 */
	public static void appendAttrValue(StringBuffer sb, String key, String... values) {
		if (sb.indexOf("?" + key + "=") != -1 || sb.indexOf("&" + key + "=") != -1) {
			return;
		}

		for (String value : values) {
			if (sb.indexOf("?") == -1) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
		}
	}

}
