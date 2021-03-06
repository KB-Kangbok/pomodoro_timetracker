package edu.gatech.cs6301.Web1;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Users_userId_projects_projectId_sessions {

    ////////////////////////////////////////////////
    // Setup - Based on PTTBackend Tests Template //
    ////////////////////////////////////////////////

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


    ////////////////////////////////////////////////
    //                 Test Cases                 //
    ////////////////////////////////////////////////

    // Purpose: ensures that the API will return an error code if the user passes in a nonexistent userId
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = getUser(0);

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


    // Purpose: ensures that the API will return an error code if the user passes in a nonexistent projectId
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        httpclient = HttpClients.createDefault();
        try {
            // Create a User with no projects associated
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Attempt to get a nonexistent project
            response = getProject(userId, 0);

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


    // Purpose: ensures that the API will return an error code if the user passes in an invalid starttime when creating a session
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        httpclient = HttpClients.createDefault();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Attempt to create Session with invalid startTime
            // This startTime is invalid because it is in the future
            String startTime = "2077-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, 0);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // This startTime is invalid because it has the wrong format
            startTime = "02-18-2020";
            endTime = "2077-02-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, 0);

            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }

    }


    // Purpose: ensures that the API will return an error code if the user passes in an invalid endtime when creating a session
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        httpclient = HttpClients.createDefault();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Attempt to create Session with invalid startTime
            // This endTime is invalid because it is in the future
            String endTime = "2077-02-18T20:00Z";
            String startTime = "2019-02-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, 0);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(201, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // This endTime is invalid because it has the wrong format
            endTime = "02-18-2020";
            startTime = "2077-02-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, 0);

            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // This endTime is invalid because it is before the startTime
            startTime = "2019-02-18T20:00Z";
            endTime = "2019-01-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, 0);

            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }

    }


    // Purpose: ensures that the API will return an error code if the user passes in an invalid counter when creating a session
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        httpclient = HttpClients.createDefault();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Attempt to create Session with invalid counter
            // This counter is invalid because it's negative
            String startTime = "2019-01-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, -1);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }

    }


    // Purpose: confirms that GET works as expected
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        httpclient = HttpClients.createDefault();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            response = getSessions(userId, projectId);

            HttpEntity entity;
            String strResponse;

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);
            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }

    }


    // Purpose: confirms that POST works as expected
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();
        httpclient = HttpClients.createDefault();
        try {
            // Create a User
            CloseableHttpResponse response =
                    createUser("George", "Burdell", "gburdell@gatech.edu");
            int userId = getIdFromResponse(response);

            // Create a Project
            response = createProject(userId, "test project");
            int projectId = getIdFromResponse(response);

            // Create a Session
            String startTime = "2019-01-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            response = createSession(userId, projectId, startTime, endTime, 0);
            int sessionId = getIdFromResponse(response);

            HttpEntity entity;
            String strResponse;

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(201, status);
            entity = response.getEntity();

            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }

    }


    ////////////////////////////////////////////////
    //              Helper Methods                //
    ////////////////////////////////////////////////

    private CloseableHttpResponse getUser(int userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                "\"lastName\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
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

    private CloseableHttpResponse getProject(int userId, int projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

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

    private CloseableHttpResponse createProject(int userId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"," +
                "\"userId\":\"" + userId + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSessions(int userId, int projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
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
