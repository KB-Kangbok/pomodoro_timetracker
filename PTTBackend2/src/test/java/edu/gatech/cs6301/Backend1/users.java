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

public class users extends BaseTestClass {
    //Firstname empty should return error 400
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = createUser("", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Lastname empty should return error 201
    @Test
    public void pttTest2() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {
            CloseableHttpResponse response = createUser("John", "", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    //Email empty should  should return 201.
    @Test
    public void pttTest3() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {

            CloseableHttpResponse response = createUser("John", "D", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(201, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Happy case all inputs are correct and must return 200
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        deleteAllProjects();
        try {

            CloseableHttpResponse response = createUser("John", "Doe", "johndoe@org");
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