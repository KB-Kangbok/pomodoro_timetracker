package edu.gatech.cs6301.Backend1;

import java.io.IOException;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class BaseTestClass {
    protected String baseUrl = "http://localhost:8080";
    protected PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    protected CloseableHttpClient httpclient;
    protected boolean setupdone;

    @Before
    public void runBefore() {
	if (!setupdone) {
	    System.out.println("*** SETTING UP TESTS ***");
	    // Increase max total connection to 100
	    cm.setMaxTotal(100);
	    // Increase default max connection per route to 20
	    cm.setDefaultMaxPerRoute(10);
	    // Increase max connections for localhost:80 to 50
	    HttpHost localhost = new HttpHost("locahost", 8080);
	    cm.setMaxPerRoute(new HttpRoute(localhost), 10);
	    httpclient = HttpClients.custom().setConnectionManager(cm).build();
	    setupdone = true;
	}
        System.out.println("*** STARTING TEST ***");
    }

    @After
    public void runAfter() {
        System.out.println("*** ENDING TEST ***");
    }

    protected CloseableHttpResponse deleteUsers(int userId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/user/"+userId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createUser(String email, String firstName, String lastName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse getAllUsers() throws IOException
        {
        HttpGet httpRequest = new HttpGet(baseUrl + "/api/contacts");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
        }

        protected CloseableHttpResponse deleteUser(String id) throws IOException
        {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
        }    

    protected CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        String url = baseUrl + "/users/"+userId+"/projects/"+projectId;
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createProject(String userId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId+"/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":" + 0 + "," +
                "\"projectname\":\"" + projectName + "\"," +
                "\"userId\":" + userId + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse getProject(String userId, String projectId) throws IOException, JSONException{
        String url = baseUrl + "/users/"+userId+"/projects/"+projectId;
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse getProjects(String userId) throws IOException, JSONException{
        String url = baseUrl + "/users/"+userId+"/projects";
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse getSession(String userId, String projectId) throws IOException, JSONException{
        // String url = baseUrl + "/users/"+Long.parseLong(userId)+"/projects/"+Long.parseLong(projectId)+"/sessions";
        String url = baseUrl + "/users/"+userId+"/projects/"+projectId+"/sessions";
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime, int counter) throws IOException {
        // HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+Long.parseLong(userId)+"/projects/"+Long.parseLong(projectId)+"/sessions");
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId+"/projects/"+projectId+"/sessions");

        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse addSession(String userId, String projectId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId+"/projects/"+projectId+"/sessions");
        httpRequest.addHeader("accept", "application/json");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + nowAsISO + "\"," +
                "\"endTime\":\"" + nowAsISO + "\"," +
                "\"counter\":" + 0 + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse updateSession(String userId, String projectId, String sessionId, int counter, String from, String to) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+userId+"/projects/"+projectId+"/sessions/"+sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + from + "\"," +
                "\"endTime\":\"" + to + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
    }

    protected String getIdFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        String id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

    protected CloseableHttpResponse getReport(String userId, String projectId, String from, String to, String includeCompletedPomodoros, String includeTotalHoursWorkedOnProject) throws IOException, JSONException{
        String url = baseUrl + "/users/"+userId+"/projects/"+projectId + "?from="+from+"&to="+to;
        if(includeCompletedPomodoros == null || includeCompletedPomodoros.length() == 0) {
            url += "&includeCompletedPomodoros=" + includeCompletedPomodoros;
        }
        if(includeTotalHoursWorkedOnProject == null || includeTotalHoursWorkedOnProject.length() == 0) {
            url += "&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject;
        }
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
}