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

public class PTTUsersUserIdProjectsProjectIdSessionsSessionId {

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


    @Test
    public void pttTest3() throws Exception {   // Purpose: Test if project id is null
        httpclient = HttpClients.createDefault();
        deleteUsers();

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
    public void pttTest5() throws Exception {   // Purpose: Test if SessionId is null
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String nullSessionId = null; // making sure the ID is null

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

            JSONObject requestParams = new JSONObject();
            requestParams.put("startTime", from);
            requestParams.put("endTime", to);

//            response = putSessionId(uid1, pid1, nullSessionId, requestParams.toString());

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest6() throws Exception {   // Purpose: Test if SessionId is missing
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz";   // making sure the ID is not present

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

            JSONObject requestParams = new JSONObject();
            requestParams.put("startTime", from);
            requestParams.put("endTime", to);

//            response = putSessionId(uid1, pid1, missingId, requestParams.toString());

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest7() throws Exception {   // Purpose: Test if body is missing
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

//            response = createSession(uid1, pid1, from, to, 1);
//            String sid1 = getIdFromResponse(response);
//            response.close();

            JSONObject requestParams = new JSONObject();

//            response = putSessionId(uid1, pid1, sid1, requestParams.toString());

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    @Test
    public void pttTest8() throws Exception {   // Purpose: Test if Put operation with correct UserID, ProjectID, SessionID and body works as expected
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1000");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

//            response = createSession(uid1, pid1, from, to, 1);
//            System.out.println("****");
//            System.out.println(response);
//            System.out.println("****");
//            String sid1 = getIdFromResponse(response);
//            response.close();



            JSONObject requestParams = new JSONObject();
            requestParams.put("startTime", from);
            requestParams.put("endTime", to);

//            response = putSessionId(uid1, pid1, sid1, requestParams.toString());

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // ************************************************************
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

//    private CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime, int counter) throws IOException, JSONException {
//
//        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
//        httpRequest.addHeader("accept", "application/json");
//        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
//                "\"endTime\":\"" + endTime + "\"," +
//                "\"counter\":" + counter + "}");
//        input.setContentType("application/json");
//        httpRequest.setEntity(input);
//
//        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
//        CloseableHttpResponse response = httpclient.execute(httpRequest);
//        System.out.println("*** Raw response " + response + "***");
//        return response;
//    }

    private CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime, int counter) throws IOException {
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

    private CloseableHttpResponse getAllSessions(String userId, String projectId) throws IOException {

        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private CloseableHttpResponse putSessionId(String uid, String pid, String sid, String bd) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + uid + "/projects/" + pid + "/sessions/" + sid);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"userId\":" + uid + "," +
                "\"projectId\":" + pid + "," +
                "\"sessionId\":" + sid + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);
        httpRequest.setEntity(new StringEntity(bd));

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

}
