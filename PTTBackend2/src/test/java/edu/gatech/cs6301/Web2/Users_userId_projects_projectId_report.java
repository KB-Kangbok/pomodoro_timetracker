package edu.gatech.cs6301.Web2;

import java.io.IOException;
import java.util.Iterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
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

public class Users_userId_projects_projectId_report {

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
    

    // Purpose: call GET method with wrong userId
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + userId;

            response = getReport(missingId, projectId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", false, false);
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
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport("", projectId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with wrong projectId
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            String missingId = "123123" + projectId;

            response = getReport(userId, missingId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with empty projectId
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            createProject(userId, "project1");

            response = getReport(userId, "", "2021-10-12T20:00Z", "2021-10-13T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with empty from query
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId, "", "2021-10-13T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with non-date form of from query
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId, "1234", "2021-10-13T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with empty to query
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId, "2021-10-12T20:00Z", "", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with non-date form to query
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId, "2021-10-12T20:00Z", "1234", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with to query smaller than from query
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getReport(userId, projectId, "2021-10-13T20:00Z", "2021-10-12T20:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with pomodoro and total hour included
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();

        try {

            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            createSession(userId, projectId, "2021-10-13T20:00Z", "2", "2021-10-13T19:00Z");

            response = getReport(userId, projectId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", true, true);
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + "2021-10-13T19:00Z" + "\"," + "\"endingTime\":\"" + "2021-10-13T20:00Z" + "\"," + "\"hoursWorked\":" + 1.0 + "}]," +
                    "\"completedPomodoros\":" + "2}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with pomodoro included
    @Test
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            createSession(userId, projectId, "2021-10-13T20:00Z", "2", "2021-10-13T19:00Z");

            response = getReport(userId, projectId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", true, false);
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + "2021-10-13T19:00Z" + "\"," + "\"endingTime\":\"" + "2021-10-13T20:00Z" + "\"," + "\"hoursWorked\":" + 1.0 + "}]," +
                    "\"completedPomodoros\":" + "2}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method with total hour included
    @Test
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            createSession(userId, projectId, "2021-10-13T20:00Z", "2", "2021-10-13T19:00Z");

            response = getReport(userId, projectId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", false, true);
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + "2021-10-13T19:00Z" + "\"," + "\"endingTime\":\"" + "2021-10-13T20:00Z" + "\"," + "\"hoursWorked\":" + 1.0 + "}]," +
                    "\"totalHoursWorkedOnProject\":" + "1.0}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: call GET method without pomodoro and total hour included
    @Test
    public void pttTest13() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "project1");
            String projectId = getIdFromResponse(response);
            response.close();

            createSession(userId, projectId, "2021-10-13T20:00Z", "2", "2021-10-13T19:00Z");

            response = getReport(userId, projectId, "2021-10-12T20:00Z", "2021-10-13T20:00Z", true, true);
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + "2021-10-13T19:00Z" + "\"," + "\"endingTime\":\"" + "2021-10-13T20:00Z" + "\"," + "\"hoursWorked\":" + 1.0 + "}]}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
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

    private CloseableHttpResponse createSession(String userId, String projectId, String endTime, String counter, String startTime) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"counter\":\"" + counter + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"startTime\":\"" + startTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getReport(String userId, String projectId, String from, String to, boolean includePom, boolean includeTotal) throws IOException, URISyntaxException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report");
        
        URIBuilder uribuilder = new URIBuilder(httpRequest.getURI())
        .addParameter("from", from)
        .addParameter("to", to);
        if(includePom)
        uribuilder.addParameter("includeCompletedPomodoros", "true");
        
        if(includeTotal)
        uribuilder.addParameter("includeTotalHoursWorkedOnProject", "true");
        
        
        URI uri = uribuilder.build();
        ((HttpRequestBase) httpRequest).setURI(uri);
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
