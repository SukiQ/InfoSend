package priv.suki.send;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import priv.suki.msg.OrgInfo;
import priv.suki.util.IpUtil;
import priv.suki.util.Propert;
import priv.suki.util.restful.RestHandler;
import priv.suki.util.restful.RestfulService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import lombok.SneakyThrows;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.time.Clock;
import java.util.concurrent.Executors;

public class RestfulSend implements Send {
    @Override
    public boolean send(OrgInfo msg) throws InterruptedException {
        return false;
    }

    @Override
    public boolean init() {
        try {
            String type = Propert.getPropert().getRestfulType();
            int port = Propert.getPropert().getRestfulPort();
            if (RestfulService.SERVER.equals(type)) {
                HttpServerProvider provider = HttpServerProvider.provider();
                //监听端,能同时接收RestfulService.CONNNUM(1)个请求
                HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(IpUtil.getLocalIpByNetcard(), port), RestfulService.CONNNUM);
                httpserver.createContext(Propert.getPropert().getRestfulUrl(), new RestHandler());
                httpserver.setExecutor(null);
                httpserver.start();
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public void close() {

    }

    @SneakyThrows
    private static void Conn() {


        HttpsServer httpserver = HttpsServer.create(new InetSocketAddress("192.168.17.212", 8080), 1);
        //监听端口8080,

        httpserver.createContext("/RestSample", new RestHandler());
        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");

//        HttpServer  httpserver = HttpServer.create(new InetSocketAddress("192.168.17.212", 8080), 1);
//        httpserver.createContext("/RestSample", new RestHandler());
//        httpserver.setExecutor(null);
//        httpserver.start();
//
    }
//        public static void HttpsServer() throws Exception {
//            {
//                HttpsServer httpserver = HttpsServer.create(new InetSocketAddress("192.168.17.212", 8080), 1);
//                //监听端口8080,
//
//                SSLContext sslContext = SSLContext.getInstance("TLS");
//                KeyStore ks;
//                if (true)
//                {
//                    char[] password = this.pwd.toCharArray();
//                    /**
//                     * JKS java keystore java数字证书库，CER是公钥
//                     */
//                    ks = KeyStore.getInstance("JKS");
//                    FileInputStream fis = new FileInputStream(cerAddr);
//                    ks.load(fis, password);
//                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//                    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//                    if (tcerAddr.isEmpty())
//                    {
//                        kmf.init(ks, password);
//                        tmf.init(ks);
//                    }
//                    else
//                    {
//                        char[] tpassword = tpwd.toCharArray();
//                        KeyStore tks = KeyStore.getInstance("JKS");
//                        FileInputStream tfis = new FileInputStream(tcerAddr);
//                        tks.load(tfis, tpassword);
//                        kmf.init(ks, password);
//                        tmf.init(tks);
//                    }
//
//                    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//                    server.setHttpsConfigurator(new HttpsConfigurator(sslContext)
//                    {
//                        public void configure ( HttpsParameters params)
//                        {
//                            try
//                            {
//                                params.setNeedClientAuth(twoOrOne);
//
//                                if (ssl.isEmpty())
//                                {
//                                    ssl = "TLSv1";
//                                }
//                                String[] ssls = ssl.split(",");
//                                params.setProtocols(ssls);
//                                log.write("FrmHttpsServer--ssl Edition:" + ssl);
//                            }
//                            catch (Exception ex)
//                            {
//                                try
//                                {
//                                    log.write("Failed to create HTTPS port:" + ex);
//                                    throw new Exception("Failed to create HTTPS port:", ex);
//                                }
//                                catch (Exception localException1)
//                                {
//                                }
//                            }
//                        }
//                    });
//                }
//                else
//                {
//                    char[] password = this.pwd.toCharArray();
//                    ks = KeyStore.getInstance("JKS");
//                    FileInputStream fis = new FileInputStream(cerAddr);
//                    ks.load(fis, password);
//
//                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//                    kmf.init(ks, password);
//
//                    sslContext.init(kmf.getKeyManagers(), null, null);
//                    server.setHttpsConfigurator(new HttpsConfigurator(sslContext)
//                    {
//                        public void configure ( HttpsParameters params)
//                        {
//                            try
//                            {
//                                params.setNeedClientAuth(twoOrOne);
//
//                                if (ssl.isEmpty())
//                                {
//                                    ssl = "TLSv1";
//                                }
//                                String[] ssls = ssl.split(",");
//                                params.setProtocols(ssls);
//                                log.write("FrmHttpsServer--ssl Edition:" + ssl);
//                            }
//                            catch (Exception ex)
//                            {
//                                try
//                                {
//                                    log.write("Failed to create HTTPS port:" + ex);
//                                    throw new Exception("Failed to create HTTPS port:", ex);
//                                }
//                                catch (Exception localException1)
//                                {
//                                }
//                            }
//                        }
//                    });
//                }
//
//                FunctionHandler functionHandler = new FunctionHandler(log.getLog(), cache, vo.getFunctionName(),
//                        vo.getEncoding(), vo.getTokenVailTime(),cacheAll);
//                server.createContext(vo.getFunctionName(), functionHandler);
//                server.createContext(vo.getFunctionName()+"server", functionHandler);
//                server.setExecutor(Executors.newCachedThreadPool());
//                server.start();
//            }
//        }


    public static void main(String[] args) {
        System.out.println(Clock.systemUTC().millis());
//        Conn();
    }
}


