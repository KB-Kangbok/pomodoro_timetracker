package edu.gatech.cs6301.Mobile1;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

public class PTTUsersUserIdProjectsProjectIdReport {

    private String baseUrl = "http://localhost:8080";
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

    @Test
    public void pttTest1() throws Exception {   // Purpose: Test if user id is null
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            String nullId = null; // making sure the ID is null

            response = getUserId(nullId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest2() throws Exception {   // Purpose: Test if user id is missing
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            String uid2 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz" + uid1 + uid2; // making sure the ID is not present

            response = getUserId(missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest3() throws Exception {   // Purpose: Test if 'from' is null
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String nullFrom = null; // making sure the ID is null

            String to = "2019-02-18T20:00Z";

            response = getReport(uid1, pid1, nullFrom, to, false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest4() throws Exception {   // Purpose: Test if 'from' is not $ISO-8601 format
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String incorrectFrom = "123"; // making sure the ID is null

            String to = "2019-02-18T20:00Z";

            response = getReport(uid1, pid1, incorrectFrom, to, false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest5() throws Exception {   // Purpose: Test if 'to' is null format
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String nullTo = null;

            response = getReport(uid1, pid1, from, nullTo, false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest6() throws Exception {   // Purpose: Test if 'to' is not $ISO-8601 format
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String incorrectTo = "123";

            response = getReport(uid1, pid1, from, incorrectTo, false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest7() throws Exception {   // Purpose: Test Get operation for report if UserID exists, but ProjectID is null
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String nullId = null;

            response = getReport(uid1, nullId, null, null,false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest8() throws Exception {   // Purpose: Test Get operation for report if UserID exists, but ProjectID is null
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz" + pid1;

            response = getReport(uid1, missingId, null, null, false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest9() throws Exception {
        // Purpose: Test Get operation for report if UserID, ProjectID exist and 'from', 'to' are valid
        // and includeCompletedPomodoros is True and includeTotalHoursWorkedOnProject is True

        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T21:00Z";

            response = getReport(uid1, pid1, from, to, true, true);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest10() throws Exception {
        // Purpose: Test Get operation for report if UserID, ProjectID exist and 'from', 'to' are valid
        // and includeCompletedPomodoros is True and includeTotalHoursWorkedOnProject is False

        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

            response = getReport(uid1, pid1, from, to, true, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest11() throws Exception {
        // Purpose: Test Get operation for report if UserID, ProjectID exist and 'from', 'to' are valid
        // and includeCompletedPomodoros is False and includeTotalHoursWorkedOnProject is True

        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

            response = getReport(uid1, pid1, from, to, false, true);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void pttTest12() throws Exception {
        // Purpose: Test Get operation for report if UserID, ProjectID exist and 'from', 'to' are valid
        // and includeCompletedPomodoros is False and includeTotalHoursWorkedOnProject is False

        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String uid1 = getIdFromResponse(response);
            response.close();

            response = createProject(uid1, "project1");
            String pid1 = getIdFromResponse(response);
            response.close();

            String from = "2019-02-18T20:00Z";

            String to = "2019-02-18T20:00Z";

            response = getReport(uid1, pid1, from, to, false, false);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }



    // ************************************************************
    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String uid, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + uid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getUserId(String uid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + uid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllUserId() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjectId(String uid, String pid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + uid + "/projects/" + pid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllProjectId(String uid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + uid + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

//    private CloseableHttpResponse getReport(String uid, String pid, String from, String to, boolean includeCompletedPomodoros, boolean includeTotalHoursWorkedOnProject) throws IOException {
//        String url = baseUrl + "/users/" + uid + "/projects/" + pid + "/report";
//        HttpGet httpRequest = new HttpGet(url);
//        httpRequest.addHeader("accept", "application/json");
//        /*
//        StringEntity input = new StringEntity("{\"userId\":\"" + uid + "\"," +
//                "\"projectId\":\"" + pid + "\"," +
//                "\"from\":\"" + from + "\"," +
//                "\"to\":\"" + to + "\"," +
//                "\"includeCompletedPomodoros\":\"" + includeCompletedPomodoros + "\"," +
//                "\"includeTotalHoursWorkedOnProject\":\"" + includeTotalHoursWorkedOnProject + "\"}");
//        input.setContentType("application/json");
//        httpRequest.setEntity(input);
//        */
//        String a = "";
//        String b = "";
//        if (includeCompletedPomodoros == true) { a = "true"; }
//        else a = "false";
//        if (includeTotalHoursWorkedOnProject == true) { b = "true"; }
//        else b = "false";
//
//        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
//        params.add(new BasicNameValuePair("projectId", pid));
//        params.add(new BasicNameValuePair("from", from));
//        params.add(new BasicNameValuePair("to", to));
//        params.add(new BasicNameValuePair("includeCompletedPomodoros", a));
//        params.add(new BasicNameValuePair("includeTotalHoursWorkedOnProject", b));
//        HttpGet httpget = new HttpGet(url + "?" + URLEncodedUtils.format(params, "utf-8"));
//
//        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
//        CloseableHttpResponse response = httpclient.execute(httpRequest);
//        System.out.println("*** Raw response " + response + "***");
//        return response;
//    }

    private CloseableHttpResponse getReport(String userId, String projectId, String from, String to, boolean includeCompletedPomodoros, boolean includeTotalHoursWorkedOnProject) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report?from=" + from + "&to=" + to + "&includeCompletedPomodoros=" + includeCompletedPomodoros +"&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getReport(Integer userId, Integer projectId, String from, String to, boolean includeCompletedPomodoros, boolean includeTotalHoursWorkedOnProject) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report?from=" + from + "&to=" + to + "&includeCompletedPomodoros=" + includeCompletedPomodoros +"&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject);
        httpRequest.addHeader("accept", "application/json");

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

        return response;
    }

    private CloseableHttpResponse deleteProjects(String uid) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + uid + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");

        return response;
    }

    private CloseableHttpResponse deleteProject(String uid, String pid) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + uid + "/projects/" + pid);
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


}
