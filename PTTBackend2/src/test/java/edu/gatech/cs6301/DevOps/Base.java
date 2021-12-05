package edu.gatech.cs6301.DevOps;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Base {
    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    public String baseUrl = prop.getProperty("TEST_BASE_URL");
    public PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    public CloseableHttpClient httpclient;
    private boolean setupdone;

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

    protected String getIdFromResponse(CloseableHttpResponse response)
            throws IOException, JSONException {
        final HttpEntity entity = response.getEntity();
        final String strResponse = EntityUtils.toString(entity);
        return String.valueOf(getIdFromStringResponse(strResponse));
    }

    protected String getProjectidFromResponse(CloseableHttpResponse response)
            throws IOException, JSONException {
        final HttpEntity entity = response.getEntity();
        final String strResponse = EntityUtils.toString(entity);
        return String.valueOf(getProjectidFromStringResponse(strResponse));
    }

    protected int getIdFromStringResponse(String strResponse)
            throws JSONException, NumberFormatException {
        final JSONObject object = new JSONObject(strResponse);
        return Integer.parseInt(object.get("id").toString());
    }

    protected int getProjectidFromStringResponse(String strResponse)
            throws JSONException, NumberFormatException {
        final JSONObject object = new JSONObject(strResponse);
        return Integer.parseInt(object.get("projectid").toString());
    }

    /* get */

    protected CloseableHttpResponse getUsers() throws IOException {
        return Http.api("users").get();
    }

    protected CloseableHttpResponse getUser(int id) throws IOException {
        return getUser(String.valueOf(id));
    }

    protected CloseableHttpResponse getUser(String id) throws IOException {
        return Http.api("users", id).get();
    }

    protected CloseableHttpResponse getProjects(int id) throws IOException {
        return Http.api("users", String.valueOf(id), "projects").get();
    }

    protected CloseableHttpResponse getProject(int userId, int projectId)
            throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId)).get();
    }

    protected CloseableHttpResponse getProject(String userId, String projectId)
            throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId)).get();
    }

    protected CloseableHttpResponse getSessions(int userId, int projectId)
            throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions").get();
    }

    protected CloseableHttpResponse getSession(int userId, int projectId,
                                               int sessionId) throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions",
                String.valueOf(sessionId)).get();
    }

    protected CloseableHttpResponse getReport(int userId, int projectId,
                                              String from, String to) throws IOException, JSONException {
        return this.getReport(userId, projectId, from, to, new JSONObject());
    }

    protected CloseableHttpResponse getReport(int userId, int projectId,
                                              String from, String to, JSONObject data) throws IOException,
            JSONException {
        data.put("from", from);
        data.put("to", to);
        final Http request = Http.api("users", String.valueOf(userId),
                "projects", String.valueOf(projectId),
                "report");
        return request.params(data).get();
    }

    /* create */

    protected CloseableHttpResponse createUser(JSONObject data)
            throws IOException, JSONException {
        return Http.api("users").post(data);
    }

