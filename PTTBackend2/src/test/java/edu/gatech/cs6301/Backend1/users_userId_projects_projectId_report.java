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

public class users_userId_projects_projectId_report extends BaseTestClass {
    
    // Purpose: Empty user Id should return 404.
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
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
        try {
            CloseableHttpResponse response = getReport("-100", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: UserId presence of non digital character should return 400.
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("1a2b3c", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Empty projectId should return 404.
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Negative projectId should return 404.
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "-123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: ProjectId presence of non digital character should return 400.
    @Test
    public void pttTest6() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "1a2b3c", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Empty from should return 404.
    @Test
    public void pttTest7() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "123", "" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: from not in ISO-8601 format should return 404.
    @Test
    public void pttTest8() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "123", "invalidTimeFormat" , "2019-02-19T20:00Z", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Empty to should return 404.
    @Test
    public void pttTest9() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "123", "2019-02-18T20:00Z" , "", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: to not in ISO-8601 format should return 404.
    @Test
    public void pttTest10() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "123", "2019-02-18T20:00Z" , "invalidTimeFormat", null, null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: IncludeCompletedPomodoros is empty should success.
    @Test
    public void pttTest11() throws Exception {
        deleteAllUsers();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("john@doe.org", "John", "Doe");
            String userId = getIdFromResponse(response);
            response.close();
            response = createProject(userId, "testProjectName");
            String projectId = getIdFromResponse(response);
            response.close();
            response = addSession(userId, projectId);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: IncludeCompletedPomodoros is invalid valud should return 404.
    @Test
    public void pttTest12() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", "invalid", "true");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: IncludeTotalHoursWorkedOnProject is empty should success.
    @Test
    public void pttTest13() throws Exception {
        deleteAllUsers();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("john@doe.org", "John", "Doe");
            String userId = getIdFromResponse(response);
            response.close();
            response = createProject(userId, "testProjectName");
            String projectId = getIdFromResponse(response);
            response.close();
            response = addSession(userId, projectId);

            response = getReport("123", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", "true", null);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: IncludeTotalHoursWorkedOnProject is invalid valud should return 404.
    @Test
    public void pttTest14() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getReport("123", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", "true", "invalid");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Happy path, all input values are correct should return 200.
    @Test
    public void pttTest15() throws Exception {
        deleteAllUsers();
        httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = createUser("john@doe.org", "John", "Doe");
            String userId = getIdFromResponse(response);
            response.close();
            response = createProject(userId, "testProjectName");
            String projectId = getIdFromResponse(response);
            response.close();
            response = addSession(userId, projectId);
            response = getReport("123", "123", "2019-02-18T20:00Z" , "2019-02-19T20:00Z", "true", "true");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
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
}