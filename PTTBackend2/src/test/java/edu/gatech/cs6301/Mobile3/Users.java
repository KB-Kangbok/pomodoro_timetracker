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

public class Users {

    Properties prop = readPropertiesFile("src/main/resources/test.properties");
    private String baseUrl = prop.getProperty("TEST_BASE_URL");
    // private String baseUrl = "http://gazelle.cc.gatech.edu:9003/ptt";
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

    //Purpose: GET /users
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        String id=null,id_1 = null;
        String expectedJson = "";
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            expectedJson = "[{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            response.close();

            response = createUser("Jane", "Doe", "jane@doe.com");
            id_1 = getIdFromResponse(response);
            expectedJson += ",{\"id\":" + id_1 + ",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@doe.com\"}]";
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
            deleteUser(id_1);
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (No id field, other parameters have non-zero length values in request body)
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();


        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (id field is sent in request body and other parameters have non-zero length values in request body)
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("0","Jane", "Doe", "jane@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }
    //Purpose: POST /users (firstName field in request body missing, but other parameters have non-zero length values in request body)
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("", "Doe", "jane@doe.org");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (firstName field in request body has zero length value, but other parameters have non-zero length values in request body)
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("", "Doe", "jane@doe.org");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (lastName field in request body missing, but other parameters have non-zero length values in request body)
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "", "john@doe.com");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (lastName field in request body has zero length value, but other parameters have non-zero length values in request body)
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "EMPTY", "john@doe.com");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (email field in request body missing, but other parameters have non-zero length values in request body)
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("Jane", "Doe", "NULL");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (email field in request body has zero length value, but other parameters have non-zero length values in request body)
    @Test
    public void pttTest9() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("Jane", "Doe", "EMPTY");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: POST /users (id is sent in the body with random number, all parameters with non-zero length values in request body)
    @Test
    public void pttTest10() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response =
                    createUser("44","John", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
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
        StringEntity input;

        if(firstname.equals("NULL") == true) // Field is absent
            input = new StringEntity("{\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(firstname.equals("EMPTY") == true) // Field is empty
            input = new StringEntity("{\"firstName\":\"" + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(lastname.equals("NULL") == true) // Field absent
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(lastname.equals("EMPTY") == true) // Field empty
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(email.equals("NULL") == true) // Field absent
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"}");
        else if(email.equals("EMPTY") == true) // Field empty
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + "\"}");
        else
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");


        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

// Purpose: Overloading the createUser with id incase one is passed, although it is not used.
    private CloseableHttpResponse createUser(String id, String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;

        if(id.equals("EMPTY")==true) //ID Field empty
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


    private CloseableHttpResponse getAllUsers() throws IOException {
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

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
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

}