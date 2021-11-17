package edu.gatech.cs6301.Mobile2;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects extends PTTBackendTestBase {
	@Test
    // Purpose: When userId doesn't exist, backend should return error
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse response = getAllProjects("0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
	

    @Test
    // Purpose: When get request is made with a valid user ID, list of projects for the user should be returned;
    public void pttTest2() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response = createProject(userId, "TestProject");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getAllProjects(userId);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                String expectedJson = "[{\"id\":\"" + projectId + "\",\"projectname\":\"TestProject\"}]";
                //JSONAssert.assertEquals(expectedJson, strResponse, false);
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
    // Post request when the userId exists and with a valid project body
    public void pttTest3() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response =  createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);

            response = createProject(id, "TestProject");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                String projectId = getIdFromStringResponse(strResponse);
                String expectedJson = "[{\"id\":\"" + projectId + "\",\"projectName\":\"TestProject\"}]";
                //JSONAssert.assertEquals(expectedJson, strResponse, false);
            } else {
                Assert.assertEquals(status, 405);
            }
            
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Post request when the userId exists but with a invalid project body
    public void pttTest4() throws Exception {

    	   
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
        	HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + id + "/projects");
            httpRequest.addHeader("accept", "application/json");
            StringEntity input = new StringEntity("{}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            response = httpclient.execute(httpRequest);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 400);
            
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    	    
    }


}
