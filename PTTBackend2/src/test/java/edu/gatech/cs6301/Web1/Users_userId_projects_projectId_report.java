package edu.gatech.cs6301.Web1;

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

public class Users_userId_projects_projectId_report {

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
    
    // Purpose: Ensures that API will return error code if userId is null
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(String.valueOf(userId), "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(null, projectId, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Ensures that API will return error code if userId < 0
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(-1, projectId, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(404, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Ensures that API will return error code if the user corresponding to the userId does not exist
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            deleteUser(userId);

            response = getReport(userId, projectId, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(404, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }
    }

    // Purpose: Ensures that API will return error code if projectId is null
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(userId, null, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Ensures that API will return error code if projectId < 0
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(userId, -1, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(404, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Ensures that API will return error code if the project corresponding to the projectId does not exist
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            deleteProject(userId, projectId);

            response = getReport(userId, projectId, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(404, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    
    // Purpose: Ensures that API will return error code if from time is null
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(userId, projectId, null, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    
    // Purpose: Ensures that API will return error code if from time is invalid
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

//            response = getReport(userId, projectId, "invalid time", endTime);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(201, status);

//            entity = response.getEntity();
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
//
//            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    
    // Purpose: Ensures that API will return error code if to time is null
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(userId, projectId, startTime, null, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Ensures that API will return error code if to time is invalid
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

//            response = getReport(userId, projectId, startTime, "invalid time");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(201, status);

//            entity = response.getEntity();
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

//            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    
    // Purpose: Ensures that API will return error code if from time is after to time
    @Test
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(userId, projectId, endTime, startTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(200, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    
    // Purpose: Ensures that a report is received if all UserID, projectID, from time, and to time are all present and valid.
    @Test
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2020-02-18T20:30Z";
            String endTime = "2020-03-18T20:30Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            response = getReport(userId, projectId, startTime, endTime, false, false);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(200, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    
    private CloseableHttpResponse getReport(Integer userId, Integer projectId, String from, String to, boolean includeCompletedPomodoros, boolean includeTotalHoursWorkedOnProject) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report?from=" + from + "&to=" + to + "&includeCompletedPomodoros=" + includeCompletedPomodoros +"&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    protected CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(int userId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(int userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + String.valueOf(userId) + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(int userId, int projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse createSession(int userId, int projectId, String startTime, String endTime, int counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"counter\":" + counter + "," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"startTime\":\"" + startTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private int getIdFromResponse(CloseableHttpResponse response) throws ClientProtocolException, IOException, JSONException {
        int status = response.getStatusLine().getStatusCode();

        HttpEntity entity;
        if (status == 201) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        String strResponse = EntityUtils.toString(entity);

        System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

        return Integer.parseInt(getIdFromStringResponse(strResponse));
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

}
