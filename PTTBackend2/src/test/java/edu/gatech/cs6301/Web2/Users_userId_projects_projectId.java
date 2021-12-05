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

public class Users_userId_projects_projectId {

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
     * check if the get method can work if there exist users and projects
     * @throws Exception
     */
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            response = getProject(userId, projectId);

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

            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"first project\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that the project id is not existing
     * @throws Exception
     */
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + projectId;

            response = getProject(userId, missingId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that the project id is empty
     * @throws Exception
     */
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            createProject("first project", userId);

            response = getProject(userId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            //this should be a successful request
            Assert.assertEquals(200, status);
            if (status != 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that the user id is not existing in db
     * @throws Exception
     */
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + userId;

            response = getProject(missingId, projectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that the user id is empty
     * @throws Exception
     */
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            response = getProjectWithEmptyUser(projectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check if the delete method can work if there exist users and projects
     * @throws Exception
     */
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            response = deleteProject(userId, projectId);

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

            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"first project\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that if the project is not existing in db
     * @throws Exception
     */
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + projectId;

            response = deleteProject(userId, missingId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that if the project is empty
     * @throws Exception
     */
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            createProject("first project", userId);

            response = deleteProject(userId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(405, status);
            if (status != 405) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that if the user is not existing
     * @throws Exception
     */
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + userId;

            response = deleteProject(missingId, projectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    /**
     * check the situation that if the user id is empty
     * @throws Exception
     */
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();
        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "123@gmail.com");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject("first project", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            response = deleteProjectWithEmptyUser(projectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
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

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String projectname, String userId ) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"," +
                "\"userId\":\"" + userId + "\"}");
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

    private CloseableHttpResponse getProject(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects//");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjectWithEmptyUser(String projId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + "/projects/" + projId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(String userId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId + "/projects//");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProjectWithEmptyUser(String projId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + "/projects/" + projId);
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

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }


}
