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

public class Users_userId_projects_projectId_sessions {

    private String baseUrl = "http://localhost:8080";
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9003/ptt";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
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

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    //Purpose: GET /users/{userId}/projects/{projectId}/sessions invalid userId, return 404
    @Test
    public void pttTest1() throws Exception {
        try {
            deleteUser("10");
            deleteProject("10", "1");
            CloseableHttpResponse response = getSessions("10", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions invalid userId, return 404
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        try {
            deleteProject("10", "1");
            CloseableHttpResponse response = postSessions("10", "1", "2019-02-18T20:00Z", "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions invalid projectId, return 404
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            deleteProject(createdUserId, "1");

            CloseableHttpResponse response = postSessions(createdUserId, "1", "2019-02-18T20:00Z", "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: GET /users/{userId}/projects/{projectId}/sessions invalid projectId, return 404
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            deleteProject(createdUserId, "1");

            CloseableHttpResponse response = getSessions(createdUserId, "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Starttime parameter: not present, return 400
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "",  "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Starttime parameter: invalid format, return 400
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "TIME",  "2019-02-18T20:25Z", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Endtime parameter: no present, return 400
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Endtime parameter: invalid format, return 400
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "TIME", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Counter parameter: not present, return 400
    @Test
    public void pttTest9() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Counter parameter: not integer, return 400
    @Test
    public void pttTest10() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "TEN");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions Counter parameter: < 0, return 400
    @Test
    public void pttTest11() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "-4");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: GET /users/{userId}/projects/{projectId}/sessions return 200
    @Test
    public void pttTest12() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse createSessionId = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "0");
            String createdSessionId = getIdFromResponse(createSessionId);
            createProjectResponse.close();

            CloseableHttpResponse response = getSessions(createdUserId, createdProjectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            deleteUsers();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects/{projectId}/sessions return 200
    @Test
    public void pttTest13() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = postSessions(createdUserId, createdProjectId, "2019-02-18T20:00Z",  "2019-02-18T20:25Z", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Helper Methods

    private CloseableHttpResponse getUser(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                "\"lastName\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
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
    private CloseableHttpResponse getProject(int userId, int projectId) throws IOException {
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

    private CloseableHttpResponse getSessions(String userId, String projectId) throws IOException {
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
        StringEntity input = new StringEntity("{\"counter\":\"" + counter + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
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