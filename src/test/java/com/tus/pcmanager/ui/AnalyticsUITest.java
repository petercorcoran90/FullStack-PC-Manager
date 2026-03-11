package com.tus.pcmanager.ui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AnalyticsUITest {

    @Value("${local.server.port}")
    private int port;

    private static WebDriver driver;
    private UIHelper ui;

    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginBtn");
    private static final By NAV_ANALYTICS_BTN = By.id("navAnalyticsBtn");
    private static final By ANALYTICS_SECTION = By.id("analyticsSection");
    private static final By INVENTORY_CHART = By.id("inventoryChart");
    private static final By NO_DATA_MSG = By.id("noDataMessage");
    private static final By USER_BUILDS_SECTION = By.id("userBuildsSection");
    private static final By ADD_PART_BTN = By.id("addPartBtn");
    private static final By PART_MODAL = By.id("partModal");
    private static final By PART_NAME_INPUT = By.id("partName");
    private static final By PART_MANUFACTURER_INPUT = By.id("partManufacturer");
    private static final By PART_PRICE_INPUT = By.id("partPrice");
    private static final By PART_STOCK_INPUT = By.id("partStock");
    private static final By SAVE_PART_BTN = By.id("savePartBtn");
    private static final By SUCCESS_MODAL = By.id("successModal");
    private static final By SUCCESS_MODAL_OK_BTN = By.cssSelector("#successModal button[data-bs-dismiss='modal']");

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
    void adminCanViewAnalyticsDashboardWithChart() {
        ui.typeText(USERNAME_INPUT, "admin");
        ui.typeText(PASSWORD_INPUT, "admin");
        ui.clickElement(LOGIN_BUTTON);
        ui.waitForVisibility(ADD_PART_BTN);
        ui.clickElement(ADD_PART_BTN);
        ui.waitForVisibility(PART_MODAL);
        ui.typeText(PART_NAME_INPUT, "Graph Test GPU");
        ui.typeText(PART_MANUFACTURER_INPUT, "NVIDIA");
        ui.typeText(PART_PRICE_INPUT, "500");
        ui.typeText(PART_STOCK_INPUT, "10");
        ui.clickElement(SAVE_PART_BTN);
        ui.waitForVisibility(SUCCESS_MODAL);
        ui.clickElement(SUCCESS_MODAL_OK_BTN);
        ui.waitForInvisibility(SUCCESS_MODAL);
        ui.waitForInvisibility(PART_MODAL);
        ui.waitForVisibility(NAV_ANALYTICS_BTN);
        ui.clickElement(NAV_ANALYTICS_BTN);
        ui.waitForVisibility(ANALYTICS_SECTION);
        assertTrue(ui.isElementVisible(INVENTORY_CHART));
        assertFalse(ui.isElementVisible(NO_DATA_MSG));
    }

    @Test
    void userCannotViewAnalyticsDashboard() {
        ui.typeText(USERNAME_INPUT, "user");
        ui.typeText(PASSWORD_INPUT, "user");
        ui.clickElement(LOGIN_BUTTON);
        ui.waitForVisibility(USER_BUILDS_SECTION);
        assertFalse(ui.isElementVisible(NAV_ANALYTICS_BTN));
    }
}