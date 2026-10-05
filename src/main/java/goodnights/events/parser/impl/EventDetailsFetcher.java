package goodnights.events.parser.impl;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts detailed description from event detail pages.
 */
public final class EventDetailsFetcher {
    private static final String EVENT_DETAIL = "event-detail";
    private static final Logger LOG =
        LoggerFactory.getLogger(EventDetailsFetcher.class);
    private static final int WAIT_TIMEOUT_SECONDS = 10;

    private final WebDriver driver;
    private final WebDriverWait wait;

    /**
     * Creates an EventDetailsFetcher.
     *
     * @param driver the WebDriver instance to use for fetching pages
     */
    public EventDetailsFetcher(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
            Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));
    }

    private String cleanDescription(String text) {
        if (text == null) {
            return "";
        }

        // Remove the management disclaimer if present
        String cleaned = text.replaceAll(
            "Management reserves the right to prevent.*?patrons\\.?",
            "").trim();

        // Remove excessive whitespace
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }

    /**
     * Extracts detailed description from an event page.
     *
     * @param eventUrl the URL of the event detail page
     * @return the detailed description, or empty string if not found
     */
    public String extract(String eventUrl) {
        if (eventUrl == null || eventUrl.isBlank()) {
            return "";
        }

        try {
            driver.get(eventUrl);

            // Wait for the page to load (looking for event-detail div)
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(
                    org.openqa.selenium.By.id(EVENT_DETAIL)),
                ExpectedConditions.presenceOfElementLocated(
                    org.openqa.selenium.By.className(EVENT_DETAIL))
            ));

            String pageSource = requireNonNull(driver.getPageSource());
            Document doc = Jsoup.parse(pageSource);

            // Try to extract from the bio section first (cleaner content)
            Element bioPanel = doc.selectFirst("div#bio");
            if (bioPanel != null) {
                String bioText = bioPanel.text();
                if (!bioText.isBlank()) {
                    return cleanDescription(bioText);
                }
            }

            // Fallback to description panel
            Element descrPanel = doc.selectFirst("div#descr .custom-content");
            if (descrPanel != null) {
                String descrText = descrPanel.text();
                if (!descrText.isBlank()) {
                    return cleanDescription(descrText);
                }
            }

            return "";
        } catch (Exception e) {
            LOG.warn("Error fetching event details from {}: {}",
                eventUrl, e.getMessage());
            return "";
        }
    }
}
