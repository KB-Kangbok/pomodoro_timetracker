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

public class Users_userid_projects_projectId {

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
    private enum HTTPRequestType {
        kGet,
        kPost,
        kDelete,
        kPut,
    }

    //Purpose: PUT /users/{userId}/projects/{projectId} invalid userId, return 404
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            deleteUser("10");
            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kPut, "10", "TestProjectOne", null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: DELETE /users/{userId}/projects/{projectId} invalid userId, return 404
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            deleteUser("10");
            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kDelete, "10", "TestProjectOne", null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    //Purpose: GET /users/{userId}/projects/{projectId} invalid userId, return 404
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            deleteUser("10");
            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kGet, "10", "1", null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId} invalid pojectId, return 404
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kPut, createdUserId, "InvalidProjectID", null);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: DELETE /users/{userId}/projects/{projectId} invalid pojectId, return 404
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kDelete, createdUserId, "InvalidProjectID", null);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: GET /users/{userId}/projects/{projectId} invalid pojectId, return 404
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kDelete, createdUserId, "InvalidProjectID", null);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Skip test case 7-9 due to the inconsistency of the PTT api yaml.

    //Purpose: GET /users/{userId}/projects/{projectId}, valid userId and projectId, return 200
    @Test
    public void pttTest10() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"", "TestProject");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kGet, createdUserId, createdProjectId, null);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}, valid userId and projectId, invalid id length return 400
    @Test
    public void pttTest11() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"", "TestProject");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            JSONObject body = new JSONObject().put("id", "").put("project-name", "TestProject");

            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kPut, createdUserId, createdProjectId, body);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId}/projects/{projectId}, valid userId and projectId, valid body, return 200
    @Test
    public void pttTest12() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"", "TestProject");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            JSONObject body = new JSONObject().put("id", createdProjectId).put("project-name", "TestProject");

            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kPut, createdUserId, createdProjectId, body);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    //Purpose: DELETE /users/{userId}/projects/{projectId}, valid userId and projectId, return 200
    @Test
    public void pttTest13() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("Jane","Doe","jane@doe.org");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = createProjectRequest(createdUserId,"", "TestProject");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = makeProjectIdRequest(HTTPRequestType.kDelete, createdUserId, createdProjectId, null);
            deleteUser(createdUserId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
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

    private CloseableHttpResponse makeProjectIdRequest(HTTPRequestType type, String userId, String projectId, JSONObject body) throws IOException {
        String url_to_go = baseUrl + "/users/" + userId + "/projects/" + projectId;
        System.out.println(url_to_go);

        if(type == HTTPRequestType.kGet){
            Assert.assertTrue(body == null);
            HttpGet httpRequest = new HttpGet(url_to_go);
            httpRequest.addHeader("accept", "application/json");
            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }else if(type == HTTPRequestType.kPost){
            HttpPost httpRequest = new HttpPost(url_to_go);
            httpRequest.addHeader("accept", "application/json");

            StringEntity input;
            if(body != null){
                input = new StringEntity(body.toString());
                input.setContentType("application/json");
                httpRequest.setEntity(input);
            }
            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }else if(type == HTTPRequestType.kDelete){
            Assert.assertTrue(body == null);
            HttpDelete httpRequest = new HttpDelete(url_to_go);
            httpRequest.addHeader("accept", "application/json");
            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }else if(type == HTTPRequestType.kPut){
            HttpPut httpRequest = new HttpPut(url_to_go);
            httpRequest.addHeader("accept", "application/json");

            StringEntity input;
            if(body != null){
                input = new StringEntity(body.toString());
                input.setContentType("application/json");
                httpRequest.setEntity(input);
            }
            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }
        Assert.assertTrue(false);
        return null;
    }

    private CloseableHttpResponse getProjectRequest(String userId) throws IOException {
        String url_to_go = baseUrl + "/users/" + userId + "/projects";
        System.out.println(url_to_go);
        HttpGet httpRequest = new HttpGet(url_to_go);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjectIdRequest(String userId, String projectId) throws IOException {
        String url_to_go = baseUrl + "/users/" + userId + "/projects/" + projectId;
        System.out.println(url_to_go);
        HttpGet httpRequest = new HttpGet(url_to_go);
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