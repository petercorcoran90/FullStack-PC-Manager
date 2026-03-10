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
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PcBuildUITest {

    @Value("${local.server.port}")
    private int port;

    private static WebDriver driver;
    private UIHelper ui;

    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginBtn");
    private static final By USER_BUILDS_SECTION = By.id("userBuildsSection");
    private static final By OPEN_CREATE_BUILD_BTN = By.id("openCreateBuildBtn");
    private static final By CREATE_BUILD_MODAL = By.id("createBuildModal");
    private static final By NEW_BUILD_NAME_INPUT = By.id("newBuildName");
    private static final By SAVE_BUILD_BTN = By.id("saveBuildBtn");
    private static final By BUILD_DETAILS_SECTION = By.id("buildDetailsSection");
    private static final By DETAIL_BUILD_NAME = By.id("detailBuildName");
    private static final By DETAIL_BUILD_TOTAL = By.id("detailBuildTotal");
    private static final By ADD_TO_BUILD_BTN = By.cssSelector(".add-to-build-btn");
    private static final By REMOVE_PART_BTN = By.cssSelector(".remove-part-btn");
    private static final By QUANTITY_BADGE = By.cssSelector("#detailPartsList .badge");

    @BeforeAll
    static void setUpDriver() {
        driver = SharedDriver.getDriver();
    }

    @BeforeEach
    @Sql({"/testuser.sql"})
    void setUp() {
        ui = new UIHelper(driver);
        ui.dismissAlertIfPresent();
        ui.navigateTo("http://localhost:" + port + "/");
        ui.clearLocalStorage();
        ui.refreshPage();
        ui.typeText(USERNAME_INPUT, "sqluser");
        ui.typeText(PASSWORD_INPUT, "password");
        ui.clickElement(LOGIN_BUTTON);
        ui.waitForVisibility(USER_BUILDS_SECTION);
    }

    @Test
    void testCreateNewBuildProfile() {
        String uniqueBuildName = "Dream PC " + UUID.randomUUID().toString().substring(0, 4);
        ui.clickElement(OPEN_CREATE_BUILD_BTN);
        ui.waitForVisibility(CREATE_BUILD_MODAL);
        ui.typeText(NEW_BUILD_NAME_INPUT, uniqueBuildName);
        ui.clickElement(SAVE_BUILD_BTN);
        ui.waitForVisibility(BUILD_DETAILS_SECTION);
        String displayedName = ui.getElementText(DETAIL_BUILD_NAME);   
        assertEquals(uniqueBuildName, displayedName);
    }

    @Test
    void testAddDuplicateAndRemoveParts() {
        String buildName = "Performance Build";
        ui.clickElement(OPEN_CREATE_BUILD_BTN);
        ui.waitForVisibility(CREATE_BUILD_MODAL);
        ui.typeText(NEW_BUILD_NAME_INPUT, buildName);
        ui.clickElement(SAVE_BUILD_BTN);
        ui.waitForVisibility(BUILD_DETAILS_SECTION);
        ui.waitForPresence(ADD_TO_BUILD_BTN);
        String initialTotal = ui.getElementText(DETAIL_BUILD_TOTAL);
        assertEquals("0.00", initialTotal);
        ui.clickElement(ADD_TO_BUILD_BTN);
        ui.waitForTextToDisappear(DETAIL_BUILD_TOTAL, "0.00");
        String totalAfterFirstAdd = ui.getElementText(DETAIL_BUILD_TOTAL);
        assertNotEquals("0.00", totalAfterFirstAdd);
        assertTrue(ui.isElementVisible(REMOVE_PART_BTN));
        ui.clickElement(ADD_TO_BUILD_BTN);
        ui.waitForVisibility(QUANTITY_BADGE);
        assertTrue(ui.getElementText(QUANTITY_BADGE).contains("x2"));
        String totalAfterSecondAdd = ui.getElementText(DETAIL_BUILD_TOTAL);
        assertNotEquals(totalAfterFirstAdd, totalAfterSecondAdd);
        ui.clickElement(REMOVE_PART_BTN);
        ui.waitForInvisibility(QUANTITY_BADGE);
        String totalAfterRemove = ui.getElementText(DETAIL_BUILD_TOTAL);
        assertEquals(totalAfterFirstAdd, totalAfterRemove);
    }
}