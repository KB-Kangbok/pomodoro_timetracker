package edu.gatech.cs6301.Backend2;

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

public class UsersUseridProjectsProjectid {

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

//    @Test
    // Purpose: Url of bad_request
//    public void pttTest1() throws Exception {
//        httpclient = HttpClients.createDefault();
//        deleteUsers();
//        deleteProjects();
//
//        try {
//            HttpGet httpRequest = new HttpGet(baseUrl + "/bad_request");
//            httpRequest.addHeader("accept", "application/json");
//
//            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
//            CloseableHttpResponse response = httpclient.execute(httpRequest);
//            System.out.println("*** Raw response " + response + "***");
//
//
//            int status = response.getStatusLine().getStatusCode();
//            HttpEntity entity;
//            String strResponse;
//            if (status != 400) {
//                throw new ClientProtocolException("Unexpected response status: " + status);
//            }
//            response.close();
//        } finally {
//            httpclient.close();
//        }
//    }

    @Test
    // Purpose: invalid userid not found
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = getUsersUseridProjectsProjectid("1", "1");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test invalid user id which contains special character
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = getUsersUseridProjectsProjectid("!!!", "1");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status != 400) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test invalid project id
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects("1", "test", userId);
            response = getUsersUseridProjectsProjectid("1", "@@");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status != 400) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test invalid project id not found
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects("1", "test", userId);
            response = getUsersUseridProjectsProjectid("1", "0");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects("1", "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectid(userId, projectId);

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

            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"test\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test delete /users/{userId}/projects/{projectId}
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects("1", "test", userId);
            String projectId = getIdFromResponse(response);

            response = deleteUsersUseridProjectsProjectid(userId, projectId);

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

            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"test\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse getUsersUseridProjectsProjectid(String userid, String projectid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userid + "/projects/" + projectid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUsersUseridProjectsProjectid(String userid, String projectid) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userid + "/projects/" + projectid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postInvalidUsersUseridProjects(String id, String projectname, String userid, String other) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":\"" + userid + "\"," +
                "\"projectname\":\"" + projectname + "\"," +
                "\"other\":\"" + other + "\"," +
                "\"userid\":\"" + userid + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postUsers(String email, String firstName, String id, String lastName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
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

    private CloseableHttpResponse postUsersUseridProjects(String id, String projectname, String userid) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"id\":\"" + userid + "\"," +
                "\"projectname\":\"" + projectname + "\"," +
                "\"userid\":\"" + userid + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postInvalidUsers(String email, String firstName, String id, String lastName, String other) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"email\":\"" + email + "\"," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"other\":\"" + other + "\"," +
                "\"lastName\":\"" + lastName + "\"}");
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
        while (keyList.hasNext()) {
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

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

}
