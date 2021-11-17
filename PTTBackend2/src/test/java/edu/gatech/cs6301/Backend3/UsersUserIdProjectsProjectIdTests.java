package edu.gatech.cs6301.Backend3;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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
import java.util.Iterator;

public class UsersUserIdProjectsProjectIdTests {

    private String baseUrl = "http://localhost:8080";
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9009/ptt";
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
    // Purpose: Testing non-numeric project id get/delete
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = getProject(userId, "hello");
            int status = response.getStatusLine().getStatusCode();
            // non-numeric projectId is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(userId, "hello");
            status = response.getStatusLine().getStatusCode();
            // non-numeric projectId is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing non-existent projectId
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = getProject(userId, "0");
            int status = response.getStatusLine().getStatusCode();
            // non-existent projectId is not found
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(userId, "0");
            status = response.getStatusLine().getStatusCode();
            // non-existent projectId is not found
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: Testing non-numeric userid
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = getProject("hello", "1");
            int status = response.getStatusLine().getStatusCode();
            // non-numeric is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing nonexistent userid
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = getProject("0", "1");
            int status = response.getStatusLine().getStatusCode();
            // non-existent is not found
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing Empty project name
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "");
            String projectId = getIdFromResponse(response);
            response.close();
            // TEST GET
            response = getProject(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing space for Project Name
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, " ");
            String projectId = getIdFromResponse(response);
            response.close();
            // TEST GET
            response = getProject(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing alphanumeric Characters for Project Name
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "CS6301");
            String projectId = getIdFromResponse(response);
            response.close();
            // TEST GET
            response = getProject(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"CS6301\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            // TEST DELETE
            response = deleteProject(userId, projectId);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing Special Characters for Project Name
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "!@#$%^&*(){}|\"/\\`~<>?+-_=");
            response.close();
        } finally {
            httpclient.close();
        }
    }
    // Util Functions
    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws  IOException {
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

    private CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
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
