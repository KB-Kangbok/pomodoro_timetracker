package edu.gatech.cs6301.Backend3;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class UsersUserIdProjectsProjectIdReportTests {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9009/ptt";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;
    
    @Before
    public void runBefore() throws Exception {
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
    deleteUsers();
    }

    @After
    public void runAfter() {
        System.out.println("*** ENDING TEST ***");
    }

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***
    // Purpose: Testing non-numeric userid
    @Test
    public void pttTest1() throws Exception {

        try {
            CloseableHttpResponse response = getReport("", "1",
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            // non-numeric is bad request
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //  Purpose: Testing nonexistent userid
    @Test
    public void pttTest2() throws Exception {

        try {
            CloseableHttpResponse response = getReport("0", "1",
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            // non-existent is not found
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    // Purpose: Testing non-numeric project id
    @Test
    public void pttTest3() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, "hello",
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            // non-numeric is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing nonexistent project id
    @Test
    public void pttTest4() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, "1",
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            // non-existent is not found
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing empty from
    @Test
    public void pttTest5() throws Exception {
        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            // Setting up valid projectid
            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId,
                    "", "2019-02-18T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            // empty from time is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing non timestamp from
    @Test
    public void pttTest6() throws Exception {
        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            // Setting up valid projectid
            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId,
                    "asdf", "2019-02-18T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            // non timestamp from is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing empty to
    @Test
    public void pttTest7() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();


            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId,
                    "2019-02-18T20:00Z", "", false, false);
            int status = response.getStatusLine().getStatusCode();
            // empty to is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing empty to
    @Test
    public void pttTest8() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId,
                    "2019-02-18T20:00Z", "hello", false, false);
            int status = response.getStatusLine().getStatusCode();
            // non timestamp to is bad request
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing normal case
    @Test
    public void pttTest9() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = createSession(userId, projectId,
                    "2019-02-19T20:00Z", "2019-02-19T21:00Z", 2);
            response.close();

            response = getReport(userId, projectId,
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z",
                    true, true);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing no total hours worked
    @Test
    public void pttTest10() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = createSession(userId, projectId,
                    "2019-02-19T20:00Z", "2019-02-19T21:00Z", 2);
            response.close();

            response = getReport(userId, projectId,
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z",
                    true, false);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing  no completed pomodoros
    @Test
    public void pttTest11() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = createSession(userId, projectId,
                    "2019-02-19T20:00Z", "2019-02-19T21:00Z", 2);
            response.close();

            response = getReport(userId, projectId,
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z",
                    false, true);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Testing no completed pomodoros, no total hours
    @Test
    public void pttTest12() throws Exception {

        try {
            // Setting up valid userId
            CloseableHttpResponse response = createUser("John", "Doe", "test@test.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "test");
            String projectId = getIdFromResponse(response);
            response.close();

            response = createSession(userId, projectId,
                    "2019-02-19T20:00Z", "2019-02-19T21:00Z", 2);
            response.close();

            response = getReport(userId, projectId,
                    "2019-02-18T20:00Z", "2019-02-20T20:00Z",
                    false, true);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    // Util Functions
    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws  IOException {
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

    private CloseableHttpResponse createProject(String userId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectName\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createSession(String userId, String projectId,
                                                String startTime, String endTime, int counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime +  "\"," +
                "\"startTime\":\"" + endTime +  "\"," +
                "\"counter\":\"" + counter +  "\"," +"\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getReport(String userId, String projectId, String from, String to, boolean includeCompletedPomodoros, boolean includeTotalHoursWorkedOnProject) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report?from=" + from + "&to=" + to + "&includeCompletedPomodoros=" + includeCompletedPomodoros +"&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject);
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
    private void deleteUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}
