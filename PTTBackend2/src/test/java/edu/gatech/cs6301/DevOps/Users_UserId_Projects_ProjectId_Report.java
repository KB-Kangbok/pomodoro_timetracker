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

import org.skyscreamer.jsonassert.JSONAssert;

public class Users_UserId_Projects_ProjectId_Report{
    //    private String baseUrl = "http://gazelle.cc.gatech.edu:9010/ptt";
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
    // Purpose: test POST error for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest0() throws Exception {
        deleteUsers();
        deleteProjects();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T21:00Z", "0");
            String id = getIdFromResponse(response);

            response = updateSession(user_id, project_id, id, "2019-02-18T21:00Z", "2019-02-18T22:00Z", "1");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + "," + "\"startTime\":\"2019-02-18T21:00Z\"," + "\"endTime\":\"2019-02-18T22:00Z\"," + "\"counter\": 1}";
    	    JSONAssert.assertEquals(expectedJson,strResponse, false);

            response = updateSession(user_id, project_id, id, "2019-01-18T20:00Z", "2019-01-18T21:00Z", "1");
            status = response.getStatusLine().getStatusCode();
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            id = getIdFromStringResponse(strResponse);

            expectedJson = "{\"id\":" + id + "," + "\"startTime\":\"2019-01-18T20:00Z\"," + "\"endTime\":\"2019-01-18T21:00Z\"," + "\"counter\":1}";
    	    JSONAssert.assertEquals(expectedJson,strResponse, false);

            response = updateSession(user_id, project_id, id, "2019-04-18T20:00Z", "2019-04-18T21:00Z", "1");
            status = response.getStatusLine().getStatusCode();
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            id = getIdFromStringResponse(strResponse);

            expectedJson = "{\"id\":" + id + "," + "\"startTime\":\"2019-04-18T20:00Z\"," + "\"endTime\":\"2019-04-18T21:00Z\"," + "\"counter\":1}";
    	    JSONAssert.assertEquals(expectedJson,strResponse, false);

            response = updateSession(user_id, project_id, id, "2019-05-18T20:00Z", "2019-05-18T21:00Z", "1");
            status = response.getStatusLine().getStatusCode();
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            id = getIdFromStringResponse(strResponse);

            expectedJson = "{\"id\":" + id + "," + "\"startTime\":\"2019-05-18T20:00Z\"," + "\"endTime\":\"2019-05-18T21:00Z\"," + "\"counter\":1}";
    	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test POST error for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);

            response = postReport(userId, projectId);
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(405, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when userId not valid for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";
            String to = "2019-03-18T22:00Z";

            response = getReport(userId + "0", projectId, from, to);
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when projectId not valid for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";
            String to = "2019-03-18T22:00Z";

            response = getReport(userId, projectId + "0", from, to);
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when from date not valid for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String to = "2019-03-18T22:00Z";

            response = getReport(userId, projectId, "test", to);
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET error when to date not valid for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";

            response = getReport(userId, projectId, from, "test");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(400, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET w/ f f for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";
            String to = "2019-03-18T22:00Z";

            response = getUsersUseridProjectsProjectidReport(userId, projectId, from, to, "false", "false");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET w/ t f for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";
            String to = "2019-03-18T22:00Z";

            response = getUsersUseridProjectsProjectidReport(userId, projectId, from, to, "true", "false");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET w/ f t for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";
            String to = "2019-03-18T22:00Z";

            response = getUsersUseridProjectsProjectidReport(userId, projectId, from, to, "false", "true");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);
        } finally {
            this.httpclient.close();
        }
    }

    // Purpose: test GET w/ t t for /users/{userId}/projects/{projectId}/report
    @Test
    public void pttTest9() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject("Project", userId);
            String projectId = getIdFromResponse(response);
            String from = "2019-03-18T20:00Z";
            String to = "2019-03-18T22:00Z";

            response = getUsersUseridProjectsProjectidReport(userId, projectId, from, to, "true", "true");
            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);
        } finally {
            this.httpclient.close();
        }
    }

    private CloseableHttpResponse postReport(String userId, String projectId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private CloseableHttpResponse createUser(String firstname, String familyname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"lastName\":\"" + familyname + "\"," +
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

    private CloseableHttpResponse deleteProjects(String user_id, String project_id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
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

    private CloseableHttpResponse putProjects(String user_id, String project_id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
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

    private CloseableHttpResponse createSession(String user_id, String project_id, String start, String end, String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + start + "\"," +
                "\"endTime\":\"" + end + "\"," +
                "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSessions(String user_id, String project_id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessions(String user_id, String project_id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteSessions(String user_id, String project_id) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteSession(String user_id, String project_id, String session_id) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions/" + session_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSession(String user_id, String project_id, String session_id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions/" + session_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postSession(String user_id, String project_id, String session_id) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions/" + session_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateSession(String user_id, String project_id, String session_id, String start, String end, String counter) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions/" + session_id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + start + "\"," +
                "\"endTime\":\"" + end + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

    protected CloseableHttpResponse getReport(String userId, String projectId,
                                              String from, String to) throws IOException, JSONException {
        return this.getReport(userId, projectId, from, to, new JSONObject());
    }

    protected CloseableHttpResponse getReport(int userId, int projectId,
                                              String from, String to, JSONObject data) throws IOException,
            JSONException {
        data.put("from", from);
        data.put("to", to);
        final Http request = Http.api("users", String.valueOf(userId),
                "projects", String.valueOf(projectId),
                "report");
        return request.params(data).get();
    }

    protected CloseableHttpResponse getReport(String userId, String projectId,
                                              String from, String to, JSONObject data) throws IOException,
            JSONException {
        data.put("from", from);
        data.put("to", to);
        final Http request = Http.api("users", String.valueOf(userId),
                "projects", String.valueOf(projectId),
                "report");
        return request.params(data).get();
    }

    private CloseableHttpResponse getUsersUseridProjectsProjectidReport(String userid, String projectid, String from, String to, String includeCompletedPomodoros, String includeTotalHoursWorkedOnProject) throws IOException {
        String url = baseUrl + "/users/" + userid + "/projects/" + projectid + "/report?from=" + from + "&to=" + to;
        if (includeCompletedPomodoros != null && includeCompletedPomodoros.length() > 0) {
            url += "&includeCompletedPomodoros=" + includeCompletedPomodoros;
        }
        if (includeTotalHoursWorkedOnProject != null && includeTotalHoursWorkedOnProject.length() > 0) {
            url += "&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject;
        }
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

}
