package edu.gatech.cs6301.Mobile2;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Users_userId_projects_projectId_report extends PTTBackendTestBase {

    @Test
    // Purpose: When userId doesn't exist, backend should return error.
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse response = getReport("0", "0", "1", "2", true, true);
            int status = response.getStatusLine().getStatusCode();
            //400 due to start and end time not being valid
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When projectId doesn't exist, backend should return 404
    public void pttTest2() throws Exception {
        try {
            deleteUsers();
            deleteProjects();
            String userId = setupUser();

            CloseableHttpResponse response = getReport(userId, "0", "1", "2", true, true);
            int status = response.getStatusLine().getStatusCode();
            //400 due to start and end time not being valid
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When endingTime smaller than startTime, backend should return 400
    public void pttTest3() throws Exception {
        try {
            deleteUsers();
            deleteProjects();
            String userId = setupUser();
            String projectId = setupUserProject(userId);

            String from = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
            String to = ZonedDateTime.now().minusMinutes(30).format(DateTimeFormatter.ISO_INSTANT);

            CloseableHttpResponse response = getReport(userId, projectId, from, to, true, true);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 400);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When endingTime same as startTime, request should succeed
    public void pttTest4() throws Exception {
        try {
//            deleteProjects();
//            deleteUsers();
//            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
//            String userId = getIdFromResponse(response);
//            response = postUsersUseridProjects(userId, "test", userId);
//            String projectId = getIdFromResponse(response);
//
//            String from = "2021-10-13T10:00Z";
//            String to = "2021-10-14T10:00Z";
//            response = addSession(userId, projectId, from, to, 1);
//            response.close();
//
//            String expectedJson = "{\"sessions\":[\"startingTime\":\"" + from + "\"," + "\"endingTime\":\"" + to + "\"," + "\"hoursWorked\":\"" + 0.50 + "\"]}";
//
//            response = getReport(userId, projectId, from, from, false, false);
////            int status = response1.getStatusLine().getStatusCode();
////            Assert.assertEquals(status, 200);
//
//            HttpEntity entity;
//            String strResponse;
//
//            entity = response.getEntity();
//            strResponse = EntityUtils.toString(entity);
////            System.out.println("*** String response " + strResponse + " (" + status + ") ***");
//
//            // Check that the record is correct in the response
//            JSONAssert.assertEquals(expectedJson, strResponse, false);
//            EntityUtils.consume(entity);
//            response.close();


            deleteUsers();
            deleteProjects();
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response = getUsersUseridProjectsProjectidReport(userId, projectId, "2019-02-18T20:00Z","2019-02-18T20:00Z", "", "true");

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

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When set includeCompletedPomodoros=true
    public void pttTest5() throws Exception {
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            response.close();

            String from1 = "2021-02-18T20:00Z";
            String to1 = "2021-02-18T20:30Z";
            response = createSession(userId, projectId, from1, to1, 0);
            response.close();

            String from2 = "2021-02-19T20:00Z";
            String to2 = "2021-02-19T20:30Z";
            response = createSession(userId, projectId, from2, to2, 0);
            response.close();

            response = getUsersUseridProjectsProjectidReport(userId, projectId, from1, to2, "true", "false");

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + from1 + "\"," + "\"endingTime\":\"" + to1 + "\"," + "\"hoursWorked\":" + 0.50 + "}," +
                    "{\"startingTime\":\"" + from2 + "\"," + "\"endingTime\":\"" + to2 + "\"," + "\"hoursWorked\":" + 0.5 + "}]," +
                    "\"completedPomodoros\":" + "0}";

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            Assert.assertEquals(200, status);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // Check that the record is correct in the response
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(entity);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When set includeCompletedPomodoros=false
    public void pttTest6() throws Exception {
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String from = "2021-02-18T20:00Z";
            String to = "2021-02-18T20:30Z";
            response = createSession(userId, projectId, from, to, 0);
            response = getUsersUseridProjectsProjectidReport(userId, projectId, from, to, "false", "false");

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + from + "\"," + "\"endingTime\":\"" + to + "\"," + "\"hoursWorked\":" + 0.50 + "}]}";
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // Check that the record is correct in the response
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When set includeTotalHoursWorkedOnProject=true
    public void pttTest7() throws Exception {
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String from = "2021-02-18T20:00Z";
            String to = "2021-02-18T20:30Z";
            response = createSession(userId, projectId, from, to, 0);
            response = getUsersUseridProjectsProjectidReport(userId, projectId, from, to, "false", "true");

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + from + "\"," + "\"endingTime\":\"" + to + "\"," + "\"hoursWorked\":" + 0.50
                    + "}],\"totalHoursWorkedOnProject\":" + "0.50}";

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // Check that the record is correct in the response
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When set includeTotalHoursWorkedOnProject=false
    public void pttTest8() throws Exception {
        try {
            deleteProjects();
            deleteUsers();
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);
            String from1 = "2021-02-18T20:00Z";
            String to1 = "2021-02-18T20:30Z";
            response = createSession(userId, projectId, from1, to1, 1);

            String from2 = "2021-02-19T20:00Z";
            String to2 = "2021-02-19T21:00Z";
            response = createSession(userId, projectId, from2, to2, 2);

            response = getUsersUseridProjectsProjectidReport(userId, projectId, from1, to2, "true", "false");

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + from1 + "\"," + "\"endingTime\":\"" + to1 + "\"," + "\"hoursWorked\":" + 0.50 + "}," +
                    "{\"startingTime\":\"" + from2 + "\"," + "\"endingTime\":\"" + to2 + "\"," + "\"hoursWorked\":" + 1.00 + "}]," +
                    "\"completedPomodoros\":" + "3}";

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // Check that the record is correct in the response
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private String setupUser() throws IOException, JSONException {
        CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
        String userId = getIdFromResponse(response);
        response.close();
        return userId;
    }

    private String setupUserProject(String userId) throws JSONException, IOException {
        CloseableHttpResponse response = createProject(userId, "JohnProject");
        String projectId = getIdFromResponse(response);
        response.close();
        return projectId;
    }

    private void deleteProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
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
