package goodnights.events.config.impl;

import goodnights.events.config.ScraperConfiguration;
import java.time.Duration;

/**
 * Configuration for scraping Goodnights Comedy Club events.
 */
public class ScraperConfigurationImpl implements ScraperConfiguration {
    private static final String BASE_URL =
        "https://www.goodnightscomedy.com/events";
    private static final String FEED_DESCRIPTION =
        "Comedy events at Goodnights Comedy Club in Raleigh, NC";
    private static final String FEED_TITLE = "Goodnights Comedy Club Events";
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(10);
    private static final int RETENTION_DAYS = 7;
    private static final String USER_AGENT =
        "Mozilla/5.0 (compatible; EventsHarvester/1.0)";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getFeedDescription() {
        return FEED_DESCRIPTION;
    }

    @Override
    public String getFeedLink() {
        return getBaseUrl();
    }

    @Override
    public String getFeedTitle() {
        return FEED_TITLE;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return PAGE_LOAD_TIMEOUT;
    }

    @Override
    public int getRetentionDays() {
        return RETENTION_DAYS;
    }

    @Override
    public String getUserAgent() {
        return USER_AGENT;
    }
}
