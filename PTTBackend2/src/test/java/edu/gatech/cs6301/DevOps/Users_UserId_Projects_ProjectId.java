package edu.gatech.cs6301.DevOps;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Users_UserId_Projects_ProjectId extends Base {
    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
    // Purpose: test POST error for /users/{userId}/projects/{projectId}
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = String.valueOf(getIdFromResponse(response));

            response = postProject(user_id, "Project", "0");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(405, status);

        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when userId not valid for /users/{userId}/projects/{projectId}
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject(Integer.valueOf(userId), "Project");
            String projectId = getIdFromResponse(response);
            response = getProject(userId + "", "" + projectId);
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when projectId not int for /users/{userId}/projects/{projectId}
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject(Integer.valueOf(userId), "Project");
            response = getProject("test", userId);
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when projectId not positive for /users/{userId}/projects/{projectId}
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject(userId, "Project");
            response = getProject(userId, "-1");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            response.close();
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET for /users/{userId}/projects/{projectId}
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

//            response = createProjectNew(userId, "Project");
            response = createProject(userId, "Project");
            String projectId = getIdFromResponse(response);
            response = getProject(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"projectname\":" + "\"Project\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: test DELETE for /users/{userId}/projects/{projectId}
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = createUser("John", "DDD", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProjectHere(userId, "Projectxxxs");
            String projectId = getIdFromResponse(response);
            response = deleteProjectHere(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            response.close();
            Assert.assertEquals(200, status);
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse postProject(String userId, String projectName, String projectId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createProjectHere(String userId, String projectName) throws IOException {
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

    private CloseableHttpResponse deleteProjectHere(String userId, String projectId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
}