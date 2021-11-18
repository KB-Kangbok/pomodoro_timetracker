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

public class users_userid extends BaseTestClass {
    //ID is empty and must return 200
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getUser("");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //ID has negative value and must return error 404
    @Test
    public void pttTest2() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = getUser("-12");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //maxInt + 1 projectId should return 400.
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {

            CloseableHttpResponse response = getUser("2147483648");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    //Special characters in projectId should return 400.
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {

            CloseableHttpResponse response = getUser("21ab$");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Happycase input values are correct and should return 200
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {

            CloseableHttpResponse response = createUser("john@doe.org", "John", "Doe");
            String userId = getIdFromResponse(response);
            response.close();

            response = getUser(userId);
            int status = response.getStatusLine().getStatusCode();
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