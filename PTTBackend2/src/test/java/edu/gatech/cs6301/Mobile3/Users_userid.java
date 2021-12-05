package edu.gatech.cs6301.Mobile3;

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

public class Users_userid {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL") + ":" + prop.getProperty("TEST_BASE_PORT");
    //private String baseUrl = "http://gazelle.cc.gatech.edu:9003/ptt";
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

    //Purpose: GET /users/{userId} with invalid {userId}
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = getUser("7");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with invalid {userId}
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = modifyUser("7","John","Doe","john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: DELETE /users/{userId} with invalid {userId}
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = deleteUser("7");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with firstName field missing in request body
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john@doe.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp,"NULL","Smith","john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            deleteUser(id_temp);
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with firstName field having zero length value in request body
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("Jane", "Doe", "jane@doe.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp,"EMPTY","Doe","jane@doe.org");
            int status = response.getStatusLine().getStatusCode();
            deleteUser(id_temp);
            Assert.assertEquals(200, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with lastName field missing in request body
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john@doe.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp,"John","NULL","john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            deleteUser(id_temp);
            Assert.assertEquals(200, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with only lastName field having zero length value in request body
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john@doe.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp,"John","EMPTY","john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            deleteUser(id_temp);
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with email field missing in request body
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("Jane", "Doe", "jane@doe.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp,"Jane","Doe","NULL");
            int status = response.getStatusLine().getStatusCode();
            String strResponse;
            deleteUser(id_temp);

            Assert.assertEquals(200, status);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    //Purpose: PUT /users/{userId} with email field having zero length value in request body
    @Test
    public void pttTest9() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("Jane", "Doe", "jane@doe.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp,"John","Doe","EMPTY");
            int status = response.getStatusLine().getStatusCode();
            String strResponse;
            deleteUser(id_temp);

            Assert.assertEquals(200, status);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // Purpose: GET /users/{userId} with valid userId
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = getUser(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            deleteUser(id);
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: PUT /users/{userId} with valid userId but updates the email field, which is invalid and should thus error.
    @Test
    public void pttTest11() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jane", "Doe", "jane@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = modifyUser(id, "John", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            deleteUser(id);


            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: DELETE /users/{userId} with valid userId
    @Test
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = null;
        HttpEntity entity1;
        String strResponse1;
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            String deleteid = getIdFromResponse(response);
            System.out.println(deleteid);
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

            expectedJson = "{\"id\":" + deleteid + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // Purpose: PUT /users/{userId} with valid userId
    @Test
    public void pttTest13() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jane", "Smith", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = modifyUser(id, "John", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            deleteUser(id);
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse modifyUser(String id, String firstname, String lastname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if(id.equals("NULL")==true) //Field is absent
            input = new StringEntity("{\"firstName\":\"" + firstname +
                    "\"," + "\"lastName\":\"" + lastname +
                    "\"," + "\"email\":\"" + email + "\"}");
        else if(id.equals("EMPTY")==true) //Field is empty
            input = new StringEntity("{\"id\":\"" + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(firstname.equals("NULL") == true) // Field is absent
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(firstname.equals("EMPTY") == true) // Field is empty
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(lastname.equals("NULL") == true) // Field is absent
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(lastname.equals("EMPTY") == true) // Field is empty
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(email.equals("NULL") == true) // Field is absent
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"}");
        else if(email.equals("EMPTY") == true) // Field is empty
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + "\"}");
        else
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
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
    private void deleteUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}