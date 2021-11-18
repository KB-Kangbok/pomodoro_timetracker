package edu.gatech.cs6301.Mobile2;

import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.Iterator;

public class Users extends PTTBackendTestBase {

    @Test
    // If a post request is made with a user ID then this should return an error as the user ID should be left empty for clarity
    public void pttTest1() throws Exception {
        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String id = "0";
            String firstname = "John";
            String lastname = "Doe";
            String email = "john@doe.org";
            StringEntity input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstname\":\"" + firstname + "\"," +
                "\"lastname\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
       
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            //this is expected behavior
            Assert.assertEquals(status, 201);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a postrequest is made with an email that is already taken by a user then this should return a resource conflict error
    public void pttTest2() throws Exception {

        try {
            createUser("John", "Doe", "john@doe.org");
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 409);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

   
    @Test
    // Purpose: When the correct GET request is made the response should be the current users in the API
    public void pttTest3() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            String expectedJson =  "[{\"id\":" + id + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}]";
            response.close();
            response = getUsers();

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            HttpEntity entity;
            String strResponse = "";
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                JSONAssert.assertEquals(expectedJson,strResponse, false);
            }

	       
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    @Test
    // Purpose: When a GET request is made and there are no users currently in the API an empty list should be returned and operation should be successful
    public void pttTest4() throws Exception {
        try {
            deleteUsers();

            CloseableHttpResponse response = getUsers();
            String expectedJson = "[]";

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, status);
            HttpEntity entity;
            String strResponse = "";
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                JSONAssert.assertEquals(expectedJson, strResponse, false);
            }
   
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a post request is made with an email and first name and last name then the operation should be successful
    public void pttTest5() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String uid = "0";
            String firstname = "John";
            String lastname = "Doe";
            String email = "john@doe.org";
            StringEntity input = new StringEntity("{\"id\":\"" + uid + "\"," + "\"firstname\":\"" + firstname + "\"," +
                    "\"lastname\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);

            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 201);

            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
                String strResponse = EntityUtils.toString(entity);
                String id = getIdFromStringResponse(strResponse);
                String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ id + ",\"lastName\":\"Doe\"}";
                JSONAssert.assertEquals(expectedJson,strResponse, false);
            } 
           
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a post request is made with no email but with a first name and last name then this should be a successful operation as no attribute is required by the schema
    public void pttTest6() throws Exception {

        try {
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String firstname = "John";
            String lastname = "Doe";
            StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                    "\"lastname\":\"" + lastname + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            //email is mandatory
            Assert.assertEquals(400, status);
            HttpEntity entity;
//            if (status == 201) {
//                entity = response.getEntity();
//                String strResponse = EntityUtils.toString(entity);
//                String id = getIdFromStringResponse(strResponse);
//                String expectedJson = "{\"id\":\"" + id + "\",\"firstname\":\"John\",\"lastname\":\"Doe\",\"email\":\"\"}";
//                JSONAssert.assertEquals(expectedJson,strResponse, false);
//            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a post request is made with an email and first name but no last name then the operation should be successful
    public void pttTest7() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String firstname = "John";
            String lastname = "Doe";
            String email = "john@doe.org";
            StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                    "\"email\":\"" + email + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 201);
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
                String strResponse = EntityUtils.toString(entity);
                String id = getIdFromStringResponse(strResponse);
                String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ id + ",\"lastName\":\"\"}";
                JSONAssert.assertEquals(expectedJson,strResponse, false);
            } 
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a post request is made with first name but no email and last name then this should be a successful operation as no attribute is required by the schema
    public void pttTest8() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String firstname = "John";

            StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            //email is mandatory
            Assert.assertEquals(status, 400);
            HttpEntity entity;
//            if (status == 201) {
//                entity = response.getEntity();
//                String strResponse = EntityUtils.toString(entity);
//                String id = getIdFromStringResponse(strResponse);
//                String expectedJson = "{\"id\":\"" + id + "\",\"firstname\":\"John\",\"lastname\":\"\",\"email\":\"\"}";
//                JSONAssert.assertEquals(expectedJson,strResponse, false);
//            }
//            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    @Test
    // If a post request is made with an email and last name but no first name then the operation should be successful
    public void pttTest9() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String email = "john@doe.org";
            String lastname = "Doe";
            StringEntity input = new StringEntity("{\"lastname\":\"" + lastname + "\"," +
            "\"email\":\"" + email + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 201);
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
                String strResponse = EntityUtils.toString(entity);
                String id = getIdFromStringResponse(strResponse);
                String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"\",\"id\":"+ id + ",\"lastName\":\"Doe\"}";
                JSONAssert.assertEquals(expectedJson,strResponse, false);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a post request is made with last name but no email and first name then this should be a successful operation as no attribute is required by the schema
    public void pttTest10() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String lastname = "Doe";
            StringEntity input = new StringEntity("{\"lastname\":\"" + lastname + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 400);
            HttpEntity entity;
//            if (status == 201) {
//                entity = response.getEntity();
//                String strResponse = EntityUtils.toString(entity);
//                String id = getIdFromStringResponse(strResponse);
//                String expectedJson = "{\"id\":\"" + id + "\",\"firstname\":\"\",\"lastname\":\"Doe\",\"email\":\"\"}";
//                JSONAssert.assertEquals(expectedJson,strResponse, false);
//            }
//            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    @Test
    // If a post request is made with an email but no last name and first name then the operation should be successful
    public void pttTest11() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            String email = "john@doe.org";
            StringEntity input = new StringEntity("{\"email\":\"" + email + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 201);
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
                String strResponse = EntityUtils.toString(entity);
                String id = getIdFromStringResponse(strResponse);
                String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"\",\"id\":"+ id + ",\"lastName\":\"\"}";
                JSONAssert.assertEquals(expectedJson,strResponse, false);
            } 
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // If a post request is made with  no last name, email and first name then the operation should be successful
    public void pttTest12() throws Exception {

        try {
            deleteUsers();
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            
            StringEntity input = new StringEntity("{}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            //No email, no first name, no last name ->
            Assert.assertEquals(status, 400);
            HttpEntity entity;
//            if (status == 201) {
//                entity = response.getEntity();
//                String strResponse = EntityUtils.toString(entity);
//                String id = getIdFromStringResponse(strResponse);
//                String expectedJson = "{\"id\":\"" + id + "\",\"firstname\":\"\",\"lastname\":\"\",\"email\":\"\"}";
//                JSONAssert.assertEquals(expectedJson,strResponse, false);
//            }
//            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
    }
    }
    }