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

public class users_userId_projects_projectId_sessions_sessionId extends BaseTestClass{
    
    
    // Purpose: Empty userId should return 400, for put method.
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("", "123", "321", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Negative userId should return 400, for put method.
    @Test
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
            // CloseableHttpResponse response = createSession("-1", "123", "2021-10-10T20:00Z", "2021-10-10T21:00Z", 2);
            // String sessionId = getIdFromResponse(response);
            // response.close();

            CloseableHttpResponse response = updateSession("-1", "123", "321", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: UserId presence of non digital character should return 400, for put method.
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("1s04b3", "123", "321", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: Empty porjectId should return 400, for put method.
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "", "321", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Negative porjectId should return 400, for put method.
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "-1", "321", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: porjectId presence of non digital character should return 400, for put method.
    @Test
    public void pttTest6() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "1s04b3","321", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

     // Purpose: Empty sessionId should return 400, for put method.
     @Test
     public void pttTest7() throws Exception {
         deleteAllUsers();
         try {
             CloseableHttpResponse response = updateSession("123", "123", "", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
             int status = response.getStatusLine().getStatusCode();
             Assert.assertEquals(405, status);
             EntityUtils.consume(response.getEntity());
             response.close();
         } finally {
             httpclient.close();
         }
     }
 
     // Purpose: Negative sessionId should return 400, for put method.
     @Test
     public void pttTest8() throws Exception {
         deleteAllUsers();
         try {
             CloseableHttpResponse response = updateSession("123", "123", "-1", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
             int status = response.getStatusLine().getStatusCode();
             Assert.assertEquals(404, status);
             EntityUtils.consume(response.getEntity());
             response.close();
         } finally {
             httpclient.close();
         }
     }
 
 
     // Purpose: sessionId presence of non digital character should return 400, for put method.
     @Test
     public void pttTest9() throws Exception {
         deleteAllUsers();
         try {
             CloseableHttpResponse response = updateSession("123", "321","1s04b3", 2, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
             int status = response.getStatusLine().getStatusCode();
             Assert.assertEquals(400, status);
             EntityUtils.consume(response.getEntity());
             response.close();
         } finally {
             httpclient.close();
         }
     }
    // Purpose: StartTime is empty  return 400, for put method
    @Test
    public void pttTest10() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "321","133", 2, "", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: StartTime is not empty and not in ISO-8601 format return 400, for put method
    @Test
    public void pttTest11() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "321","133", 2, "invalidTimeFormat", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    // Purpose: endTime is  empty  return 400, for put method
    @Test
    public void pttTest12() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "321","133", 2, "2021-10-10T21:00Z", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: endTime is not empty and not in ISO-8601 format return 400, for put method
    @Test
    public void pttTest13() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "321","133", 2, "2021-10-10T21:00Z", "invalidTimeFormat");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: counter is <0 return 400, for put method
    @Test
    public void pttTest14() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("123", "123", "133", -1, "2021-10-10T20:00Z", "2021-10-10T21:00Z");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: Happy path, all input are correct and return 201, for put method
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
            response = createSession(userId, projectId, "2021-10-10T20:00Z", "2021-10-10T21:00Z", 2);
            String sessionId =getIdFromResponse(response);
            response.close();
            


            response = updateSession(userId, projectId, sessionId, 1, "2021-10-10T21:00Z","2021-10-10T21:30Z");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
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

    private void deleteAllUsers() throws Exception {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        response.close();
    }

}
