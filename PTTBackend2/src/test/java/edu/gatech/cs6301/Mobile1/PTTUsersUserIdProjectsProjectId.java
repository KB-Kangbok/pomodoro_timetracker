package edu.gatech.cs6301.Mobile1;

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

public class PTTUsersUserIdProjectsProjectId {

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
    
    @Test
    public void pttTest1() throws Exception {   // Purpose: Test if user id is null
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            String nullId = null; // making sure the ID is null

            response = getUserId(nullId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest2() throws Exception {   // Purpose: Test if user id is missing
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            String uid2 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz" + uid1 + uid2; // making sure the ID is not present

            response = getUserId(missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest3() throws Exception {   // Purpose: Test if project id is null
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String nullProjectName = null; // making sure the ID is null

            response = getProjectId(uid1, nullProjectName);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest4() throws Exception {   // Purpose: Test if project id is not found
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project2");
            String pid2 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz" + pid1 + pid2; // making sure the ID is not present

            response = getProjectId(uid1, missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
 
    @Test
    public void pttTest5() throws Exception {   // Purpose: Test if Get operation with correct user id and project id works as expected
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            response = getProjectId(uid1, pid1);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest6() throws Exception {   // Purpose: Test if Delete operation with correct user id and project id works as expected
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        String expectedJson = null;

        

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteProject(uid1, pid1);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "{\"id\":" + pid1 + ",\"projectname\":\"project1\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllProjectId(uid1);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "[]";
	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // ************************************************************ 
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

    private CloseableHttpResponse createProject(String uid, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + uid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getUserId(String uid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + uid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllUserId() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjectId(String uid, String pid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + uid + "/projects/" + pid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllProjectId(String uid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + uid + "/projects");
        httpRequest.addHeader("accept", "application/json");

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

        return response;
    }

    private CloseableHttpResponse deleteProjects(String uid) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + uid + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");

        return response;
    }

    private CloseableHttpResponse deleteProject(String uid, String pid) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + uid + "/projects/" + pid);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
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

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

}
