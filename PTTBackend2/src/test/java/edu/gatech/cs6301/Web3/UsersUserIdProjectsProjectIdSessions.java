package edu.gatech.cs6301.Web3;

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

public class UsersUserIdProjectsProjectIdSessions {

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

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    // Tests if POST returns OK when the starttime is empty. Status should be 400 since starttime cannot be empty 
    @Test
    public void pttTestSession1() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 1L, null, "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Tests if POST returns OK when the endtime is empty. Status should be 400 since endtime cannot be empty 
    @Test
    public void pttTestSession2() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 1L, "2019-02-18T20:00Z", null, 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if POST OK when counter is equal to 0 (OL), which is the default value of 'empty'
     * Since we assume that the counter starts from 1, the POST request needs to return a 400 status code (error)
     */
    @Test
    public void pttTestSession3() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 0L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test if POST OK when counter has a negative value (-1L)
    @Test
    public void pttTestSession4() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, -1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if POST OK when userId Empty (0L because empty is default to 0). We
     * assume the projectId in our databse will start from 1 so this POST request
     * would be expected to return 400 error code.
     */
    @Test
    public void pttTestSession5() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(0L, 1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if POST OK when userId Negative (-1L). We assume the userId in our databse
     * will start from 1 so this POST request would be expected to return 400 error
     * code.
     */
    @Test
    public void pttTestSession6() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(-1L, 1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if POST ok when projectId Empty (0L because empty is default to 0) We
     * assume the projectId in our databse will start from 1 so this POST request
     * would be expected to return 400 error code.
     */
    @Test
    public void pttTestSession7() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 0L, 0L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }


    /*
     * Test if POST OK when Projectid Negative (-1L). We assume the projectId in our
     * databse will start from 1 so this POST request would be expected to return
     * 400 error code.
     */
    @Test
    public void pttTestSession8() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, -1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

     /*
     * Test if POST OK when SessionId empty (0L). We assume the SessionId in our
     * databse will start from 1 so this POST request would be expected to return
     * 400 error code.
     */
    @Test
    public void pttTestSession9() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 0L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if POST OK when SessionId Negative (-1L). We assume the SessionId in our
     * databse will start from 1 so this POST request would be expected to return
     * 400 error code.
     */
    @Test
    public void pttTestSession10() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", -1L);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }



    /*
     * Test if POST OK when starttime : Not empty endtime : Not empty counter : One
     * userid : Non-negative projectid : Non-negative sessionId : Non-negative
     */
    @Test
    public void pttTestSession11() throws Exception {
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 1L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if GET OK when starttime : Not empty endtime : Not empty counter : zero
     * userid : Non-negative projectid : Non-negative
     */
    @Test
    public void pttTestSession12() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteSessions(1L, 1L);
        try {
            CloseableHttpResponse response = createSession(1L, 1L, 0L, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 1L);
            response.close();
            response = getSession(1L, 1L);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            entity = response.getEntity();

            strResponse = EntityUtils.toString(entity);
            String id = getIdFromStringResponse(strResponse);
            String counter = getCounterFromStringResponse(strResponse);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"counter\":\"" + counter + "\",\"endTime\":\"2019-02-18T20:00Z\",\"id\":\"" + id
                    + "\",\"startTime\":\"2019-02-18T20:00Z\"}";
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    
    private CloseableHttpResponse createSession(Long userId, Long projectId, Long counter, String endtime,
            String starttime, Long sessionId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"counter\":\"" + counter + "\"," + "\"endtime\":\"" + endtime + "\"," + "\"starttime\":\"" + starttime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSession(Long userId, Long projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/api/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    
    private CloseableHttpResponse deleteSessions(Long userId, Long projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(
                baseUrl + "/api/users/" + userId + "projects/" + projectId + "/sessions");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
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

    private String getCounterFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        String counter = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("counter")) {
                counter = object.get(key).toString();
            }
        }
        return counter;
    }

}