//    protected CloseableHttpResponse createUser(String firstName,
//            String lastName, String email) throws IOException, JSONException {
//        final JSONObject data = new JSONObject();
//        data.put("firstName", firstName);
//        data.put("lastName", lastName);
//        data.put("email", email);
//        return createUser(data);
//    }

    public CloseableHttpResponse createUser(String firstName, String lastName, String email) throws  IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createProject(int id, String projectname)
            throws IOException, JSONException {
        final JSONObject data = new JSONObject();
        data.put("projectname", projectname);
        return Http.api("users", String.valueOf(id), "projects").post(data);
    }

    protected CloseableHttpResponse createProject(String id, String projectname)
            throws IOException, JSONException {
        final JSONObject data = new JSONObject();
        data.put("projectname", projectname);
        return Http.api("users", String.valueOf(id), "projects").post(data);
    }

    private CloseableHttpResponse createProjectNew(String userId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectName\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createSession(int userId, int projectId,
                                                  String startTime, String endTime, int counter) throws IOException,
            JSONException {
        final JSONObject data = new JSONObject();
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("counter", counter);
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions").post(data);
    }

    /* post */

    protected CloseableHttpResponse postUser(int id, String firstName,
                                             String lastName, String email) throws IOException,
            JSONException {
        final JSONObject data = new JSONObject();
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("email", email);
        return Http.api("users", String.valueOf(id)).post(data);
    }

    protected CloseableHttpResponse postSession(String userId, String projectId,
                                                String sessionId) throws IOException {
        final JSONObject data = new JSONObject();
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions",
                String.valueOf(sessionId)).post(data);
    }

    /* delete */

    protected CloseableHttpResponse deleteUsers() throws IOException {
        return Http.api("users").delete();
    }

    protected CloseableHttpResponse deleteUser(int id) throws IOException {
        return deleteUser(String.valueOf(id));
    }

    protected CloseableHttpResponse deleteUser(String id) throws IOException {
        return Http.api("users", id).delete();
    }

    protected CloseableHttpResponse deleteProject(int id) throws IOException {
        return Http.api("users", String.valueOf(id), "projects").delete();
    }

    protected CloseableHttpResponse deleteProject(int userId, int projectId)
            throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId)).delete();
    }

    protected CloseableHttpResponse deleteProject(String userId, String projectId)
            throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId)).delete();
    }

    protected CloseableHttpResponse deleteSessions(int userId, int projectId)
            throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions").delete();
    }

    protected CloseableHttpResponse deleteSession(int userId, int projectId,
                                                  int sessionId) throws IOException {
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions",
                String.valueOf(sessionId)).delete();
    }

    /* put */

    protected CloseableHttpResponse putUsers() throws IOException,
            JSONException {
        return Http.api("users").put(new JSONObject());
    }

    protected CloseableHttpResponse putUser(int id, String firstName,
                                            String lastName, String email) throws IOException,
            JSONException {
        return putUser(String.valueOf(id), firstName, lastName, email);
    }

    protected CloseableHttpResponse putUser(String id, String firstName,
                                            String lastName, String email) throws IOException,
            JSONException {
        final JSONObject data = new JSONObject();
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("email", email);
        return Http.api("users", id).put(data);
    }

    protected CloseableHttpResponse putProjects(int id) throws IOException,
            JSONException {
        final JSONObject data = new JSONObject();
        return Http.api("users", String.valueOf(id), "projects").put(data);
    }

    protected CloseableHttpResponse putSession(int userId, int projectId,
                                               int sessionId, String startTime, String endTime, int counter)
            throws IOException, JSONException {
        final JSONObject data = new JSONObject();
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("counter", counter);
        return Http.api("users", String.valueOf(userId), "projects",
                String.valueOf(projectId), "sessions",
                String.valueOf(sessionId)).put(data);
    }

    /* assert */

    protected void assertStatus(int statusCode, CloseableHttpResponse response)
            throws ClientProtocolException {
        System.out.println("********");
        System.out.println(statusCode);
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println("********");
        if (statusCode != response.getStatusLine().getStatusCode()) {
            throw new ClientProtocolException("Unexpected response status: "
                    + statusCode);
        }
    }

    protected void assertJSON(JSONArray expectation,
                              CloseableHttpResponse response) throws JSONException, IOException {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            JSONArray actual = new JSONArray(strResponse);
            System.out.println("*** String response " + strResponse
                    + " (" + statusCode + ") ***");
            JSONAssert.assertEquals(expectation, actual, false);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    protected void assertJSON(JSONObject expectation,
                              CloseableHttpResponse response) throws JSONException, IOException {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            JSONObject actual = new JSONObject(strResponse);
            System.out.println("*** String response " + strResponse
                    + " (" + statusCode + ") ***");
            JSONAssert.assertEquals(expectation, actual, false);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    protected void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}