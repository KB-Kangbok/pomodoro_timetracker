package edu.gatech.cs6301.Mobile3;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
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
            HttpHost localhost = new HttpHost("locahost", 8080);
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

    // Purpose: GET /users/{userId}/projects/{projectId}/report with valid information
    @Test
    public void getReportTestSuccess() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse userResponse = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(userResponse);
            userResponse.close();

            CloseableHttpResponse projectResponse = createProject(userId, "TestProject");
            String projectId = getIdFromResponse(projectResponse);
            projectResponse.close();

            CloseableHttpResponse sessionResponse = addSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "2");
            sessionResponse.close();

            CloseableHttpResponse reportResponse = getReport(userId, projectId, "2019-02-18T19:00Z", "2019-02-18T23:00Z");

            int status = reportResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = reportResponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + reportResponse.getStatusLine().getStatusCode() + ") ***");

            EntityUtils.consume(reportResponse.getEntity());
            reportResponse.close();
            deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose GET /users/{userId}/projects/{projectId}/report for user with no projects
    @Test
    public void getReportTestNoProjects() throws Exception {
        int status = 404;
        try {
            CloseableHttpResponse userResponse = createUser("john@doe.com", "John", "Doe");
            String userId = getIdFromResponse(userResponse);
            userResponse.close();

            CloseableHttpResponse reportResponse = getReport(userId, "0", "2019-02-18T19:00Z", "2019-02-18T23:00Z");

            status = reportResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = reportResponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + reportResponse.getStatusLine().getStatusCode() + ") ***");
        } catch (Exception e) {
            Assert.assertEquals(404, status);
           // deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose GET /users/{userId}/projects/{projectId}/report for not existing user
    @Test
    public void getReportTestNoUser() throws Exception {
        int status = 404;
        try {
            CloseableHttpResponse reportResponse = getReport("0", "0", "2019-02-18T19:00Z", "2019-02-18T23:00Z");

            status = reportResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = reportResponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + reportResponse.getStatusLine().getStatusCode() + ") ***");
        } catch (Exception e) {
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    // Purpose GET /users/{userId}/projects/{projectId}/report bad input
    @Test
    public void getReportTestBadInput() throws Exception {
        int status = 400;
        try {
            CloseableHttpResponse reportResponse = getReport("NULL", "NULL","2019-02-18T19:00Z", "2019-02-18T23:00Z");

            status = reportResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = reportResponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + reportResponse.getStatusLine().getStatusCode() + ") ***");
        } catch (Exception e) {
            Assert.assertEquals(404, status);
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


    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse addSession(String userId, String projectId, String startTime, String endTime, String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
        "\"endTime\":\"" + endTime + "\"," +
        "\"counter\":\"" + counter + "\"}");
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

    private CloseableHttpResponse getReport(String uid, String pid, String from, String to) throws IOException {
        boolean includeCompletedPomodoros = false;
        boolean includeTotalHoursWorkedOnProject = false;
        String url = baseUrl + "/api/users/" + uid + "/projects/" + pid + "/report";
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");
        /*
        StringEntity input = new StringEntity("{\"userId\":\"" + uid + "\"," +
                "\"projectId\":\"" + pid + "\"," +
                "\"from\":\"" + from + "\"," +
                "\"to\":\"" + to + "\"," +
                "\"includeCompletedPomodoros\":\"" + includeCompletedPomodoros + "\"," +
                "\"includeTotalHoursWorkedOnProject\":\"" + includeTotalHoursWorkedOnProject + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);
        */
        String a = "";
        String b = "";
        if (includeCompletedPomodoros == true) { a = "true"; }
        else a = "false";
        if (includeTotalHoursWorkedOnProject == true) { b = "true"; }
        else b = "false";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("projectId", pid));
        params.add(new BasicNameValuePair("from", from));
        params.add(new BasicNameValuePair("to", to));
        params.add(new BasicNameValuePair("includeCompletedPomodoros", a));
        params.add(new BasicNameValuePair("includeTotalHoursWorkedOnProject", b));
        HttpGet httpget = new HttpGet(url + "?" + URLEncodedUtils.format(params, "utf-8"));

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
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
    private void deleteAllUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

}
    
