package edu.gatech.cs6301.Web1;

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

public class Users_UserId_Projects_ProjectId {
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

    private void deleteUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

    // Purpose: Tests if a GET request with a null user id returns 400 code
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = getUser(null);
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("*** Correct response returned, null userId invalid");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expected 400");
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests if a GET request with a negative user id returns 404 code
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = getUser("-1");

            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("*** Correct response returned, -1 userId invalid");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expected 404");
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests if a GET request with a valid but unused user id returns 201 code
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("John", "Smith", "johnsmith@gmail.com");
            String userId = getIdFromResponse(response);
            int status = response.getStatusLine().getStatusCode();

            response = getUser(userId + userId);
            if (status == 201) {
                System.out.println("*** Correct response returned, user id not sent");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expecting 201");
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests if a GET request with a null project id returns 201 code
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "johndoe@gmail.com");
            String userId = getIdFromResponse(response);
            int status = response.getStatusLine().getStatusCode();

            response = getProject(userId, null);
            if (status == 201) {
                System.out.println("*** Correct response returned, project name not sent");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expecting 201");
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests if a GET request with a negative project id returns 201 code
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("John", "Gru", "johngru@gmail.com");
            String userId = getIdFromResponse(response);
            int status = response.getStatusLine().getStatusCode();

            response = getProject(userId, "-1");
            if (status == 201) {
                System.out.println("*** Correct response returned, project name not sent");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expecting 201");
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests if a GET request with a valid but unused project id returns 201 code
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("John", "Fus", "johnfus@gmail.com");
            String userId = getIdFromResponse(response);
            int status = response.getStatusLine().getStatusCode();

            response = getProject(userId, "1");
            if (status == 201) {
                System.out.println("*** Correct response returned, user id not found");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expecting 201");
            }

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests if a GET request with a valid project id returns 200 code
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();
        deleteProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("John", "Dun", "johndun@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "CS6301");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getProject(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + ", expecting 200");
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"projectname\":\"CS6301\",\"id\":" + projectId + "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"firstname\":\"" + firstname + "\"," +
                        "\"lastname\":\"" + lastname + "\"," +
                        "\"email\":\"" + email + "\"}"
        );
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

    private CloseableHttpResponse createProject(String userId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"projectName\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
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


}
