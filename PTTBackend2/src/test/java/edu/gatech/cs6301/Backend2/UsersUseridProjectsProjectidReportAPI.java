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

public class UsersUseridProjectsProjectidReportAPI {

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

//    @Test
//    // Purpose: Url of bad_request
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
    // Purpose: userid contains special character
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport(userId+"***", projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test invalid user id not found
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport("-1", projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: empty userid
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport("", projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: projectid contains special character
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport("1", "@@@", "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test invalid project id not found - return 404
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport(userId, "-1"+projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "");

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
    // Purpose: empty project id
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport(userId, "", "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            //return all projects
            if (status != 400) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = Empty, includeTotalHoursWorkedOnProject = Empty
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);

            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";

            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "false", "false");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}]}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = Empty, includeTotalHoursWorkedOnProject = true
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);

            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";

            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "true");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}],\"totalHoursWorkedOnProject\":1.0}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = Empty, includeTotalHoursWorkedOnProject = false
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);

            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";

            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "", "false");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}]}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = True, includeTotalHoursWorkedOnProject = Empty
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);

            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";
            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "true", "");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}],\"completedPomodoros\":1}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = true, includeTotalHoursWorkedOnProject = true
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);

            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";
            response = createSession(userId, projectId, startTime, endTime, 1);
            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "true", "true");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}],\"completedPomodoros\":1,\"totalHoursWorkedOnProject\":1.0}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = true, includeTotalHoursWorkedOnProject = false
    public void pttTest13() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";
            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "true", "false");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}],\"completedPomodoros\":1}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = fasle, includeTotalHoursWorkedOnProject = Empty
    public void pttTest14() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";
            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "false", "");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}]}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = false, includeTotalHoursWorkedOnProject = true
    public void pttTest15() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";
            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "false", "true");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}],\"totalHoursWorkedOnProject\":1.0}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: test get /users/{userId}/projects/{projectId}/report when includeCompletedPomodors = false, includeTotalHoursWorkedOnProject = false
    public void pttTest16() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        deleteProjects();

        try {
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String startTime = "2021-02-18T20:00Z";
            String endTime = "2021-02-18T21:00Z";
            response = createSession(userId, projectId, startTime, endTime, 1);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2021-02-18T20:00Z","2023-02-18T20:00Z", "false", "false");

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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2021-02-18T20:00Z\",\"endingTime\":\"2021-02-18T21:00Z\",\"hoursWorked\":1.0}]}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse getUsersUseridProjectsProjectidReport(String userid, String projectid, String from, String to, String includeCompletedPomodoros, String includeTotalHoursWorkedOnProject) throws IOException {
        String url = baseUrl + "/users/" + userid + "/projects/" + projectid + "/report?from=" + from + "&to=" + to;
        if (includeCompletedPomodoros != null && includeCompletedPomodoros.length() > 0) {
            url += "&includeCompletedPomodoros=" + includeCompletedPomodoros;
        }
        if (includeTotalHoursWorkedOnProject != null && includeTotalHoursWorkedOnProject.length() > 0) {
            url += "&includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject;
        }
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
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
        HttpPost httpRequest = new HttpPost(baseUrl + "/users" + userid + "/projects");
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

//    private CloseableHttpResponse postUsersUseridProjects(String id, String projectname, String userid) throws IOException {
//        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects");
//        httpRequest.addHeader("accept", "application/json");
//        StringEntity input = new StringEntity(
//                "{\"projectname\":\"" + projectname + "\"}"
//                );
//        input.setContentType("application/json");
//        httpRequest.setEntity(input);
//
//        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
//        CloseableHttpResponse response = httpclient.execute(httpRequest);
//        System.out.println("*** Raw response " + response + "***");
//        return response;
//    }

    private CloseableHttpResponse postUsersUseridProjects(String id, String projectName, String userId) throws IOException{
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"projectname\":\"" + projectName + "\"," +
                        "\"userId\":\"" + userId + "\"}"
        );
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

    private CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime, int counter) throws IOException{
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"startTime\":\"" + startTime + "\"," +
                        "\"endTime\":\"" + endTime + "\"," +
                        "\"counter\":" + counter + "}"
        );
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
}
