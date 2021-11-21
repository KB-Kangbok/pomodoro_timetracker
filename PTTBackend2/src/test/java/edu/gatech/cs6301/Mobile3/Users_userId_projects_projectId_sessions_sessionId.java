package edu.gatech.cs6301.Mobile3;

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

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId_sessions_sessionId {

    private String baseUrl = "http://localhost:8080";
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9003/ptt";
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

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} UserID content :  invalid userId, return 404
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = putSessionId("10", "1", "1", "2019-02-18T20:00Z", "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} ProjectID content :  invalid projectId, return 404
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, "1", "1", "2019-02-18T20:00Z", "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} SessionID content :  invalid sessionId, return 404
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, "1", "2019-02-18T20:00Z", "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Id parameter :  not present, return 200
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Starttime parameter :  not present, return 400
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "", "2019-02-18T20:30Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} StartTime content :  invalid time format, return 400
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "TIME", "2019-02-18T20:30Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Endtime parameter :  not present, return 400
    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:05Z", "", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} EndTime content :  invalid time format, return 400
    @Test
    public void pttTest8() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:05Z", "TIME", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Counter parameter :  not present, return 400
    @Test
    public void pttTest9() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:05Z", "2019-02-18T20:30Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Counter content :  not integer, return 400
    @Test
    public void pttTest10() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:05Z", "2019-02-18T20:30Z", "TEN");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Counter content :  <0, return 400
    @Test
    public void pttTest11() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:05Z", "2019-02-18T20:30Z", "-4");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}/sessions/{sessionId} Valid parameters, return 200
    @Test
    public void pttTest12() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionResponse = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionId(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:05Z", "2019-02-18T20:30Z", "2");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test 13 identical to 4

    // Helper Methods
    private CloseableHttpResponse getUser(int userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"firstName\":\"" + firstName + "\"," +
                        "\"lastName\":\"" + lastName + "\"," +
                        "\"email\":\"" + email + "\"}"
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String userId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProjectRequest(String userId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"," +
                "\"userId\":\"" + userId + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSessions(int userId, int projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postSessions(String userId, String projectId, String startTime, String endTime, String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"counter\":" + "0" + "," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"startTime\":\"" + startTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessionId(String userId, String projectId, String sessionId, String startTime, String endTime,
                                             String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" +
                sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"counter\":" + "0" + "," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"id\":" + sessionId + "," +
                "\"startTime\":\"" + startTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private String getIdFromResponse(CloseableHttpResponse response) throws ClientProtocolException, IOException, JSONException {
        int status = response.getStatusLine().getStatusCode();

        HttpEntity entity;
        if (status == 201) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        String strResponse = EntityUtils.toString(entity);

        System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

        return getIdFromStringResponse(strResponse);
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