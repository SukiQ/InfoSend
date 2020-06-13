package priv.suki.send;

import priv.suki.msg.OrgInfo;
import priv.suki.util.IpUtil;
import priv.suki.util.Propert;
import priv.suki.util.restful.RestGetHandler;
import priv.suki.util.restful.RestfulService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

public class RestfulSend implements Send {
    @Override
    public boolean send(OrgInfo msg) throws InterruptedException {
        return false;
    }

    @Override
    public boolean init() {
        try {
            String type = Propert.getPropert().getRestful_type();
            int port = Propert.getPropert().getRestful_port();
            if (RestfulService.SERVER.equals(type)) {
                HttpServerProvider provider = HttpServerProvider.provider();
                //监听端,能同时接收RestfulService.CONNNUM(10)个请求
                HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(IpUtil.getLocalIpByNetcard(), port), RestfulService.CONNNUM);
                httpserver.createContext(Propert.getPropert().getRestful_url(), new RestGetHandler());
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
        HttpServerProvider provider = HttpServerProvider.provider();

        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress("192.168.17.212", 8080), 10);
        //监听端口8080,

        httpserver.createContext("/RestSample", new RestGetHandler());
        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");


    }

    public static void main(String[] args) {
        Conn();
    }
}


