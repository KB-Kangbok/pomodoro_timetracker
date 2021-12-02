package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ReportTest extends BrowserFunctions {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a"); 
    private final String startDateXpath = "//*[@id=\"root\"]/div/div[2]/div/div[2]/div/div/div/div/div/div[2]/div/div/input";
    private final String endDateXpath = "//*[@id=\"root\"]/div/div[2]/div/div[2]/div/div/div/div/div/div[3]/div/div/input";
    private final String xpath2 = "/html/body/div[2]/div[3]/div/div[1]/div/div[1]/div/button";
    private final String xpath3 = "/html/body/div[2]/div[3]/div/div[1]/div/div[3]/div/div/div/input";
    private final String xpath4 = "/html/body/div[2]/div[3]/div/div[2]/button[2]";
    private long time;

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
        utils.clickButton("continue-cancel", true);
        Thread.sleep(200);
        utils.clickUserTab("report");
        time = System.currentTimeMillis();
    }

    // @BeforeClass
    public void othersetup() throws Exception{
        utils.login("kb@gmail.com");
        utils.clickUserTab("report");
    }

    // @AfterClass
    public void otherteardown() throws Exception{
        utils.logout();
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }

    @Test
    public void getReport() throws Exception {
        utils.selectProject("projects", PROJECT);
        Thread.sleep(200);
        Timestamp ts = new Timestamp(time - 10 * 1000 * 60);
        String target = sdf.format(ts).toLowerCase();
        utils.changeDate(startDateXpath, xpath2, xpath3, xpath4, target);
        ts = new Timestamp(time + 60 * 1000 * 60);
        target = sdf.format(ts).toLowerCase();
        utils.changeDate(endDateXpath, xpath2, xpath3, xpath4, target);
        utils.clickButton("show-report-btn", true);
        Assert.assertEquals(utils.sessionCount(), 1);
        utils.clickButton("close-report-btn", true);
    }

    @Test
    public void getReportHour() throws Exception {
        utils.selectProject("projects", PROJECT);
        Thread.sleep(200);
        Timestamp ts = new Timestamp(time - 10 * 1000 * 60);
        String target = sdf.format(ts).toLowerCase();
        utils.changeDate(startDateXpath, xpath2, xpath3, xpath4, target);
        ts = new Timestamp(time + 60 * 1000 * 60);
        target = sdf.format(ts).toLowerCase();
        utils.changeDate(endDateXpath, xpath2, xpath3, xpath4, target);
        utils.clickCheckbox("time-checkbox");
        utils.clickButton("show-report-btn", true);
        Assert.assertEquals(utils.sessionCount(), 1);
        Assert.assertTrue(utils.isOptionReportExist("time"));
        utils.clickButton("close-report-btn", true);
    }

    @Test
    public void getReportPomodoro() throws Exception {
        utils.selectProject("projects", PROJECT);
        Thread.sleep(200);
        Timestamp ts = new Timestamp(time - 10 * 1000 * 60);
        String target = sdf.format(ts).toLowerCase();
        utils.changeDate(startDateXpath, xpath2, xpath3, xpath4, target);
        ts = new Timestamp(time + 60 * 1000 * 60);
        target = sdf.format(ts).toLowerCase();
        utils.changeDate(endDateXpath, xpath2, xpath3, xpath4, target);
        utils.clickCheckbox("pomodoro-checkbox");
        utils.clickButton("show-report-btn", true);
        Assert.assertEquals(utils.sessionCount(), 1);
        Assert.assertTrue(utils.isOptionReportExist("pomodoro"));
        utils.clickButton("close-report-btn", true);
    }
    
}
