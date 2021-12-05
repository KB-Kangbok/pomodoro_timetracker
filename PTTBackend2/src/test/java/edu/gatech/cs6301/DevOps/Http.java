package edu.gatech.cs6301.DevOps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Http {
    static Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private static String baseUrl = prop.getProperty("TEST_BASE_URL");
    private static CloseableHttpClient client;
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private static boolean setup = false;
    private String url;

    private Http(String ...parts) {
        this.url = String.join("/", parts);
        if (this.url.length() > 0) {
            this.url = "/" + this.url;
        }
        this.url = Http.baseUrl + this.url;

        if (!Http.setup) {
            // Increase max total connection to 100
            Http.cm.setMaxTotal(100);
            // Increase default max connection per route to 20
            int max = Integer.valueOf(prop.getProperty("MAX_CONN"));
            Http.cm.setDefaultMaxPerRoute(max);
            // Increase max connections for localhost:80 to 50
            HttpHost localhost = new HttpHost("locahost", 8080);
            Http.cm.setMaxPerRoute(new HttpRoute(localhost), max);
            Http.client = HttpClients.custom().setConnectionManager(Http.cm)
                    .build();
            Http.setup = true;
        }
    }

    private Http(Http other) {
        this.url = other.url;
    }

    public static Http api(String ...parts) {
        return new Http(parts);
    }

    public Http params(JSONObject data) throws JSONException {
        Http copy = new Http(this);
        final JSONArray keys = data.names();
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            String value = data.getString(key);
            out.add(key + "=" + value);
        }
        if (out.size() > 0) {
            copy.url += '?' + String.join("&", out);
        }
        return copy;
    }

    private CloseableHttpResponse exec(HttpUriRequest request)
            throws IOException {
        request.addHeader("accept", "application/json");
        System.out.println("*** Executing request "
                + request.getRequestLine() + "***");
        CloseableHttpResponse response = Http.client.execute(request);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse exec(HttpEntityEnclosingRequestBase request,
                                       JSONObject data) throws IOException {
        StringEntity input = new StringEntity(data.toString());
        input.setContentType("application/json");
        request.setEntity(input);
        return this.exec(request);
    }

    public CloseableHttpResponse get() throws IOException {
        final HttpGet request = new HttpGet(this.url);
        return this.exec(request);
    }

    public CloseableHttpResponse delete() throws IOException {
        final HttpDelete request = new HttpDelete(this.url);
        return this.exec(request);
    }

    public CloseableHttpResponse put(JSONObject data) throws IOException {
        final HttpPut request = new HttpPut(this.url);
        return this.exec(request, data);
    }

    public CloseableHttpResponse post(JSONObject data) throws IOException {
        final HttpPost request = new HttpPost(this.url);
        return this.exec(request, data);
    }
}