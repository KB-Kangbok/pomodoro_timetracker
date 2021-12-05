package edu.gatech.cs6301.Backend3;

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

public class UsersUserIdTests {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9009/ptt";
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

    // purpose: test non numeric id
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = getUser("retydf354dfg");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // purpose: test no id
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = getUser("");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // purpose: test empty first name
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {	
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);

            response.close();
            // TEST DELETE
            response = deleteUser(userId);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    
    // purpose: test empty last name
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {	
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "John", "", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);

            response.close();
        } finally {
            httpclient.close();
        }
    }
    
    // purpose: test no email
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "John", "Doe", "");

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);

            response.close();

        } finally {
            httpclient.close();
        }
    }

    // purpose: test invalid email
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "John", "Doe", "rthrgreg");

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(200, status);

            response.close();
            
        } finally {
            httpclient.close();
        }
    }
    
    // purpose: test alpha-numeric first and last name
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {	
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "John1", "Doe2", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"id\":" + userId + ",\"firstName\":\"John1\",\"lastName\":\"Doe2\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            
        } finally {
            httpclient.close();
        }
    }
    
    // purpose: test special-character first name and alpha-numeric last name
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();

        try {	
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "!@#$", "Doe2", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"id\":" + userId + ",\"firstName\":\"!@#$\",\"lastName\":\"Doe2\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            
        } finally {
            httpclient.close();
        }
    }
    
    // purpose: test alpha-numeric first name and special character last name
    @Test
    public void pttTest9() throws Exception {
        deleteUsers();

        try {	
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "John1", "!@#$", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"id\":" + userId + ",\"firstName\":\"John1\",\"lastName\":\"!@#$\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            // TEST DELETE
            response = deleteUser(userId);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            
        } finally {
            httpclient.close();
        }
    }
    
    // purpose: test both special character first and last name
    @Test
    public void pttTest10() throws Exception {
        deleteUsers();

        try {	
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");;
            String userId = getIdFromResponse(response);
            response.close();
            
            // TEST PUT
            response = updateUser(userId, "!@#$", "%^&*", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"id\":" + userId + ",\"firstName\":\"!@#$\",\"lastName\":\"%^&*\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            // TEST DELETE
            response = deleteUser(userId);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            
            
        } finally {
            httpclient.close();
        }
    }

    // utils
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
    private CloseableHttpResponse getUser(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse updateUser(String userid, String firstName, String lastName, String email) throws  IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userid);
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
