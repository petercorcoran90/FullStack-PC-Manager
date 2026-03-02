package com.tus.pcmanager.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SharedDriver {

	private static WebDriver driver;

	public static WebDriver getDriver() {
		if (driver == null) {
			ChromeOptions options = new ChromeOptions();

			options.addArguments("--headless");
			options.addArguments("--disable-gpu");
			options.addArguments("--window-size=1920,1080");
			options.addArguments("--incognito");
			Map<String, Object> prefs = new HashMap<>();
			prefs.put("credentials_enable_service", false);
			prefs.put("profile.password_manager_enabled", false);
			options.setExperimentalOption("prefs", prefs);
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			driver = new ChromeDriver(options);
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				if (driver != null) {
					driver.quit();
				}
			}));
		}
		return driver;
	}
}