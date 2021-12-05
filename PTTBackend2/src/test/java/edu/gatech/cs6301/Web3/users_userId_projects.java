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

public class users_userId_projects {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL");
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
    


    /*
     * Test if POST OK when projectName Empty. We will throw 400 error.
     */
    @Test //#1
    public void pttTest1() throws Exception {

        try {
            CloseableHttpResponse response = createProject("1", "", "1233");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if POST OK when userId Empty. We will throw 500 error. UserId empty is when its equal to 0
     */
    @Test //#1
    public void pttTest2() throws Exception {

        try {
            CloseableHttpResponse response = createProject("2", "project1", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }


    /*
     * Test if POST OK when UserId Negative. We will throw 404 error.
     */
    @Test //#1
    public void pttTest3() throws Exception {

        try {
            CloseableHttpResponse response = createProject("3", "project1", "-1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();

        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if POST OK when projectName not Empty and userId not Empty. We will create the project
     */
    @Test //#1
    public void pttTest4() throws Exception {

        try {
            CloseableHttpResponse response = createProject("1","project1", "111");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    /*
     * Test if GET OK when projectName not Empty, userID non negative. We will return the projects of the  user with the userID given.
     */

    @Test
    public void pttTest4b() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        String id = null;
        String expectedJson = "[";


        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("1", "project2", userId);
            // EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllProjects(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse postUsers(String email, String firstName, String id, String lastName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse createProject(String id, String projectName, String userId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":\"" + id + "\"," +
                "\"projectname\":\"" + projectName + "\"," +
                "\"userid\":\"" + userId + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
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

    private CloseableHttpResponse getAllProjects(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" );
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
    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}