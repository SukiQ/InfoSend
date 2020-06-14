import priv.suki.util.restful.MyX509TrustManager;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;

public class RestfulAction {
    private static final String targetURL = "http://192.168.17.212:8080/RestSample";

    public static void main(String[] args) throws Exception {
        try {

            SSLContext sslcontext = SSLContext.getInstance("SSL");//��һ������ΪЭ��,�ڶ�������Ϊ�ṩ��(����ȱʡ)
            TrustManager[] tm = {new MyX509TrustManager()};
            sslcontext.init(null, tm, new SecureRandom());
            System.out.println("ȱʡ��ȫ�׽���ʹ�õ�Э��: " + sslcontext.getProtocol());
            HostnameVerifier ignoreHostnameVerifier = (s, sslsession) -> {
                System.out.println("WARNING: Hostname is not matched for cert.");
                return true;
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());


            URL restServiceURL = new URL(targetURL);
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();

            httpConnection.setRequestProperty("Accept", "application/xml");
            httpConnection.setRequestMethod("GET");

            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + httpConnection.getResponseCode());
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));
            String output;
            System.out.println("Output from Server:  \n");
            while ((output = responseBuffer.readLine()) != null) {
                System.out.println(output);
            }
            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
