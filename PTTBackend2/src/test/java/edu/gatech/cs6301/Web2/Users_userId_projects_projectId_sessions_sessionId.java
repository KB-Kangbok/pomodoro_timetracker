package edu.gatech.cs6301.Web2;

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

public class Users_userId_projects_projectId_sessions_sessionId {

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
    
    @Test
    // Purpose: Check if API returns 404 when userId does not exist.
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + userId;

            response = updateSession(missingId, projectId, sessionId, startTime, endTime, counter);
            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 404 when userId is empty.
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession("", 
                projectId, sessionId, startTime, endTime, counter);
            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 404 when projectId does not exist.
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + projectId;

            response = updateSession(userId, 
                missingId, sessionId, startTime, endTime, counter);
            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 400 when projectId is empty.
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email@goog.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, "", sessionId, startTime, endTime, counter);
            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 404 when sessionId does not exist.
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + sessionId;

            response = updateSession(userId, projectId, missingId, startTime, endTime, counter);
            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 405 when sessionId is empty.
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            addSession(userId, projectId, startTime, endTime, counter);

            response = updateSession(userId, projectId, "", startTime, endTime, counter);
            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(405, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 400 when startTime is empty
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, "", endTime, counter);

            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 400 when endTime is empty
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, startTime, "", counter);

            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 400 when endTime is before startTime
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        
        String newEndTime = "2017-02-18T22:00Z";

    String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, startTime, newEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API returns 400 when the counter is negative
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "0";
        String newCounter = "-1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, startTime, endTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
	        Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API updates startDate with valid value
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T19:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "0";

        String newStartTime = "2021-02-18T21:00Z";
        String newEndTime = "2021-02-18T22:00Z";


        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, newStartTime, newEndTime, "0");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + sessionId + "," +
                "\"startTime\":\"" + newStartTime + "\"," +
                "\"endTime\":\"" + newEndTime + "\"," +
                "\"counter\":" + counter + "}";
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API updates startDate with no change
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "0";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            entity = response.getEntity();
            if (status != 201) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + sessionId + "," +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}";
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API updates startDate with valid value
    public void pttTest13() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T19:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        String newStartTime = "2018-02-18T20:00Z";
        String newEndTime = "2018-02-18T21:00Z";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, newStartTime, newEndTime, counter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + sessionId + "," +
                "\"startTime\":\"" + newStartTime + "\"," +
                "\"endTime\":\"" + newEndTime + "\"," +
                "\"counter\":" + counter + "}";
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Check if API updates startDate with no change
    public void pttTest14() throws Exception {
        httpclient = HttpClients.createDefault();

        String startTime = "2021-02-18T20:00Z";
        String endTime = "2021-02-18T21:00Z";
        String counter = "1";

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("firstName", "lastName", "email");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = addSession(userId, projectId, startTime, endTime, counter);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = updateSession(userId, projectId, sessionId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();

            if(status != 201){
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + sessionId + "," +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}";
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse addSession(String userId, String projectId, String startTime, String endTime, String count) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity body = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":\"" + count + "\"}");
        body.setContentType("application/json");
        httpRequest.setEntity(body);

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
    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");
        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"," +
                "\"userId\":\"" + userId + "\"}");
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
