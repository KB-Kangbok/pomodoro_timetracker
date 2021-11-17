package edu.gatech.cs6301.Mobile2;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId extends PTTBackendTestBase {
	@Test
    // Purpose: When userId doesn't exist, backend should return error
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse response = getProject("0", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
	
	@Test
    // Purpose: When projectId doesn't exist, backend should return error
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse response = getProject("0", "0");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

	@Test
    // Get when userId and projectId are both valid
    public void pttTest3() throws Exception {

        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            response = createProject(userId, "TestProject");
            String projectId = getIdFromResponse(response);
            response = getProject(userId,projectId);
            
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
                String expectedJson = "{\"id\":\"" + projectId + "\",\"projectname\":\"TestProject\"}";
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
    // Delete when userId and projectId are both valid
    public void pttTest4() throws Exception {


    	
        try {
            deleteUsers();
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);

            response = createProject(userId, "TestProject");
            String projectId = getIdFromResponse(response);
            response = deleteProject(userId, projectId);
            
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(status, 200);
//            if (status == 200) {
//                entity = response.getEntity();
//                strResponse = EntityUtils.toString(entity);
//                String expectedJson = "{\"id\":\"" + projectId + "\",\"projectname\":\"TestProject\"}";
//                //JSONAssert.assertEquals(expectedJson, strResponse, false);
//            } else {
//
//            }
           
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
	
	
	
}
