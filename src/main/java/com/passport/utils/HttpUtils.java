package com.passport.utils;

<<<<<<< HEAD
import com.passport.web.AccountController;
=======
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
<<<<<<< HEAD
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * HTTP 请求工具类
 */
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpPost = new HttpGet(apiUrl);
            HttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            System.out.println("执行状态码 : " + statusCode);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param apiUrl
     * @return
     */
    public static String doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            logger.info(response.toString());
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl
     * @param json   json对象
     * @return
     */
    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            logger.info(response.getStatusLine().getStatusCode()+"");
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     *
     * @param apiUrl API接口URL
     * @param json   JSON对象
     * @return
     */
    public static String doPostSSL(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    public static String doPost4Stream(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream input = entity.getContent();
                OutputStream output = new FileOutputStream(new File("D:\\code.jpg"));
                IOUtils.copy(input, output);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    public static InetAddress getLocalHostLANAddress() throws Exception {
        try {
//           return InetAddress.getLocalHost();
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
=======


public class HttpUtils {

  private static final int MAX_TIMEOUT = 7000;
  private static PoolingHttpClientConnectionManager connMgr;
  private static RequestConfig requestConfig;

  static {
    connMgr = new PoolingHttpClientConnectionManager();
    connMgr.setMaxTotal(100);
    connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

    RequestConfig.Builder configBuilder = RequestConfig.custom();
    configBuilder.setConnectTimeout(MAX_TIMEOUT);
    configBuilder.setSocketTimeout(MAX_TIMEOUT);
    configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
    configBuilder.setStaleConnectionCheckEnabled(true);
    requestConfig = configBuilder.build();
  }


  public static String doGet(String url) {
    return doGet(url, new HashMap<String, Object>());
  }


  public static String doGet(String url, Map<String, Object> params) {
    String apiUrl = url;
    StringBuffer param = new StringBuffer();
    int i = 0;
    for (String key : params.keySet()) {
      if (i == 0) {
        param.append("?");
      } else {
        param.append("&");
      }
      param.append(key).append("=").append(params.get(key));
      i++;
    }
    apiUrl += param;
    String result = null;
    HttpClient httpclient = new DefaultHttpClient();
    try {
      HttpGet httpPost = new HttpGet(apiUrl);
      HttpResponse response = httpclient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();

      HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream instream = entity.getContent();
        result = IOUtils.toString(instream, "UTF-8");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }


  public static String doPost(String apiUrl) {
    return doPost(apiUrl, new HashMap<String, Object>());
  }


  public static String doPost(String apiUrl, Map<String, Object> params) {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    String httpStr = null;
    HttpPost httpPost = new HttpPost(apiUrl);
    CloseableHttpResponse response = null;

    try {
      httpPost.setConfig(requestConfig);
      List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
      for (Map.Entry<String, Object> entry : params.entrySet()) {
        NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
            .getValue().toString());
        pairList.add(pair);
      }
      httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
      response = httpClient.execute(httpPost);
      System.out.println(response.toString());
      HttpEntity entity = response.getEntity();
      httpStr = EntityUtils.toString(entity, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return httpStr;
  }


  public static String doPost(String apiUrl, Object json) {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    String httpStr = null;
    HttpPost httpPost = new HttpPost(apiUrl);
    CloseableHttpResponse response = null;

    try {
      httpPost.setConfig(requestConfig);
      StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
      stringEntity.setContentEncoding("UTF-8");
      stringEntity.setContentType("application/json");
      httpPost.setEntity(stringEntity);
      response = httpClient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      System.out.println(response.getStatusLine().getStatusCode());
      httpStr = EntityUtils.toString(entity, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return httpStr;
  }


  public static String doPostSSL(String apiUrl, Map<String, Object> params) {
    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr)
        .setDefaultRequestConfig(requestConfig).build();
    HttpPost httpPost = new HttpPost(apiUrl);
    CloseableHttpResponse response = null;
    String httpStr = null;

    try {
      httpPost.setConfig(requestConfig);
      List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
      for (Map.Entry<String, Object> entry : params.entrySet()) {
        NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
            .getValue().toString());
        pairList.add(pair);
      }
      httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        return null;
      }
      HttpEntity entity = response.getEntity();
      if (entity == null) {
        return null;
      }
      httpStr = EntityUtils.toString(entity, "utf-8");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return httpStr;
  }


  public static String doPostSSL(String apiUrl, Object json) {
    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr)
        .setDefaultRequestConfig(requestConfig).build();
    HttpPost httpPost = new HttpPost(apiUrl);
    CloseableHttpResponse response = null;
    String httpStr = null;

    try {
      httpPost.setConfig(requestConfig);
      StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
      stringEntity.setContentEncoding("UTF-8");
      stringEntity.setContentType("application/json");
      httpPost.setEntity(stringEntity);
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        return null;
      }
      HttpEntity entity = response.getEntity();
      if (entity == null) {
        return null;
      }
      httpStr = EntityUtils.toString(entity, "utf-8");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return httpStr;
  }

  private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
    SSLConnectionSocketFactory sslsf = null;
    try {
      SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

        public boolean isTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
          return true;
        }
      }).build();
      sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

        @Override
        public boolean verify(String arg0, SSLSession arg1) {
          return true;
        }

        @Override
        public void verify(String host, SSLSocket ssl) throws IOException {
        }

        @Override
        public void verify(String host, X509Certificate cert) throws SSLException {
        }

        @Override
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        }
      });
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
    return sslsf;
  }

  public static String doPost4Stream(String apiUrl, Map<String, Object> params) {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    String httpStr = null;
    HttpPost httpPost = new HttpPost(apiUrl);
    CloseableHttpResponse response = null;

    try {
      httpPost.setConfig(requestConfig);
      List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
      for (Map.Entry<String, Object> entry : params.entrySet()) {
        NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
            .getValue().toString());
        pairList.add(pair);
      }
      httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
      response = httpClient.execute(httpPost);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        InputStream input = entity.getContent();
        OutputStream output = new FileOutputStream(new File("D:\\code.jpg"));
        IOUtils.copy(input, output);
        output.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return httpStr;
  }

  public static InetAddress getLocalHostLANAddress() throws Exception {
    try {
      InetAddress candidateAddress = null;

      for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces();
          ifaces.hasMoreElements(); ) {
        NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

        for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
          InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
          if (!inetAddr.isLoopbackAddress()) {
            if (inetAddr.isSiteLocalAddress()) {

              return inetAddr;
            } else if (candidateAddress == null) {
              candidateAddress = inetAddr;
            }
          }
        }
      }
      if (candidateAddress != null) {
        return candidateAddress;
      }
      InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
      return jdkSuppliedAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}