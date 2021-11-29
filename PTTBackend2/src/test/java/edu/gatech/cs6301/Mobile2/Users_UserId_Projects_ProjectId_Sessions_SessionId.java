package edu.gatech.cs6301.Mobile2;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Users_UserId_Projects_ProjectId_Sessions_SessionId extends PTTBackendTestBase {

    @Test
    // Purpose: When userId doesn't exist, backend should return error
    
    public void pttTest1() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";

            CloseableHttpResponse response = updateSession("-1", "0", "0", startTime, endTime, 1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When projectId doesn't exist, backend should return error
    
    public void pttTest2() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";

            CloseableHttpResponse response = updateSession("0", "-1", "0", startTime, endTime, 1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When sessionId doesn't exist, backend should return error
    
    public void pttTest3() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";

            CloseableHttpResponse response = updateSession("0", "0", "-1", startTime, endTime, 1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When all paramters exist, backend should return success
    
    public void pttTest4() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            CloseableHttpResponse response = postUsers("abc@def.com", "Way", "1", "Toshiba");
            String userId = getIdFromResponse(response);
            response = postUsersUseridProjects(userId, "test", userId);
            String projectId = getIdFromResponse(response);

            String startTime = "2021-10-13T10:00Z";
            String endTime = "2021-10-14T10:00Z";
            response = addSession(userId, projectId, startTime, endTime, 1);
            String sessionId = getIdFromResponse(response);
            response.close();

            String updatedStartTime = "2019-02-19T18:00Z";
            String updatedEndTime = "2019-02-19T20:30Z";
            response = updateSession(userId, projectId, sessionId, updatedStartTime, updatedEndTime, 1);

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(201, status);
        } finally {
            httpclient.close();
        }
    }

    private void deleteAllUsers() throws Exception {
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

}