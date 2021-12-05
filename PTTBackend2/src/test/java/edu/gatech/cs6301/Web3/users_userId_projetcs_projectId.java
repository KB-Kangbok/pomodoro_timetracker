package edu.gatech.cs6301.Web3;

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

public class users_userId_projetcs_projectId {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
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
    

    /*
     * Test if GET OK when projectname Empty.  We will throw 400 error.
     */
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createProject("", 1L, 1L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    
	/*
     * Test if GET OK when projectname userId Empty(equal to 0 is empty).  We will throw 400 error.
     */
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createProject("project1", 0L, 1L);
			int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if DELETE OK when userId Empty.  We will throw 400 error.
     */
    @Test
    public void pttTest2b() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = deleteProject(0L, 1L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if GET OK when userId Negative.  We will throw 400 error.
     */
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createProject("project1", -1L, 1L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if DELETE OK when userId negative.  We will throw 400 error.
     */
    @Test
    public void pttTest3b() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = deleteProject(-1L, 1L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if GET OK when projectname projectId Empty(equal to 0 is empty).  We will throw 400 error.
     */
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createProject("project1", 1L, 0L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if DELETE OK when userId Empty.  We will throw 400 error.
     */
    @Test
    public void pttTest4b() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = deleteProject(1L, 0L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if GET OK when projectId Negative.  We will throw 400 error.
     */
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createProject("project1", 1L, -1L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if DELETE OK when projectId Negative.  We will throw 400 error.
     */
    @Test
    public void pttTest5b() throws Exception {
        try {
            CloseableHttpResponse response = deleteProject(1L, -1L);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if GET OK when projectName not Empty, userId non negative and projectId non negative. we will Get the project by ID for a given user
     */
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = getProject("project1", 1L, 1L);
            //String projectName = getProjetcNameFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if DELETE OK when projectName not Empty, userId non negative and projectId non negative. we will Get the project by ID for a given user
     */

    @Test
    public void pttTest6b() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = getProject("project1", 1L, 1L);
            String id = getIdFromResponse(response);
            response.close();
            response = deleteProject(1L, 1L);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":\"" + id +"\",\"projectname\":\"project1\",\"userId\":\"1\"}";
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse getProject(String porjectName, Long userId, Long projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/porjectName/" + porjectName + "/userId/" + String.valueOf(userId) + "/porjectId/" + String.valueOf(projectId));
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private String getIdFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        String id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("userId")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }
    private CloseableHttpResponse createProject(String projectName, Long userId, Long projectId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId +"/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"," +
                "\"userId\":\"" + String.valueOf(userId) + "\"," +
                "\"projectId\":\"" + String.valueOf(projectId) + "\"}}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private String getProjetcNameFromResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        String projectName = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("projectname")) {
                projectName = object.get(key).toString();
            }
        }
        return projectName;
    }
    private String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
    }
    private CloseableHttpResponse deleteProject(Long userId, Long projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + String.valueOf(userId)+ "/projects/" + String.valueOf(projectId));
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
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
