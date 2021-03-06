package edu.gatech.cs6301.Web2;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6301.entity.Project;
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

public class Users_userId_projects {

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
    

    // Purpose: call GET method when projects is empty
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = getProjects(userId);
            
            String expectedJson = "[]";

            HttpEntity entity;
            String strResponse;
            int status = response.getStatusLine().getStatusCode();
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

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }
    // Purpose: call GET method when projects is not empty
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project");
            String projectId = getIdFromResponse(response);

            response = getProjects(userId);

            String expectedJson = "[{\"id\":" + projectId + ",\"projectname\":\"project\"}]";

            HttpEntity entity;
            String strResponse;
            int status = response.getStatusLine().getStatusCode();
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

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with wrong userId
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
    
            String missingId = "123123" + userId;

            response = getProjects(missingId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }
    // Purpose: call GET method with empty userId
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
    

            response = getProjects("");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }
    // Purpose: call POST method when there is no projects and with empty project name
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
    
            response = createProject(userId, "");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }

    // Purpose: call POST method when there is project and with empty project name
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
    
            createProject(userId, "project");
            
            response = createProject(userId, "");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }
    // Purpose: call POST method when there is project and with same project name
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            createProject(userId, "project");
            
            response = createProject(userId, "project");

            int status = response.getStatusLine().getStatusCode();
            // return 409 in case of duplicate entity
            Assert.assertEquals(409, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }
    // Purpose: call POST method when there is project and with different project name
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response  = createProject(userId, "project1");
            response = createProject(userId, "project2");

            HttpEntity entity;
            String strResponse;

            int status = response.getStatusLine().getStatusCode();
            if (status != 201) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            entity = response.getEntity();
            strResponse = EntityUtils.toString(entity);
            Project project;
            ObjectMapper mapper = new ObjectMapper();
            project = mapper.readValue(strResponse, new TypeReference<Project>(){});

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = " {\"id\":" + String.valueOf(project.getId()) + ",\"projectname\":\"project2\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }
    // Purpose: call POST method with wrong userId
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            
            String missingId = "123123" + userId;

            response = createProject(missingId, "project");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }
    // Purpose: call POST method with empty userId
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            

            response = createProject("", "project");
            int status = response.getStatusLine().getStatusCode();

            //returns a 500
            Assert.assertEquals(405, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);

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

    private CloseableHttpResponse getProjects(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
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

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}
