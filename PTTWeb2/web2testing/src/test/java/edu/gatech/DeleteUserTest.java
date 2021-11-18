package edu.gatech;

import org.testng.annotations.Test;
import org.testng.Assert;

public class DeleteUserTest extends BrowserFunctions{
    @Test
    public void deleteUserNoProject() throws Exception{
        utils.createUser("Joe", "Doe", "123@gmail.com");
        utils.deleteUser("123@gmail.com", true);

        boolean isUser = utils.checkUserExisting("123@gmail.com");
        Assert.assertTrue(!isUser);
    }

    @Test
    public void deleteUserWithProjectAccept() throws Exception{
        utils.createUser("Joe", "Doe", "123@gmail.com");
        utils.createProject("123@gmail.com", "sample");
        utils.deleteUser("123@gmail.com", true);
        
        boolean isUser = utils.checkUserExisting("123@gmail.com");
        Assert.assertTrue(!isUser);
    }

    @Test
    public void deleteUserWithProjectCancel() throws Exception{
        utils.createUser("Joe", "Doe", "123@gmail.com");
        utils.createProject("123@gmail.com", "sample");
        utils.deleteUser("123@gmail.com", false);
        
        boolean isUser = utils.checkUserExisting("123@gmail.com");
        utils.deleteUser("123@gmail.com", true);
        Assert.assertTrue(isUser);
    }
}
