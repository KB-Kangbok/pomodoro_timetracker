package edu.gatech.cs6301.Web2;

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

public class Users {

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

    /**
     * Purpose： check if email is empty
     * @throws Exception
     */
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            CloseableHttpResponse response = createUserWithEmptyEmail("John", "Doe");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(400, status);

            if (status != 400) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /**
     * Purpose： check if first name is empty
     * @throws Exception
     */
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        //this should work
        try {
            deleteUsers();
            CloseableHttpResponse response = createUserWithEmptyFirstName("Doe", "321@gmail.com");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(201, status);

            if (status != 201) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /**
     * Purpose： check if last name is empty
     * @throws Exception
     */
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUserWithEmptyLastName("John", "321@gmail.com");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            Assert.assertEquals(201, status);

            if (status != 201) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /**
     * Purpose： check if the post method can work
     * @throws Exception
     */
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        String id = null;
        String expectedJson = "";

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            HttpEntity thisEntity = response.getEntity();
            String resp = EntityUtils.toString(thisEntity);
            id = getIdFromStringResponse(resp);
            expectedJson += "[{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}]";
            response.close();

            response = getAllUsers();

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

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that the email already exists
     * @throws Exception
     */
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        String id = null;
        String expectedJson = "";

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            HttpEntity thisEntity = response.getEntity();
            String resp = EntityUtils.toString(thisEntity);
            id = getIdFromStringResponse(resp);
            expectedJson += "[{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}]";
            response.close();

            response = createUser("Jane", "Wall", "john@doe.org");
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllUsers();

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

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }


    /**
     * in the case that database is not empty, check the post method can work
     * @throws Exception
     */
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = "";

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            HttpEntity thisEntity = response.getEntity();
            String resp = EntityUtils.toString(thisEntity);
            String id1 = getIdFromStringResponse(resp);
            expectedJson += "[{\"id\":" + id1 + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            HttpEntity thisEntity2 = response.getEntity();
            String resp2 = EntityUtils.toString(thisEntity2);
            String id2 = getIdFromStringResponse(resp2);
            expectedJson += ", {\"id\":" + id2 + ",\"firstName\":\"Jane\",\"lastName\":\"Wall\",\"email\":\"jane@wall.com\"}]";
            response.close();

            response = getAllUsers();

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

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id1);
            deleteUser(id2);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check if the get method can work when the database is empty
     * @throws Exception
     */
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = "[]";

        try {
            CloseableHttpResponse response = getAllUsers();

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

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /**
     * check if the get method can work when the database is not empty
     * @throws Exception
     */
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = "";

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            HttpEntity thisEntity = response.getEntity();
            String resp = EntityUtils.toString(thisEntity);
            String id1 = getIdFromStringResponse(resp);
            expectedJson += "[{\"id\":" + id1 + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            HttpEntity thisEntity2 = response.getEntity();
            String resp2 = EntityUtils.toString(thisEntity2);
            String id2 = getIdFromStringResponse(resp2);
            expectedJson += ", {\"id\":" + id2 + ",\"firstName\":\"Jane\",\"lastName\":\"Wall\",\"email\":\"jane@wall.com\"}]";
            response.close();

            response = getAllUsers();

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

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id1);
            deleteUser(id2);
        } finally {
            httpclient.close();
        }
    }

    /**
     * create a new user and post it
     * @param firstName
     * @param lastName
     * @param email
     * @return
     * @throws IOException
     */
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

    /**
     * create a new user without first name passed in
     * @param lastName
     * @param email
     * @return
     * @throws IOException
     */
    private CloseableHttpResponse createUserWithEmptyFirstName(String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    /**
     * create a new user without last name passed in
     * @param firstName
     * @param email
     * @return
     * @throws IOException
     */
    private CloseableHttpResponse createUserWithEmptyLastName(String firstName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"firstName\":\"" + firstName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    /**
     * create a new user without email passed in
     * @param firstName
     * @param lastName
     * @return
     * @throws IOException
     */
    private CloseableHttpResponse createUserWithEmptyEmail(String firstName, String lastName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    /**
     * retrieve all existing users from database
     * @throws IOException
     */
    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + id);
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
