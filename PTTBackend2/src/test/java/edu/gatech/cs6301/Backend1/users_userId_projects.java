package edu.gatech.cs6301.Backend1;

import java.util.Date;

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

public class users_userId_projects extends BaseTestClass {

    // Purpose: Empty user Id should return 400.
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProjects("");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Negative user Id should return 404.
    @Test
    public void pttTest2() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProjects("-1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Zero user Id should return 400.
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProjects("0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: maxLong user Id, should return 400.
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProjects(String.valueOf(Long.MAX_VALUE));
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: UserId presence of non digital character should return 400.
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProjects("1a2b3c");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Empty project name should return 400.
    @Test
    public void pttTest6() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = createProject("123", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Happy path, all input values are correct should return 200.
    @Test
    public void pttTest7() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("john@doe.org", "John", "Doe");
            String userId = getIdFromResponse(response);
            response.close();
            response = createProject(userId, "testProjectName");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getProjects(userId);
            int status = response.getStatusLine().getStatusCode();
            //
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"testProjectName\",\"userId\":" + userId;
            String expectedJson = "[{\"id\":" + projectId + ",\"projectname\":\"testProjectName\"" + "}]";

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            //??
            // Assert.assertEquals(200, status);
            //
            EntityUtils.consume(response.getEntity());
            response.close();
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

    private void deleteAllProjects() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/projects");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }
}