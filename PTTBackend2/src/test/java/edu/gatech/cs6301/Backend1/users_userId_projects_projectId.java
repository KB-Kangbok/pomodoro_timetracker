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

public class users_userId_projects_projectId extends BaseTestClass {
    
    // Purpose: Empty user Id should return 404, for both post and delete method.
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("", "123");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("", "123");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            
        } finally {
            httpclient.close();
        }
    }

     // Purpose: Negative user Id should return 400, for both post and delete method.
     @Test
     public void pttTest2() throws Exception {
         deleteAllUsers();
         deleteAllProjects();
         try {
            CloseableHttpResponse response = getProject("-1", "123");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("-1", "123");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
         } finally {
             httpclient.close();
         }
     }

    // Purpose: Zero user Id should return 404, for both post and delete method.
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("0", "123");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("0", "123");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: maxLong user Id, should return 400, not 404; Tests for both post and delete method.
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject(String.valueOf(Long.MAX_VALUE), "123");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject(String.valueOf(Long.MAX_VALUE), "123");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: UserId presence of non digital character should return 400, for both post and, 400 for delete method.
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("1a2b3c", "123");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("1a2b3c", "123");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Empty projectId should return 400, for both post and delete method.
    @Test
    public void pttTest6() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("123", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("123", "");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Negative projectId should return 400, for both post and delete method.
    @Test
    public void pttTest7() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("123", "-1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("123", "-1");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Zero project Id should return 404, for both post and delete method.
    @Test
    public void pttTest8() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("123", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("123", "0");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: maxLong project Id, should return 400, not 404, for both post and delete method.
    @Test
    public void pttTest9() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("123", String.valueOf(Long.MAX_VALUE));
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("123", String.valueOf(Long.MAX_VALUE));
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: ProjectId presence of non digital character should return 400, for both post and delete method.
    @Test
    public void pttTest10() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getProject("123", "1a2b3c");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = deleteProject("123", "1a2b3c");
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Happy path, all input values are correct should return 200, for both post and delete method.
    @Test
    public void pttTest11() throws Exception {
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
            
            response = getProject(userId, projectId);
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

            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"testProjectName\"}";
	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            // for successful delete
            response = deleteProject(userId, projectId);
            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
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