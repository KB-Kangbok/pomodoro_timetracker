package edu.gatech.cs6301.Mobile2;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.swing.RepaintManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;

public class Users_userId extends PTTBackendTestBase {

    

    @Test
    // Purpose: When userId doesn't exist, backend should return error
    public void pttTest1() throws Exception {
        // initPTT();

        try {
            CloseableHttpResponse response = updateUser("0", "John", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When get request is made with a valid user ID the user should be succesfully returned
    public void pttTest2() throws Exception {
        try {
            // String firstname = "John";
            // String lastname = "Doe";
            // String email = "john@doe.org";
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            
            response = getUser(id);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"John\",\"id\":"+ id + ",\"lastName\":\"Doe\"}";
                JSONAssert.assertEquals(expectedJson,strResponse, false);
            } else {
                Assert.assertEquals(status, 200);
            }
           
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When userId exists, test put
    public void pttTest3() throws Exception {
        // initPTT();

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 201);
            String userId = getIdFromResponse(response);
            response.close();

            response = updateUser(userId, "Joe", "Dohn", "joe@dohn.com");
            status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"email\":\"john@doe.org\",\"firstName\":\"Joe\",\"id\":"+ userId + ",\"lastName\":\"Dohn\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When the userId exists, the user has projects, and,
    // the administrator confirms deletion (should check at frontend?),
    // delete request should succeed
    public void pttTest4() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String deleteId = getIdFromResponse(response);
            response.close();

            response = createProject(deleteId, "JohnProject");
            response.close();

            response = deleteUser(deleteId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When the userId exists, the user does not have projects,
    // delete request should succeed
    public void pttTest5() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String deleteId = getIdFromResponse(response);
            response.close();

            response = deleteUser(deleteId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When the userId exists, the user has projects, and,
    // the administrator doesn't confirm deletion (should check at frontend?),
    // delete request should not succeed
    public void pttTest6() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String deleteId = getIdFromResponse(response);
            response.close();

            response = createProject(deleteId, "JohnProject");
            response.close();

            // should not call backend
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When the userID is not provided then GET request should just return list of all the users
    public void pttTest7() throws Exception {
        try {
            deleteUsers();
            HttpGet httpRequest = new HttpGet(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 200);
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                String expectedJson = "[]";
                JSONAssert.assertEquals(expectedJson,strResponse, false);
               
            } 
            EntityUtils.consume(response.getEntity());
            response.close();
           
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When userId doesn't exist, backend should return error
    public void pttTest8() throws Exception {
        // initPTT();

        try {
            CloseableHttpResponse response = updateUser("0", "John", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: When userId doesn't exist, backend should return error
    public void pttTest9() throws Exception {
        // initPTT();

        try {
            CloseableHttpResponse response = deleteUser("0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

}
