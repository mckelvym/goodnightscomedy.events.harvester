package goodnights.events.config.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ScraperConfigurationImpl.
 */
class ScraperConfigurationImplTest {

    private ScraperConfigurationImpl config;

    @BeforeEach
    void setUp() {
        config = new ScraperConfigurationImpl();
    }

    @Test
    void testGetBaseUrl() {
        assertThat(config.getBaseUrl())
            .isEqualTo("https://www.goodnightscomedy.com/events");
    }

    @Test
    void testGetPageLoadTimeout() {
        assertThat(config.getPageLoadTimeout().toSecondsPart())
            .isEqualTo(10);
    }

    @Test
    void testGetRetentionDays() {
        assertThat(config.getRetentionDays())
            .isEqualTo(7);
    }

    @Test
    void testGetUserAgent() {
        assertThat(config.getUserAgent())
            .isEqualTo("Mozilla/5.0 (compatible; EventsHarvester/1.0)");
    }
}
