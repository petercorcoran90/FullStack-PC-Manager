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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginUITest {

    @Value("${local.server.port}")
    private int port;

    private static WebDriver driver;
    private UIHelper ui;

    @BeforeAll
    static void setUpDriver() {
        driver = SharedDriver.getDriver();
    }

    @BeforeEach
    void setUp() {
        ui = new UIHelper(driver);
        ui.dismissAlertIfPresent();
        ui.navigateTo("http://localhost:" + port + "/login.html");
        ui.clearLocalStorage();
        driver.navigate().refresh();
    }

    @Test
    @Sql({"/testuser.sql"}) 
    void testSuccessfulLoginRedirectsToDashboard() {
        ui.typeText(By.id("username"), "sqluser");
        ui.typeText(By.id("password"), "password");
        ui.clickElement(By.tagName("button"));
        assertTrue(ui.isElementVisible(By.id("logoutBtn")));
        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }

    @Test
    void testFailedLoginShowsErrorMessage() {
        ui.typeText(By.id("username"), "wronguser");
        ui.typeText(By.id("password"), "wrongpass");
        ui.clickElement(By.tagName("button"));
        assertTrue(ui.isElementVisible(By.id("errorMsg")));
        assertEquals("Invalid username or password.", ui.getElementText(By.id("errorMsg")));
    }
}