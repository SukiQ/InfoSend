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

public class RestGetHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        String requestMethod = he.getRequestMethod();
        if (RestfulService.GET.equalsIgnoreCase(requestMethod)) {
            Headers responseHeaders = he.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");

            System.out.println("test");
            //响应码，响应消息长度（-1为不发送响应正文，0为任意，也可以指定长度）
            he.sendResponseHeaders(200, 0);

            OutputStream responseBody = he.getResponseBody();
            Headers requestHeaders = he.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();

            for (String key : keySet) {
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\r\n";
                responseBody.write(s.getBytes());
            }
            //he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);

            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
//            RestParameter.parseQuery(query, parameters);
            // send response
            String response = "";
            for (String key : parameters.keySet()) {
                response += key + " = " + parameters.get(key) + "\r\n";
            }
            responseBody.write(response.getBytes());

            responseBody.close();
        }
    }
}
