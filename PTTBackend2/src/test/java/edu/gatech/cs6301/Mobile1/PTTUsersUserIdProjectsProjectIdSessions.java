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

public class PTTUsersUserIdProjectsProjectIdSessions {

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

    // Purpose: userId is null
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        try {
            // get all sessions with null project id
            response = getAllSessions(null, projectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: userId is not found
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        try {
            // get all sessions with null project id
            response = getAllSessions(userId + "NotFound", projectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: body is missing for posting session
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        try {
            // create session
            response = createSessionWithoutBody(userId, projectId + "NotFound");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(500, status);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: userId is found, project id is null, for getting all sessions
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        try {
            // create session 1
            String startTime1 = "2019-02-18T20:00Z";
            String endTime1 = "2019-02-18T20:00Z";
            int counter1 = 0;
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            String sessionId = getIdFromResponse(response);

            // create session 2
            String startTime2 = "2020-02-18T20:00Z";
            String endTime2 = "2020-02-18T20:00Z";
            int counter2 = 1;
            response = createSession(userId, projectId, startTime2, endTime2, counter2);
            sessionId = getIdFromResponse(response);

            // get all sessions
            response = getAllSessions(userId, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: userId is found, project id is not found, for getting all sessions
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        try {
            // create session 1
            String startTime1 = "2019-02-18T20:00Z";
            String endTime1 = "2019-02-18T20:00Z";
            int counter1 = 0;
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            String sessionId = getIdFromResponse(response);

            // create session 2
            String startTime2 = "2020-02-18T20:00Z";
            String endTime2 = "2020-02-18T20:00Z";
            int counter2 = 1;
            response = createSession(userId, projectId, startTime2, endTime2, counter2);
            sessionId = getIdFromResponse(response);

            // get all sessions
            response = getAllSessions(userId, projectId + "NotFound");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: userId is found, project id is found, for getting all sessions
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        String expectedJson = "";

        try {
            // create session 1
            String startTime1 = "2019-02-18T20:00Z";
            String endTime1 = "2019-02-18T20:00Z";
            int counter1 = 0;
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            String sessionId = getIdFromResponse(response);
            expectedJson += "[{\"id\":\"" + sessionId + "\",\"starttime\":\"" + startTime1 + "\",\"endtime\":\"" + endTime1 + "\",\"counter\":\""+ counter1 +"\"}, ";

            // create session 2
            String startTime2 = "2020-02-18T20:00Z";
            String endTime2 = "2020-02-18T20:00Z";
            int counter2 = 1;
            response = createSession(userId, projectId, startTime2, endTime2, counter2);
            sessionId = getIdFromResponse(response);
            expectedJson += "{\"id\":\"" + sessionId + "\",\"starttime\":\"" + startTime2 + "\",\"endtime\":\"" + endTime2 + "\",\"counter\":\""+ counter2 +"\"}]";

            // get all sessions
            response = getAllSessions(userId, projectId);
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

    // Purpose: userId is found, project id is null, body is present, for posting a new session
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // project id is null
        String projectId = null;

        try {
            // create session
            String startTime1 = "2019-02-18T20:00Z";
            String endTime1 = "2019-02-18T20:00Z";
            int counter1 = 0;
            response = createSession(userId, projectId, startTime1, endTime1, counter1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: userId is found, project id is not found, body is present, for posting a new session
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        try {
            // create session
            String startTime1 = "2019-02-18T20:00Z";
            String endTime1 = "2019-02-18T20:00Z";
            int counter1 = 0;
            response = createSession(userId, projectId + "NotFound", startTime1, endTime1, counter1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: userId is found, project id is found, body is present, for posting a new session
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        // create user
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();

        // create project
        response = createProject(userId, "projectName");
        String projectId = getIdFromResponse(response);
        response.close();

        String expectedJson = "";

        try {
            // create session
            String startTime1 = "2019-02-18T20:00Z";
            String endTime1 = "2019-02-18T20:00Z";
            int counter1 = 0;
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            String sessionId = getIdFromResponse(response);
            expectedJson += "{\"id\":\"" + sessionId + "\",\"starttime\":\"" + startTime1 + "\",\"endtime\":\"" + endTime1 + "\",\"counter\":\""+ counter1 +"\"}";

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

    private CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime, int counter) throws IOException {

        HttpPost httpRequest = new HttpPost(baseUrl + "/users" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"starttime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createSessionWithoutBody(String userId, String projectId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllSessions(String userId, String projectId) throws IOException {

        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
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
}
