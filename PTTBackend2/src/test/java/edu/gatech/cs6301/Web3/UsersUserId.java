package edu.gatech.cs6301.Web3;

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

public class UsersUserId {
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
    /*
     * Test if PUT OK when firstName Empty. We will throw 400 error.
     */
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse response = updateUser("0", null, "Doe", "jdoe@gmail.com");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals( 404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if PUT OK when lastName Empty. We will throw 400 error.
     */
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse response = updateUser("1L", "Jane", null, "jdoe@gmail.com");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status );
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if PUT OK when email Empty. We will throw 400 error.
     */
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse response = updateUser("0", "Jane", "Doe", null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if PUT OK when userid Empty (0 because it's default to 0 when empty). We
     * will throw 404 error.
     */
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse response = updateUser("0L", "Jane", "Doe", "jdoe@gmail.com");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if PUT OK when userid Empty (0 because it's default to 0 when empty). We
     * will throw 404 error.
     */
    @Test
    public void pttTest4b() throws Exception {
        try {
            CloseableHttpResponse response = deleteUser("0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if PUT OK when userid Negative. We will throw 400 error.
     */
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse response = updateUser("1L", "Jane", "Doe", "jdoe@gmail.com");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 400);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if DELETE OK when userid Negative. We will throw 400 error.
     */
    @Test
    public void pttTest5b() throws Exception {
        try {
            CloseableHttpResponse response = deleteUser("0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if PUT OK when firstname : Not empty, lastname : Not empty, email : Not
     * empty, userid : Non-negative
     */
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("Tom", "Doe", "jdoe@gmail.com");
            String id = getIdFromResponse(response);
            response.close();
            response = updateUser(id, "Jane", "Doe", "jdoe@gmail.com");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"email\":\"jdoe@gmail.com\",\"firstName\":\"Jane\",\"id\":" + id
                    + ",\"lastName\":\"Doe\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, true);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test if DELETE OK when firstname : Not empty, lastname : Not empty, email :
     * Not empty, userid : Non-negative
     */
    @Test
    public void pttTest6b() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("Tom", "Doe", "jdoe@gmail.com");
            String id = getIdFromResponse(response);
            response.close();
            response = deleteUser(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"email\":\"jdoe@gmail.com\",\"firstName\":\"Tom\",\"id\":" + id
                    + ",\"lastName\":\"Doe\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, true);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
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

    private void deleteUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

    private CloseableHttpResponse deleteUser(String userId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId);
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
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

    private CloseableHttpResponse updateUser(String id, String firstName, String lastName, String email)
            throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

}
