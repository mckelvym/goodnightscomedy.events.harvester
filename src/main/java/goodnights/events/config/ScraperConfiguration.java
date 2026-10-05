package goodnights.events.config;

import java.time.Duration;

/**
 * Configuration interface for event scraping.
 * Defines all configurable parameters for the scraping process.
 */
public interface ScraperConfiguration {
    /**
     * Gets the base URL for the events page.
     *
     * @return the events page URL
     */
    String getBaseUrl();

    /**
     * Gets the RSS feed description.
     *
     * @return the feed description
     */
    String getFeedDescription();

    /**
     * Gets the RSS feed link.
     *
     * @return the feed link
     */
    String getFeedLink();

    /**
     * Gets the RSS feed title.
     *
     * @return the feed title
     */
    String getFeedTitle();

    /**
     * Gets the timeout duration for page loads.
     *
     * @return the timeout duration
     */
    Duration getPageLoadTimeout();

    /**
     * Gets the number of days to retain events in the feed.
     * Events older than this will be filtered out.
     *
     * @return number of days to retain events
     */
    int getRetentionDays();

    /**
     * Gets the user agent string to use for web requests.
     *
     * @return user agent string
     */
    String getUserAgent();
}
