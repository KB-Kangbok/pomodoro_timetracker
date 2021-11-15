package edu.gatech.cs6301.DevOps;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.junit.Test;

public class Users_UserId extends Base {
    private CloseableHttpClient httpclient;
    // Purpose: POST is not an allowed method, tests if backend properly handles POST requests
    @Test
    public void pttTest1() throws Exception {
        CloseableHttpResponse response = null;
        try {
            response = postUser(0, "John", "Doe", "john@doe.org");
            assertStatus(500, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }      

    // Purpose: test when URL parameters {user_id} is not an integer
    @Test
    public void pttTest2() throws Exception {
	    deleteUsers().close();
        createUser("John", "Doe", "john.doe@gmail.com").close();
        CloseableHttpResponse response = null;
        try {
            response = putUser("John", "JK", "Rowling", "jk.rowling@gmail.com");
            assertStatus(400, response);
            response.close();
            response = getUser("John");
            assertStatus(400, response);
            response.close();
            response = deleteUser("John");
            assertStatus(400, response);
            response.close();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }    

    // Purpose: Tests when URL parameters {user_id} has a value of below 0
    @Test
    public void pttTest3() throws Exception {
	    deleteUsers().close();
        createUser("John", "Doe", "john.doe@gmail.com").close();
        CloseableHttpResponse response = null;
        try {
            response = putUser(-1, "JK", "Rowling", "jk.rowling@gmail.com");
            assertStatus(404, response);
            response.close();
            response = putUser(-999999999, "JK", "Rowling",
                               "jk.rowling@gmail.com");
            assertStatus(404, response);
            response.close();
            response = getUser(-1);
            assertStatus(404, response);
            response.close();
            response = getUser(-999999999);
            assertStatus(404, response);
            response.close();
            response = deleteUser(-1);
            assertStatus(404, response);
            response.close();
            response = deleteUser(-999999999);
            assertStatus(404, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }    

    // Purpose: Tests when the user_Id does not exist on the server
    @Test
    public void pttTest4() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = putUser(0, "JK", "Rowling", "jk.rowling@gmail.com");
            assertStatus(404, response);
            response.close();
            response = getUser(0);
            assertStatus(404, response);
            response.close();
            response = deleteUser(0);
            assertStatus(404, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }    

    // Purpose: Tests when firstname for a PUT is not a valid string
    @Test
    public void pttTest5() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john.doe@gmail.com");
            int id = Integer.valueOf(getIdFromResponse(response));
            response.close();
            response = putUser(id, "123456", "Doe", "john@doe.org");
            assertStatus(200, response);
            response.close();
            response = putUser(id, "[1]", "Doe", "john@doe.org");
            assertStatus(200, response);
            response.close();
            response = putUser(id, "&f3fls", "Doe", "john@#doe.org");
            assertStatus(200, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }    

    // Purpose: Tests when lastname for a PUT is not a valid string
    @Test
    public void pttTest6() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john.doe@gmail.com");
            int id = Integer.valueOf(getIdFromResponse(response));
            response.close();
            response = putUser(id, "John", "123456", "john@doe.org");
            assertStatus(200, response);
            response.close();
            response = putUser(id, "John", "[1]", "john@doe.org");
            assertStatus(200, response);
            response.close();
            response = putUser(id, "John", "&f3fls", "john@#doe.org");
            assertStatus(200, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }    


    // Purpose: Tests when email for a PUT is not a valid email string
    @Test
    public void pttTest7() throws Exception {
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            response = createUser("John", "Doe", "john.doe@gmail.com");
            int id = Integer.valueOf(getIdFromResponse(response));
            response.close();
            response = putUser(id, "John", "Doe", "@.org");
            assertStatus(200, response);
            response.close();
            response = putUser(id, "John", "Doe", "johndoeorg");
            assertStatus(200, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }  

    // Purpose: test if GET functionality is correct for endpoint /users/user_id
    public void pttTest8() throws Exception{
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            final JSONObject expectation0 = new JSONObject();
            expectation0.put("firstname", "John");
            expectation0.put("familyname", "Doe");
            expectation0.put("email", "john.doe@gmail.com");
            response = createUser(expectation0);
            final int id0 = Integer.valueOf(getIdFromResponse(response));
            response.close();
            expectation0.put("id", id0);

            final JSONObject expectation1 = new JSONObject();
            expectation1.put("firstname", "Kate");
            expectation1.put("familyname", "Doe");
            expectation1.put("email", "kate.doe@gmail.com");
            response = createUser(expectation1);
            final int id1 = Integer.valueOf(getIdFromResponse(response));
            response.close();
            expectation1.put("id", id1);

            response = getUser(id0);
            assertStatus(200, response);
            assertJSON(expectation0, response);
            response.close();

            response = getUser(id1);
            assertStatus(200, response);
            assertJSON(expectation1, response);
            response.close();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: test if PUT functionality is correct for endpoint /users/user_id
    public void pttTest9() throws Exception{
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            final JSONObject expectation = new JSONObject();
            expectation.put("firstname", "John");
            expectation.put("familyname", "Doe");
            expectation.put("email", "john.doe@gmail.com");
            response = createUser(expectation);
            final int id = Integer.valueOf(getIdFromResponse(response));
            response.close();
            expectation.put("id", id);
            response = putUser(id, "Tom", "Doe", "tom@doe.org");
            expectation.put("firstname", "Tom");
            expectation.put("familyname", "Doe");
            expectation.put("email", "tom.doe@gmail.com");
            assertJSON(expectation, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    // Purpose: test if DELETE functionality is correct for endpoint /users/user_id
    public void pttTest10() throws Exception{
        deleteUsers().close();
        CloseableHttpResponse response = null;
        try {
            final JSONObject expectation = new JSONObject();
            expectation.put("firstname", "John");
            expectation.put("familyname", "Doe");
            expectation.put("email", "john.doe@gmail.com");
            response = createUser(expectation);
            final int id = Integer.valueOf(getIdFromResponse(response));
            response.close();
            expectation.put("id", id);
            response = deleteUser(id);
            assertStatus(200, response);
            assertJSON(expectation, response);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
