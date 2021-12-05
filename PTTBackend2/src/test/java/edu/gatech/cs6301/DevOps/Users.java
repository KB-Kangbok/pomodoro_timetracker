package edu.gatech.cs6301.DevOps;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import static edu.gatech.cs6301.ReadProperties.readPropertiesFile;

public class Users {
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

    // Purpose: test GET functionality for /users endpoint
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            JSONArray expected = new JSONArray();
            JSONObject expectedObj = new JSONObject();
            expectedObj.put("id", id);
            expected.put(expectedObj);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            id = getIdFromResponse(response);
            expectedObj = new JSONObject();
            expectedObj.put("id", id);
            expected.put(expectedObj);
            response.close();

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if /users endpoint fails for any request besides POST and GET
    @Test
    public void pttTest2() throws Exception {
        deleteAllUsers();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john@doe.org");
            response.close();

//            response = putUsers();
            System.out.println(response.getStatusLine().getStatusCode());
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
            response.close();

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if input firstName is a valid String for POST method for endpoint /users
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        CloseableHttpResponse response = null;
        try {
            // could potentially test for other types
            response = createUser("12", "Doe", "john@doe.org");
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
            response.close();

            response = createUser("[0, 1, 2, 3]", "Doe", "john@doe.org");
            Assert.assertEquals(409, response.getStatusLine().getStatusCode());
            response.close();

            response = createUser("", "Doe", "john@doe.org");
            Assert.assertEquals(409, response.getStatusLine().getStatusCode());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if input lastname is valid String for POST method for endpoint /users
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "12", "john@doe.org");
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
            response.close();

            response = createUser("John", "[0, 1, 2, 3]", "john@doe.org");
            Assert.assertEquals(409, response.getStatusLine().getStatusCode());
            response.close();

            response = createUser("John", "", "john@doe.org");
            Assert.assertEquals(409, response.getStatusLine().getStatusCode());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if input email is valid email string for POST method for endpoint /users
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "johndoe.org");
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
            response.close();

            response = createUser("John", "Doe", "12");
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
            response.close();

            response = createUser("John", "Doe", "[0, 1, 2]");
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if POST method works for endpoint /users
    public void pttTest6() throws Exception {
        deleteAllUsers();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john@doe.org");
            Assert.assertEquals(201, response.getStatusLine().getStatusCode());
            String id = getIdFromResponse(response);
            JSONObject expectation = new JSONObject();
            expectation.put("id", id);
            expectation.put("firstName", "John");
            expectation.put("lastName", "Doe");
            expectation.put("email", "john@doe.org");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private void deleteAllUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

    public CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
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