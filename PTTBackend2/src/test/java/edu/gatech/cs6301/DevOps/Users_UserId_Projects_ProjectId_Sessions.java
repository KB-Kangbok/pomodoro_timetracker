package edu.gatech.cs6301.DevOps;
import java.io.IOException;
import java.util.Iterator;

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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.util.ArrayList;
import java.util.List;



import org.skyscreamer.jsonassert.JSONAssert;

public class Users_UserId_Projects_ProjectId_Sessions {
    private String baseUrl = "http://localhost:8080";
    // private String baseUrl = "http://localhost:8080";
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


    // Purpose: test GET functionality for /users/user_id/projects/projects_id/sessions endpoint
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();
        String id = null;
        String expectedJson = "";

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john.doe@gmail.com");
            String user_id = getIdFromResponse(response);
            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);
            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T21:00Z", "0");
            id = getIdFromResponse(response);
            expectedJson += "[{\"id\":" + id + "," + "\"startTime\":\"2019-02-18T20:00Z\"," + "\"endTime\":\"2019-02-18T21:00Z\"," + "\"counter\": 0 },";

            int status;
            HttpEntity entity;
            String strResponse;

            response = createSession(user_id, project_id, "2019-02-19T20:00Z", "2019-02-19T21:00Z", "1");
            id = getIdFromResponse(response);
            expectedJson += "{\"id\":" + id + "," + "\"startTime\":\"2019-02-19T20:00Z\"," + "\"endTime\":\"2019-02-19T21:00Z\"," + "\"counter\": 1 }]";
            response.close();

            response = getSessions(user_id, project_id);

            status = response.getStatusLine().getStatusCode();
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


    // Purpose: Tests if /users/user_id/projects/project_id/sessions endpoint fails for any request besides POST and GET
    @Test
    public void pttTest2() throws Exception {
	    deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");

            response = putSessions(user_id, project_id);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);

            response.close();

            response = deleteSessions(user_id, project_id);
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);

            response.close();

        } finally {
            httpclient.close();
        }
    }    

    // Purpose: Tests when the project_id is not valid
    // we don't need to check if user_id is valid, since that is already checked, we can't create project if the user_id is invalid
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createSession("0", "0", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createSession(user_id, "0", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            response = createProject("Project", user_id);
            response = createSession(user_id, "-1", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            response.close();

            response = createSession(user_id, "40", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            response.close();

            response = createSession(user_id, "1324412", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }    

    // Purpose: Tests when input start date is not valid String for POST method for endpoint /users/user_id/project/project_id/sessions
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "0", "2019-02-18T20:00Z", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "November 2012", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }      

    // Purpose: Tests when input end date is not valid String for POST method for endpoint /users/user_id/project/project_id/sessions
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "0", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "November 2012", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "201-02-18T20:00Z", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }     

    // Purpose: Tests when input counter is not an Integer value for POST method for endpoint /users/user_id/project/project_id/sessions
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0.12");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "hello");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "[0, 1,2, 3]");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }  

    // Purpose: Tests when input counter is below 0 for POST method for endpoint /users/user_id/project/project_id/sessions
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "-1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "-999999999999999999");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    } 

    @Test
    // Purpose: Tests if POST method works for endpoint /users/user_id/projects/project_id/sessions
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response = createProject("Project", user_id);
            String project_id = getIdFromResponse(response);

            response = createSession(user_id, project_id, "2019-02-18T20:00Z", "2019-02-18T21:00Z", "0");
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

            String expectedJson = "{\"id\":" + id + "," + "\"startTime\":\"2019-02-18T20:00Z\"," + "\"endTime\":\"2019-02-18T21:00Z\"," + "\"counter\":0}";
    	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    private CloseableHttpResponse createUser(String firstname, String familyname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"lastName\":\"" + familyname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUsers() throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse deleteProjects(String user_id, String project_id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse createProject(String projectname, String id) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + id + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjects(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putProjects(String user_id, String project_id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private CloseableHttpResponse getUsers() throws IOException {
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

    private CloseableHttpResponse createSession(String user_id, String project_id, String start, String end, String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + start + "\"," +
                            "\"endTime\":\"" + end + "\"," +
                            "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSessions(String user_id, String project_id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessions(String user_id, String project_id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteSessions(String user_id, String project_id) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
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