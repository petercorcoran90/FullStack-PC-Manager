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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HardwarePartUITest {

    @Value("${local.server.port}")
    private int port;

    private static WebDriver driver;
    private UIHelper ui;

    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginBtn");
    private static final By DASHBOARD_VIEW = By.id("dashboardView");
    private static final By ADD_PART_BTN = By.id("addPartBtn");
    private static final By PART_MODAL = By.id("partModal");
    private static final By PART_NAME_INPUT = By.id("partName");
    private static final By PART_MANUFACTURER_INPUT = By.id("partManufacturer");
    private static final By PART_CATEGORY_DROPDOWN = By.id("partCategory");
    private static final By PART_PRICE_INPUT = By.id("partPrice");
    private static final By PART_STOCK_INPUT = By.id("partStock");
    private static final By SAVE_PART_BTN = By.id("savePartBtn");
    private static final By MODAL_ERROR_ALERT = By.id("modalErrorAlert");
    private static final By MODAL_CLOSE_BTN = By.cssSelector("#partModal .btn-close");
    private static final By PARTS_TABLE = By.id("partsTable");
    private static final By TABLE_ROWS = By.cssSelector("#partsTable tbody tr");
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search']");
    private static final By DELETE_CONFIRM_MODAL = By.id("deleteConfirmModal");
    private static final By CONFIRM_DELETE_BTN = By.id("confirmDeleteBtn");
    private static final By SUCCESS_MODAL = By.id("successModal");
    private static final By SUCCESS_MODAL_MSG = By.id("successModalMessage");
    private static final By SUCCESS_MODAL_OK_BTN = By.cssSelector("#successModal button[data-bs-dismiss='modal']");

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
        ui.typeText(USERNAME_INPUT, "admin");
        ui.typeText(PASSWORD_INPUT, "admin");
        ui.clickElement(LOGIN_BUTTON);
        ui.waitForVisibility(DASHBOARD_VIEW);
        ui.waitForVisibility(ADD_PART_BTN);
        ui.waitForPresence(TABLE_ROWS);
    }

    private By getDeleteButtonForPart(String uniqueName) {
        return By.xpath("//td[contains(text(), '" + uniqueName + "')]/following-sibling::td//button[contains(@class, 'delete-part-btn')]");
    }

    private By getEditButtonForPart(String uniqueName) {
        return By.xpath("//td[contains(text(), '" + uniqueName + "')]/following-sibling::td//button[contains(@class, 'edit-part-btn')]");
    }

    private void addHardwarePart(String name, String price, String stock) {
        ui.clickElement(ADD_PART_BTN);
        ui.waitForVisibility(PART_MODAL);
        ui.typeText(PART_NAME_INPUT, name);
        ui.typeText(PART_MANUFACTURER_INPUT, "TestCorp");
        ui.selectDropdownByValue(PART_CATEGORY_DROPDOWN, "CPU");
        ui.typeText(PART_PRICE_INPUT, price);
        ui.typeText(PART_STOCK_INPUT, stock);
        ui.clickElement(SAVE_PART_BTN);
    }

    @Test
    void testSuccessfulPartAddition() {
        String uniqueName = "Test CPU " + UUID.randomUUID().toString().substring(0, 6);
        addHardwarePart(uniqueName, "299.99", "10");
        ui.waitForVisibility(SUCCESS_MODAL);
        assertTrue(ui.getElementText(SUCCESS_MODAL_MSG).contains("Part added successfully."));
        ui.clickElement(SUCCESS_MODAL_OK_BTN);
        ui.waitForInvisibility(SUCCESS_MODAL);
        ui.waitForInvisibility(PART_MODAL);
        ui.typeText(SEARCH_INPUT, uniqueName);
        ui.waitForTextToAppear(PARTS_TABLE, uniqueName);
        assertTrue(ui.getElementText(PARTS_TABLE).contains(uniqueName));
    }

    @Test
    void testSuccessfulPartUpdate() {
        String initialName = "Old GPU " + UUID.randomUUID().toString().substring(0, 6);
        addHardwarePart(initialName, "400.00", "5");
        ui.waitForVisibility(SUCCESS_MODAL);
        ui.clickElement(SUCCESS_MODAL_OK_BTN);
        ui.waitForInvisibility(SUCCESS_MODAL);
        ui.typeText(SEARCH_INPUT, initialName);
        ui.waitForTextToAppear(PARTS_TABLE, initialName);
        ui.clickElement(getEditButtonForPart(initialName));
        ui.waitForVisibility(PART_MODAL);
        String updatedName = "Updated GPU " + UUID.randomUUID().toString().substring(0, 6);
        ui.typeText(PART_NAME_INPUT, updatedName); 
        ui.typeText(PART_PRICE_INPUT, "450.00");
        ui.clickElement(SAVE_PART_BTN);
        ui.waitForVisibility(SUCCESS_MODAL);
        assertTrue(ui.getElementText(SUCCESS_MODAL_MSG).contains("Part updated successfully."));
        ui.clickElement(SUCCESS_MODAL_OK_BTN);
        ui.waitForInvisibility(SUCCESS_MODAL);
        ui.typeText(SEARCH_INPUT, updatedName);
        ui.waitForTextToAppear(PARTS_TABLE, updatedName);
        assertTrue(ui.getElementText(PARTS_TABLE).contains(updatedName));
    }

    @Test
    void testNegativePriceShowsValidationErrorMessage() {
        addHardwarePart("Broken RAM", "-50.00", "10");
        ui.waitForVisibility(MODAL_ERROR_ALERT);
        String errorMessage = ui.getElementText(MODAL_ERROR_ALERT);
        assertTrue(errorMessage.contains("Price must be greater than zero."));
        ui.clickElement(MODAL_CLOSE_BTN);
        ui.waitForInvisibility(PART_MODAL);
    }

    @Test
    void testPartDeletion() {
        String uniqueName = "Delete Me " + UUID.randomUUID().toString().substring(0, 6);
        addHardwarePart(uniqueName, "199.99", "5");
        ui.waitForVisibility(SUCCESS_MODAL);
        ui.clickElement(SUCCESS_MODAL_OK_BTN);
        ui.waitForInvisibility(SUCCESS_MODAL);
        ui.waitForInvisibility(PART_MODAL);
        ui.typeText(SEARCH_INPUT, uniqueName);
        ui.waitForTextToAppear(PARTS_TABLE, uniqueName);
        ui.clickElement(getDeleteButtonForPart(uniqueName));
        ui.waitForVisibility(DELETE_CONFIRM_MODAL);
        ui.clickElement(CONFIRM_DELETE_BTN);
        ui.waitForInvisibility(DELETE_CONFIRM_MODAL);
        ui.typeText(SEARCH_INPUT, "");
        ui.waitForTextToDisappear(PARTS_TABLE, uniqueName);
        assertFalse(ui.getElementText(PARTS_TABLE).contains(uniqueName));
    }
}