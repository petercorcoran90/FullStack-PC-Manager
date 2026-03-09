package com.tus.pcmanager.ui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginUITest {

    @Value("${local.server.port}")
    private int port;

    private static WebDriver driver;
    private UIHelper ui;

    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginBtn");
    private static final By LOGOUT_BUTTON = By.id("logoutBtn");
    private static final By DASHBOARD_VIEW = By.id("dashboardView");
    private static final By ERROR_MSG = By.id("loginErrorMsg");

    @BeforeAll
    static void setUpDriver() {
        driver = SharedDriver.getDriver();
    }

    @BeforeEach
    void setUp() {
        ui = new UIHelper(driver);
        ui.dismissAlertIfPresent();
        ui.navigateTo("http://localhost:" + port + "/");
        ui.clearLocalStorage();
        ui.refreshPage();
    }

    @Test
    @Sql({"/testuser.sql"}) 
    void testSuccessfulLoginDisplaysDashboard() {
        ui.typeText(USERNAME_INPUT, "sqluser");
        ui.typeText(PASSWORD_INPUT, "password");
        ui.clickElement(LOGIN_BUTTON);
        
        assertTrue(ui.isElementVisible(LOGOUT_BUTTON));
        assertTrue(ui.isElementVisible(DASHBOARD_VIEW));
    }

    @Test
    void testFailedLoginShowsErrorMessage() {
        ui.typeText(USERNAME_INPUT, "wronguser");
        ui.typeText(PASSWORD_INPUT, "wrongpass");
        ui.clickElement(LOGIN_BUTTON);
        
        assertTrue(ui.isElementVisible(ERROR_MSG));
        assertTrue(ui.getElementText(ERROR_MSG).contains("Invalid username or password"));
    }
}