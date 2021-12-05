package edu.gatech.cs6301.Mobile3;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

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

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Users_userid_projects {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
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
            int max = Integer.valueOf(prop.getProperty("MAX_CONN"));
            cm.setDefaultMaxPerRoute(max);
            // Increase max connections for localhost:80 to 50
            HttpHost localhost = new HttpHost(prop.getProperty("TEST_HOST_NAME"), Integer.parseInt(prop.getProperty("TEST_BASE_PORT")));
            cm.setMaxPerRoute(new HttpRoute(localhost), max);
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

    //Purpose: POST /users/{userId}/projects invalid userId, return 404
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            deleteUser("10");
            CloseableHttpResponse response = createProjectRequest("10", "TestProjectOne");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: GET /users/{userId}/projects invalid userId, return 404
    @Test
    public void pttTest2() throws Exception {
        try {
            deleteUser("10");
            CloseableHttpResponse response = getProjectRequest("10");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects, no project id return 201
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser( "John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();

            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId, "TestProjectOne");
            HttpEntity entity = createProjectResponse.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String createdProjectId = getIdFromStringResponse(strResponse);
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);

            int status = createProjectResponse.getStatusLine().getStatusCode();
            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);
            Assert.assertEquals(201, status);
            String expectedJson = "{\"id\":" + createdProjectId + "," +"\"projectname\":\"TestProjectOne\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);

            EntityUtils.consume(getProjectResponse.getEntity());
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects, missing projectname parameter in POST request body
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            CloseableHttpResponse response = createProjectRequest(createdUserId, "NULL");
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects, empty projectname in POST request body
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            CloseableHttpResponse response = createProjectRequest(createdUserId,"EMPTY");
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: GET /users/{userId}/projects, valid userid
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"", "TestProject");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);
            int status = getProjectResponse.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(getProjectResponse.getEntity());
            Assert.assertTrue(responseBody.contains(createdProjectId));

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(getProjectResponse.getEntity());
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects, with an empty project id in POST request body
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"EMPTY", "TestProject");
            HttpEntity entity = createProjectResponse.getEntity();
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);

            System.out.println("UserID:"+createdUserId);
            int status = createProjectResponse.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            deleteUser(createdUserId);

            EntityUtils.consume(getProjectResponse.getEntity());
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users/{userId}/projects, with a non empty project id in POST request body
    @Test
    public void pttTest8() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("John","Doe","john@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"1", "TestProject");
            HttpEntity entity = createProjectResponse.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String createdProjectId = getIdFromStringResponse(strResponse);
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);

            System.out.println("UserID:"+createdUserId);
            int status = createProjectResponse.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            String expectedJson = "{\"id\":" + createdProjectId + "," +"\"projectname\":\"TestProject\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);

            deleteProject(createdUserId, createdProjectId);
            deleteUser(createdUserId);

            EntityUtils.consume(getProjectResponse.getEntity());
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    //Helper methods
    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"lastName\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
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
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if (projectName.equals("NULL")) {
            input = new StringEntity("{}");
        } else if (projectName.equals("EMPTY")) {
            input = new StringEntity("{\"projectname\":\"" + "\"}");
        } else {
            input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        }
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    // Purpose: Overloading the createProjectRequest with id incase one is passed, although it is not used.
    private CloseableHttpResponse createProjectRequest(String userId, String projectId, String projectName) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if (projectName.equals("NULL")) {
            input = new StringEntity("{\"id\":\"" + "\"}");
        } else if (projectName.equals("EMPTY")) {
            input = new StringEntity("{\"id\":\"" + projectId + "\"," +
                    "\"projectname\":\"" + "\"}");
        } else {
            input = new StringEntity("{\"id\":\"" + projectId + "\"," +
                    "\"projectname\":\"" + projectName + "\"}");
        }
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjectRequest(String userId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
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

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

}