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

public class Users_UserId_Projects_ProjectId_Sessions extends PTTBackendTestBase {

    @Test
    // Purpose: When userId doesn't exist, backend should return error
    
    public void pttTest1() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            CloseableHttpResponse response = addSession("-1", "0", "12:00:00", "01:00:00", 1);

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
            CloseableHttpResponse response = addSession("0", "-1", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT), ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT), 1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When startTime doesn't exist, backend should return error
    
    public void pttTest3() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            CloseableHttpResponse response = addSession("0", "0", "emptyStartTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT), 1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When endTime doesn't exist, backend should return error
    
    public void pttTest4() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            CloseableHttpResponse response = addSession("0", "0", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT), "emptyEndTime", 1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When counter doesn't exist, backend should return error
    
    public void pttTest5() throws Exception {
        try {
            deleteAllUsers();
            deleteProjects();
            CloseableHttpResponse response = addSession("0", "0", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT), ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT), -1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When all paramters exist, backend should return success
    
    public void pttTest6() throws Exception {
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

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);
        } finally {
            httpclient.close();
        }
    }
    @Test
    // Purpose: When userId and projectId exist, backend should return all sessions
    
    public void pttTest7() throws Exception {
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

            response = getSessions(userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
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
}