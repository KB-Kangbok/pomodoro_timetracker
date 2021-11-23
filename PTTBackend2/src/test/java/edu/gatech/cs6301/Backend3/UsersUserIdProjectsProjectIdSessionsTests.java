package edu.gatech.cs6301.Backend3;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class UsersUserIdProjectsProjectIdSessionsTests {

    private String baseUrl = "http://localhost:8080";
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9009/ptt";
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
            cm.setDefaultMaxPerRoute(10);
            // Increase max connections for localhost:80 to 50
            HttpHost localhost = new HttpHost("locahost", 8080);
            cm.setMaxPerRoute(new HttpRoute(localhost), 10);
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

    // Util Functions
    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
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

    private CloseableHttpResponse createProject(String userId, String projectName) throws IOException {
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

    private CloseableHttpResponse getAllProjects(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private long getIdFromResponse(HttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        long id = getIdFromStringResponse(strResponse);
        return id;
    }

    private long getIdFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        long id = 0;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.getLong(key);
            }
        }
        return id;
    }

    @Test
    // Purpose: the endpoint should return a 404 or 400 for not numeric user id.
    public void pttTest1() throws IOException {
        String nonNumericUserId = "not numeric user id";
        long projectId = 0;
        try {
            HttpGet httpRequest = new HttpGet(
                    String.format("%s/users/%s/projects/%d/sessions", baseUrl, nonNumericUserId, projectId)
            );
            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertTrue(status == 400 || status == 404);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: the endpoint should return a 404 or 400 for non-numeric project id.
    public void pttTest2() throws IOException {
        long userId = 0;
        String nonNumericProjectId = "non-numeric project id";
        try {
            HttpGet httpRequest = new HttpGet(
                    String.format("%s/users/%d/projects/%s/sessions", baseUrl, userId, nonNumericProjectId)
            );
            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertTrue(status == 400 || status == 404);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: the endpoint should not accept a new sesstion with empty start time
    public void pttTest3() throws IOException, JSONException {

        try {
            HttpResponse createUserResponse = createUser("John", "Doe", "john_doe@test.domain");
            long userId = getIdFromResponse(createUserResponse);
            HttpResponse createProjectResponse = createProject(String.format("%d", userId), "test-project");
            long projectId = getIdFromResponse(createProjectResponse);

            StringEntity sessionNoStartTime = new StringEntity("{\n" +
                                                                       "    \"counter\":0,\n" +
                                                                       "    \"endTime\":\"2019-02-18T20:00Z\",\n" +
                                                                       "    \"id\":0\n" +
                                                                       "}");
            HttpPost postRequest = new HttpPost(String.format(
                    String.format("%s/users/%d/projects/%s/sessions", baseUrl, userId, projectId)
            ));
            postRequest.addHeader("accept", "application/json");
            sessionNoStartTime.setContentType("application/json");
            postRequest.setEntity(sessionNoStartTime);

            System.out.println("*** Executing request " + postRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(postRequest);
            System.out.println("*** Raw response " + response + "***");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: the server should return 400 for adding a session without end time
    public void pttTest4() throws IOException {

        try {
            HttpResponse createUserResponse = createUser("John", "Doe", "john_doe@test.domain");
            long userId = getIdFromResponse(createUserResponse);
            HttpResponse createProjectResponse = createProject(String.format("%d", userId), "test-project");
            long projectId = getIdFromResponse(createProjectResponse);

            StringEntity sessionNoStartTime = new StringEntity("{\n" +
                                                                       "    \"counter\":0,\n" +
                                                                       "    \"startTime\":\"2019-02-18T20:00Z\",\n" +
                                                                       "    \"id\":0\n" +
                                                                       "}");
            HttpPost postRequest = new HttpPost(String.format("%s/users/%d/projects/%s/sessions", baseUrl, userId,
                                                              projectId));
            postRequest.addHeader("accept", "application/json");
            sessionNoStartTime.setContentType("application/json");
            postRequest.setEntity(sessionNoStartTime);

            System.out.println("*** Executing request " + postRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(postRequest);
            System.out.println("*** Raw response " + response + "***");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: the server should return 400 for adding sessions with a negative counter.
    public void pttTest5() throws IOException {
        try {
            HttpResponse createUserResponse = createUser("John", "Doe", "john_doe@test.domain");
            long userId = getIdFromResponse(createUserResponse);
            HttpResponse createProjectResponse = createProject(String.format("%d", userId), "test-project");
            long projectId = getIdFromResponse(createProjectResponse);

            StringEntity sessionNoStartTime = new StringEntity("{\n" +
                                                                       "    \"counter\":-1,\n" +
                                                                       "    \"startTime\":\"2019-02-18T19:00Z\",\n" +
                                                                       "    \"endTime\":\"2019-02-18T20:00Z\",\n" +
                                                                       "    \"id\":0\n" +
                                                                       "}");
            HttpPost postRequest = new HttpPost(String.format("%s/users/%d/projects/%s/sessions", baseUrl, userId,
                                                              projectId));
            postRequest.addHeader("accept", "application/json");
            sessionNoStartTime.setContentType("application/json");
            postRequest.setEntity(sessionNoStartTime);

            System.out.println("*** Executing request " + postRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(postRequest);
            System.out.println("*** Raw response " + response + "***");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: the server should return 200 and the correct json object for a valid session with counter == 0 where end time hours - start time hours = 2
    public void pttTest6() throws IOException {
        try {
            HttpResponse createUserResponse = createUser("John", "Doe", "john_doe@test.domain");
            long userId = getIdFromResponse(createUserResponse);
            HttpResponse createProjectResponse = createProject(String.format("%d", userId), "test-project");
            long projectId = getIdFromResponse(createProjectResponse);

            String sessionJson = "{\n" +
                    "    \"counter\":0,\n" +
                    "    \"startTime\":\"2019-02-18T20:00Z\",\n" +
                    "    \"endTime\":\"2019-02-18T20:00Z\",\n" +
                    "    \"id\":0\n" +
                    "}";
            StringEntity sessionNoStartTime = new StringEntity(sessionJson);
            HttpPost postRequest = new HttpPost(String.format("%s/users/%d/projects/%s/sessions", baseUrl, userId,
                                                              projectId));
            postRequest.addHeader("accept", "application/json");
            sessionNoStartTime.setContentType("application/json");
            postRequest.setEntity(sessionNoStartTime);

            System.out.println("*** Executing request " + postRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(postRequest);
            System.out.println("*** Raw response " + response + "***");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            HttpEntity responseEntity = response.getEntity();

            String stringResponseEntity = EntityUtils.toString(responseEntity);
            JSONAssert.assertEquals(sessionJson, stringResponseEntity, false);

            response.close();
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: the server should return 200 and the correct json for a valid session with counter > 0
    public void pttTest7() throws IOException {
        try {
            HttpResponse createUserResponse = createUser("John", "Doe", "john_doe@test.domain");
            long userId = getIdFromResponse(createUserResponse);
            HttpResponse createProjectResponse = createProject(String.format("%d", userId), "test-project");
            long projectId = getIdFromResponse(createProjectResponse);

            String sessionJson = "{\n" +
                    "    \"counter\":2,\n" +
                    "    \"startTime\":\"2019-02-18T19:00Z\",\n" +
                    "    \"endTime\":\"2019-02-18T20:00Z\",\n" +
                    "    \"id\":0\n" +
                    "}";
            StringEntity sessionNoStartTime = new StringEntity(sessionJson);
            HttpPost postRequest = new HttpPost(String.format("%s/users/%d/projects/%s/sessions", baseUrl, userId,
                                                              projectId));
            postRequest.addHeader("accept", "application/json");
            sessionNoStartTime.setContentType("application/json");
            postRequest.setEntity(sessionNoStartTime);

            System.out.println("*** Executing request " + postRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(postRequest);
            System.out.println("*** Raw response " + response + "***");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);
            response.close();
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
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
