package priv.suki.util.restful;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.send.SOCKET_SERVER;
import priv.suki.util.Propert;

public class RestHandler implements HttpHandler {
    private static Log log = LogFactory.getLog(SOCKET_SERVER.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        log.info("收到请求" + requestMethod + "，消息头为:");
        exchange.getRequestHeaders().entrySet().stream().forEach((s) -> log.info(s));
        if (RestfulService.GET.equalsIgnoreCase(requestMethod)) {
            get(exchange);
        }
    }

    /**
     * GET请求
     *
     * @param exchange
     */
    public static void get(HttpExchange exchange) throws IOException {
        {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/" + Propert.getPropert().getRestfulMsgType());

            //响应码，响应消息长度（-1为不发送响应正文，0为任意，也可以指定长度）
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();

            System.out.println(keySet.size());

            for (String key : keySet) {
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\r\n";
                responseBody.write(s.getBytes());
            }
            //he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);

            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = exchange.getRequestURI();
            String query = requestedUri.getRawQuery();
//            RestParameter.parseQuery(query, parameters);
            // send response
            String response = "";
            for (String key : parameters.keySet()) {
                response += key + " = " + parameters.get(key) + "\r\n";
            }
            responseBody.write(response.getBytes());
            System.out.println("finish");

            responseBody.close();
        }
    }
}
