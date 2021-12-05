package edu.gatech.cs6301.DevOps;
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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import org.skyscreamer.jsonassert.JSONAssert;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Users_UserId_Projects {
//    private String baseUrl = "http://gazelle.cc.gatech.edu:9010/ptt";
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
        int max = Integer.valueOf(prop.getProperty("MAX_CONN"));
	    cm.setDefaultMaxPerRoute(max);
	    // Increase max connections for localhost:80 to 50
	    HttpHost localhost = new HttpHost("locahost", 8080);
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


    // Purpose: test GET functionality for /users/user_id/projects endpoint
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id1, id2 = null;
        String expectedJson = "";
        

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john.doe@gmail.com");
            
            String user_id = getIdFromResponse(response);
            response = createProject("Project", user_id);
            id1 = getIdFromResponse(response);
            expectedJson += "[{\"id\":" + id1 + ",\"projectname\":\"Project\"}]";
            response.close();

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;


            response = createProject("Project1", user_id);

            id2 = getIdFromResponse(response);
            expectedJson = "[{\"id\":" + id1 + ",\"projectname\":\"Project\"}";
            expectedJson += ",{\"id\":" + id2 + ",\"projectname\":\"Project1\"}]";
            response.close();

            response = getProjects(user_id);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }      

    // Purpose: Tests if /users/{user_id}/projects endpoint fails for any request besides POST and GET
    @Test
    public void pttTest2() throws Exception {
	    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            response = createProject("Project", user_id);
            response = putProjects(user_id);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);

            response.close();

            response = deleteProjects(user_id);
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);

            response.close();

        } finally {
            httpclient.close();
        }
    }    

    // Purpose: Tests when the user_id is not valid
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createProject("Project", "0");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);

            response = createUser("John", "Doe", "john@doe.org");

            response = createProject("Project", "-1");
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            response.close();

            response = createProject("Project", "string");
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);
            response.close();

            response = createProject("Project", "1");
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }    

    // Purpose: Tests if input projectname is valid String for POST method for endpoint /users/user_id/project
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("", user_id);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();


            response = createProject("!@##$!@$", user_id);
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(201, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }      

    // Purpose: Tests if POST method works for endpoint /users/user_id/projects
    public void pttTest5() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":\"" + id + "\",\"projectname\": \"Project\"}";
    	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
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

    private CloseableHttpResponse deleteUsers() throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse deleteProjects(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse createProject(String projectname, String id) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + id + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjects(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putProjects(String id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private CloseableHttpResponse getUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
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
}