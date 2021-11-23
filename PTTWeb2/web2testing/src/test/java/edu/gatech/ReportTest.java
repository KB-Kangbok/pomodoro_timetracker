package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ReportTest extends BrowserFunctions {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @BeforeClass
    public void setup() throws Exception{
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
        utils.login(USERNAME);
        utils.clickUserTab("project");
        utils.createProject(PROJECT);
        utils.clickUserTab("pomodoro");
        utils.clickStartPomodoro();
        utils.clickButton("dialog-accept", true);
        utils.selectProjectForPomodoro(PROJECT);
        Thread.sleep(POMODORO_DURATION);
        Thread.sleep(2000);
        utils.clickButton("continue-cancel", true);
        Thread.sleep(200);
        utils.clickUserTab("report");
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
    }

    @Test
    public void getReport() throws Exception {
        utils.selectProject("projects", PROJECT);
        Thread.sleep(200);
        long time = System.currentTimeMillis();
        Timestamp ts = new Timestamp(time - 10 * 1000 * 60);
        String target = sdf.format(ts);
        utils.changeDate("//*[@id=\"root\"]/div/div[2]/div/div[2]/div/div/div/div/div/div[2]/div/div/input", target);
        ts = new Timestamp(time + 60 * 1000 * 60);
        target = sdf.format(ts);
        utils.changeDate("//*[@id=\"root\"]/div/div[2]/div/div[2]/div/div/div/div/div/div[3]/div/div/input", target);
    }
    
}
