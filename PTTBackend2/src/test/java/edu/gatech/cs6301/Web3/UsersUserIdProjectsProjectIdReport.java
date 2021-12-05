package edu.gatech.cs6301.Web3;

import java.io.IOException;
import java.util.Iterator;

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
import org.apache.http.client.utils.URIBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.net.URISyntaxException;
import java.util.Properties;

import org.skyscreamer.jsonassert.JSONAssert;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class UsersUserIdProjectsProjectIdReport {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;

    @Before
    public void runBefore() throws Exception {
        if (!setupdone) {
            System.out.println("*** SETTING UP TESTS ***");
            // Increase max total connection to 100
            cm.setMaxTotal(100);
            // Increase default max connection per route to 20
            int max = Integer.valueOf(prop.getProperty("MAX_CONN"));
            cm.setDefaultMaxPerRoute(max);
            // Increase max connections for localhost:80 to 50
            HttpHost localhost = new HttpHost(prop.getProperty("TEST_HOST_NAME"), Integer.parseInt(prop.getProperty("TEST_BASE_PORT")));
            cm.setMaxPerRoute(new HttpRoute(localhost), max);
            httpclient = HttpClients.custom().setConnectionManager(cm).build();
            setupdone = true;
        }
        System.out.println("*** STARTING TEST ***");
        deleteUsers();
    }

    @After
    public void runAfter() {
        System.out.println("*** ENDING TEST ***");
    }

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    // Tests if GET returns 400 when the starttime is empty. Status should be 400 since starttime cannot be empty 
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse response = createSession(1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);
            int status = response.getStatusLine().getStatusCode();

            response.close();

            CloseableHttpResponse response2 = getReport(0L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(404, getStatus);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse response2 = getReport(-1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(404, getStatus);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse response2 = getReport(1L, 0L, "2019-02-18T20:00Z", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(404, getStatus);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest4() throws Exception {
        try {


            CloseableHttpResponse response2 = getReport(1L, -1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(getStatus, 404);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest5() throws Exception {
        try {

            CloseableHttpResponse response2 = getReport(1L, 1L, "", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(400,getStatus);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest6() throws Exception {

        try{
            CloseableHttpResponse response2 = getReport(1L, 1L, "2019-02-18T20:00Z", "");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(400, getStatus);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse response2 = getReport(1L, 1L, "2019-03-18T20:00Z", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            Assert.assertEquals(400,getStatus);
            response2.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest8() throws Exception {
        try {

            CloseableHttpResponse response2 = getReport(1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z");
            int getStatus = response2.getStatusLine().getStatusCode();
            response2.close();
        } finally {
            httpclient.close();
        }
    }


    private CloseableHttpResponse getReport(Long userId, Long projectId, String from, String to) throws IOException {
        boolean includeCompletedPomodoros = false;
        boolean includeTotalHoursWorkedOnProject = false;
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report?from=" + from + "&to=" + to + "&includeCompletedPomodoros=" + includeCompletedPomodoros +"&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    protected CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
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

    private CloseableHttpResponse deleteUser(int userId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(int userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + String.valueOf(userId) + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(int userId, int projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse createSession(Long userId, Long projectId, String startTime, String endTime, Long counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"counter\":" + counter + "," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"startTime\":\"" + startTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private int getIdFromResponse(CloseableHttpResponse response) throws ClientProtocolException, IOException, JSONException {
        int status = response.getStatusLine().getStatusCode();

        HttpEntity entity;
        if (status == 201) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        String strResponse = EntityUtils.toString(entity);

        System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

        return Integer.parseInt(getIdFromStringResponse(strResponse));
    }

    private String getIdFromStringResponse(String strResponse) throws JSONException {
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

    private void deleteUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}