package edu.gatech.cs6301.Backend2;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

public class UsersUserIdProjectsProjectIdSessionsSessionId{

    private String baseUrl = "http://localhost:8080";
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

    //Purpose: update session with erroneous body - 400 response
    @Test
    public void pttTest1() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();

        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            
            response = createProject("PTTTest",userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(userId, projectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            response = updateSessionWithoutCounter(userId, projectId, sessionId, updatedStartTime, updatedEndTime);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(userId, projectId);
            response.close();
            response = deleteUser(userId);
            response.close();            
        }finally{
            httpclient.close();
        }
    }

    //Purpose: UserId not found - 400
    @Test
    public void pttTest2() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String createdUserId = getIdFromResponse(response);
            response.close();
            
            response = createProject("PTTTest", createdUserId);
            String projectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(createdUserId, projectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();

	        //user not found
            String userId = createdUserId + createdUserId + createdUserId + "xyz";

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(createdUserId, projectId);
            response.close();
            response = deleteUser(createdUserId);
            response.close();            
        } finally{
            httpclient.close();
        }
    }

    //Purpose: UserId empty - 404 
    @Test
    public void pttTest3() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String createdUserId = getIdFromResponse(response);
            response.close();

            //Empty userId
            String userId = "";
            
            response = createProject("PTTTest", createdUserId);
            String projectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(createdUserId, projectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(createdUserId, projectId);
            response.close();
            response = deleteUser(createdUserId);
            response.close();            
        } finally{
            httpclient.close();
        }
    }

    //Purpose: projectId not found - 404 
    @Test
    public void pttTest4() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            
            response = createProject("PTTTest", userId);
            String createdProjectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(userId, createdProjectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();

            //projectId not found
            String projectId =  "-" + createdProjectId + createdProjectId + createdProjectId;

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(userId, createdProjectId);
            response.close();
            response = deleteUser(userId);
            response.close();
        } finally{
            httpclient.close();
        }
    }


    //Purpose: projectId empty - 404 
    @Test
    public void pttTest5() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            
            response = createProject("PTTTest", userId);
            String createdProjectId = getIdFromResponse(response);
            response.close();

            //empty projectId
            String projectId = "";

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(userId, createdProjectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(userId, createdProjectId);
            response.close();
            response = deleteUser(userId);
            response.close();
        } finally{
            httpclient.close();
        }
    }

    //Purpose: sessionId not found - 404 
    @Test
    public void pttTest6() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            
            response = createProject("PTTTest", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(userId, projectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();

            //sessionId not found
            sessionId = "-" + sessionId + sessionId + sessionId;

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            response = deleteProject(userId, projectId);
            response.close();
            response = deleteUser(userId);
            response.close();            
        } finally{
            httpclient.close();
        }
    }

    //Purpose: sessionId empty - session id empty - 405 method not allowed
    @Test
    public void pttTest7() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            
            response = createProject("PTTTest", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";

            //Empty sessionId
            String sessionId = "";

            String updatedStartTime = "2021-10-14T10:00Z";
            String updatedEndTime = "2021-10-14T10:00Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            response = deleteProject(userId, projectId);
            response.close();
            response = deleteUser(userId);
            response.close();            
        } finally{
            httpclient.close();
        }
    }

    //Purpose: Successful put session request - 201 response 
    @Test
    public void pttTest8() throws Exception{
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        try{
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("PTTTest",userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = createSession(userId, projectId, startTime, endTime);
            String sessionId = getIdFromResponse(response);
            response.close();
            
            String updatedStartTime = "2019-02-19T18:00Z";
            String updatedEndTime = "2019-02-19T20:30Z";
            String counter = "1";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, counter);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if(status == 201){
                entity = response.getEntity();
            }else{
                throw new ClientProtocolException("Unexpected response status: " + status + ", expecting 201");
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + "," +
                "\"startTime\":\"" + updatedStartTime + "\"," +
                "\"endTime\":\"" + updatedEndTime + "\"," +
                "\"counter\":" + counter + "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(userId, projectId);
            response.close();
            response = deleteUser(userId);
            response.close();
        }finally{
            httpclient.close();
        }
    }
    
    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
            "{\"firstname\":\"" + firstname + "\"," + 
            "\"lastname\":\"" + lastname + "\"," + 
            "\"email\":\"" + email + "\"}"
            );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

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
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private void deleteAllUsers() throws Exception {

        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
         response.close();
        //return response;
    }

    private CloseableHttpResponse createProject(String projectName, String userId) throws IOException{
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"projectname\":\"" + projectName + "\"," +
                "\"userId\":\"" + userId + "\"}"
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

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
    
    private CloseableHttpResponse getSession(String userid, String projectid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userid + "/projects/" + projectid + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime) throws IOException{
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + startTime + "\"," +
                        "\"endTime\":\"" + endTime + "\"," +
                        "\"counter\":0}"
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createSessionWithoutEndTime(String userId, String projectId, String startTime) throws IOException{
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + startTime + "\"," +
                "\"counter\":0}" 
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateSession(String userId, String projectId, String sessionId, String startTime, String endTime, String counter) throws IOException{
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}" 
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateSessionWithoutCounter(String userId, String projectId, String sessionId, String startTime, String endTime) throws IOException{
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"}"
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

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
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

}
