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

public class Users_userId {

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

    // Purpose: Error test case when firstname field for PUT is empty
    @Test 
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUserWithEmptyFirstName(id, "Green", email);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            //this is expected to pass.
            Assert.assertEquals(200, status);

            if (status != 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Error test case when lastname field for PUT is empty
    @Test 
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();;
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUserWithEmptyLastName(id, "Tom", email);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            //last name can be empty in PUT
            Assert.assertEquals(200, status);

            if (status != 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that PUT works with existing ID and both firstname and lastname are changed
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser(id, "Tom", "Green", email);

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

            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"Tom\",\"id\":"+ id + ",\"lastName\":\"Green\"}";	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that PUT works with existing ID and firstname is changed and lastname is unchanged
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser(id, "Tom", "Doe", email);

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

            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"Tom\",\"id\":"+ id + ",\"lastName\":\"Doe\"}";	        JSONAssert.assertEquals(expectedJson,strResponse, false);
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that PUT works with existing ID and firstname is unchanged and lastname is changed
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser(id, "John", "Green", email);

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

            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ id + ",\"lastName\":\"Green\"}";	        JSONAssert.assertEquals(expectedJson,strResponse, false);
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that PUT works with existing ID and both firstname and lastname are unchanged
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser(id, "John", "Doe", email);

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

            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ id + ",\"lastName\":\"Doe\"}";	        JSONAssert.assertEquals(expectedJson,strResponse, false);
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for PUT when user ID does not exist
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            String id2 = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + id1 + id2; // making sure the ID is not present

            response = updateUser(missingId, "Tom", "Green", email);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id1);
            deleteUser(id2);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for PUT when user ID field is empty
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            String email = "john@doe.org";
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser("", "Tom", "Green", email);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            //internal server error
            Assert.assertEquals(405, status);

            if (status != 405) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test to GET user from ID when ID exists
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = getUser(id);

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

            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ id + ",\"lastName\":\"Doe\"}";	        JSONAssert.assertEquals(expectedJson,strResponse, false);
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test to GET user from ID when ID does not exist
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            String id2 = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + id1 + id2; // making sure the ID is not present

            response = getUser(missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id1);
            deleteUser(id2);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test to GET user from ID when ID field is empty
    @Test
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = getUser("");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            //returns all users instead
            Assert.assertEquals(200, status);

            if (status != 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that user is deleted when ID exists
    @Test
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String deleteid = getIdFromResponse(response);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteUser(deleteid);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ deleteid + ",\"lastName\":\"Doe\"}";            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllUsers();
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "[]";
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that user is not deleted when ID does not exist
    @Test
    public void pttTest13() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            String id2 = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + id1 + id2; // making sure the ID is not present

            response = deleteUser(missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
            
            deleteUser(id1);
            deleteUser(id2);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test that user is not deleted when ID field is empty
    @Test
    public void pttTest14() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = deleteUser("");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            //deletes all users
            Assert.assertEquals(200, status);

            if (status != 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }
    
    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                "\"lastname\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateUser(String id, String firstname, String lastname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id + "/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":\"" + id + "\"," +
                "\"firstname\":\"" + firstname + "\"," +
                "\"lastname\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateUserWithEmptyFirstName(String id, String lastname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":\"" + id + "\"," +
                "\"lastname\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateUserWithEmptyLastName(String id, String firstname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":\"" + id + "\"," +
                "\"firstname\":\"" + firstname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    
    private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id + "/");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id + "/");
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
                break;
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
