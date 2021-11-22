package edu.gatech.cs6301.DevOps;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class Users extends Base {
    private CloseableHttpClient httpclient;

    // Purpose: test GET functionality for /users endpoint
    @Test
    public void pttTest1() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            JSONArray expected = new JSONArray();
            JSONObject expectedObj = new JSONObject();
            expectedObj.put("id", id);
            expected.put(expectedObj);
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            id = getIdFromResponse(response);
            expectedObj = new JSONObject();
            expectedObj.put("id", id);
            expected.put(expectedObj);
            response.close();

            response = getUsers();
            assertStatus(200, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if /users endpoint fails for any request besides POST and GET
    @Test
    public void pttTest2() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john@doe.org");
            response.close();

//            response = putUsers();
            System.out.println(response.getStatusLine().getStatusCode());
            assertStatus(201, response);
            response.close();

            response = deleteUsers();
            assertStatus(200, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if input firstName is a valid String for POST method for endpoint /users
    @Test
    public void pttTest3() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            // could potentially test for other types
            response = createUser("12", "Doe", "john@doe.org");
            assertStatus(201, response);
            response.close();

            response = createUser("[0, 1, 2, 3]", "Doe", "john@doe.org");
            assertStatus(409, response);
            response.close();

            response = createUser("", "Doe", "john@doe.org");
            assertStatus(409, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if input lastname is valid String for POST method for endpoint /users
    @Test
    public void pttTest4() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "12", "john@doe.org");
            assertStatus(201, response);
            response.close();

            response = createUser("John", "[0, 1, 2, 3]", "john@doe.org");
            assertStatus(409, response);
            response.close();

            response = createUser("John", "", "john@doe.org");
            assertStatus(409, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if input email is valid email string for POST method for endpoint /users
    @Test
    public void pttTest5() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "johndoe.org");
            assertStatus(201, response);
            response.close();

            response = createUser("John", "Doe", "12");
            assertStatus(201, response);
            response.close();

            response = createUser("John", "Doe", "[0, 1, 2]");
            assertStatus(201, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: Tests if POST method works for endpoint /users
    public void pttTest6() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john@doe.org");
            assertStatus(201, response);
            String id = getIdFromResponse(response);
            JSONObject expectation = new JSONObject();
            expectation.put("id", id);
            expectation.put("firstName", "John");
            expectation.put("lastName", "Doe");
            expectation.put("email", "john@doe.org");
            assertJSON(expectation, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}