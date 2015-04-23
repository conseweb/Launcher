/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.acra;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Helper class to send POST data over HTTP/HTTPS.
 */
class HttpUtils {
  private static final String LOG_TAG = CrashReportingLibrary.LOG_TAG;

  private static final TrustManager[] TRUST_MANAGER = {new NaiveTrustManager()};

  private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();

  private static final int SOCKET_TIMEOUT = 3000;

  /**
   * Send an HTTP(s) request with POST parameters.
   *
   * @param entity
   * @param url
   * @throws java.io.UnsupportedEncodingException
   *
   * @throws java.io.IOException
   * @throws java.security.KeyManagementException
   *
   * @throws java.security.NoSuchAlgorithmException
   *
   */
  static void doPost(UrlEncodedFormEntity entity, URL url)
      throws URISyntaxException, IOException,
      KeyManagementException, NoSuchAlgorithmException {

//    URLConnection cnx = getConnection(url);
//
//    // POST data
//    cnx.setDoOutput(true);

    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setSoTimeout(params, 15000);
    HttpConnectionParams.setConnectionTimeout(params, 15000);
    DefaultHttpClient httpClient = new DefaultHttpClient(params);
    HttpPost httpPost = new HttpPost(url.toURI());
    httpPost.setEntity(entity);
    //We don't care about the response, so we just hope it went well and on with it
    httpClient.execute(httpPost);

//    OutputStreamWriter wr = new OutputStreamWriter(cnx
//        .getOutputStream());
//    Log.d(LOG_TAG, "Posting crash report data");
//    wr.write(paramString);
//    wr.flush();
//    wr.close();
//    // We do not need to read the response, and there is no response
//    Log.d(LOG_TAG, "Reading response");
//    BufferedReader rd = new BufferedReader(new InputStreamReader(cnx
//            .getInputStream()));
//
//    String line;
//    while ((line = rd.readLine()) != null) {
//        Log.d(LOG_TAG, line);
//    }
//    rd.close();
  }

  /**
   * Open an URL connection. If HTTPS, accepts any certificate even if not
   * valid, and connects to any host name.
   *
   * @param url The destination URL, HTTP or HTTPS.
   * @return The URLConnection.
   * @throws java.io.IOException
   * @throws java.security.NoSuchAlgorithmException
   *
   * @throws java.security.KeyManagementException
   *
   */
  private static URLConnection getConnection(URL url) throws IOException,
      NoSuchAlgorithmException, KeyManagementException {
    URLConnection conn = url.openConnection();
    if (conn instanceof HttpsURLConnection) {
      // Trust all certificates
      SSLContext context = SSLContext.getInstance("TLS");
      context.init(new KeyManager[0], TRUST_MANAGER, new SecureRandom());
      SSLSocketFactory socketFactory = context.getSocketFactory();
      ((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);

      // Allow all hostnames
      ((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);

    }
    conn.setConnectTimeout(SOCKET_TIMEOUT);
    conn.setReadTimeout(SOCKET_TIMEOUT);
    return conn;
  }

}
