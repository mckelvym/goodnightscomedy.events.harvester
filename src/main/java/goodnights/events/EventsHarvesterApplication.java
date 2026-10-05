package goodnights.events;

import goodnights.events.config.ScraperConfiguration;
import goodnights.events.config.impl.ScraperConfigurationImpl;
import goodnights.events.domain.EventItem;
import goodnights.events.feed.RssFeedManager;
import goodnights.events.feed.RssFeedManagerImpl;
import goodnights.events.parser.EventParser;
import goodnights.events.parser.impl.EventParserImpl;
import goodnights.events.scraper.EventScraper;
import goodnights.events.scraper.impl.EventScraperImpl;
import goodnights.events.webdriver.ChromeDriverManager;
import goodnights.events.webdriver.PageLoader;
import goodnights.events.webdriver.WebDriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main application for scraping and generating RSS feed.
 */
public final class EventsHarvesterApplication {

    private static final String DEFAULT_OUTPUT_FILE = "events.xml";
    private static final Logger LOG =
        LoggerFactory.getLogger(EventsHarvesterApplication.class);

    private EventsHarvesterApplication() {
        // utility
    }

    private static void configureLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Main entry point for the application.
     *
     * @param args optional output file path (defaults to events.xml)
     */
    public static void main(String[] args) {
        configureLogging();

        String outputFile = args.length > 0 ? args[0] : DEFAULT_OUTPUT_FILE;

        LOG.info("Starting Goodnights Comedy Events Harvester");
        LOG.info("Output file: {}", outputFile);

        ScraperConfiguration config = new ScraperConfigurationImpl();
        RssFeedManager feedManager = new RssFeedManagerImpl(config);

        try {
            LOG.info("Loading existing feed");
            Set<String> existingGuids = feedManager.loadExistingGuids(outputFile);
            LOG.info("Found {} existing events", existingGuids.size());

            List<EventItem> newEvents = new ArrayList<>(scrapeEvents(config, existingGuids));
            LOG.info("Scraped {} new events", newEvents.size());
            feedManager.generateFeed(outputFile, newEvents, outputFile);

            LOG.info("Harvesting completed successfully");
        } catch (Exception e) {
            LOG.error("Application failed", e);
            System.exit(1);
        }
    }

    private static List<EventItem> scrapeEvents(ScraperConfiguration config,
                                                Set<String> existingGuids) {
        try (WebDriverManager driverManager = new ChromeDriverManager(config)) {
            PageLoader pageLoader = new PageLoader(driverManager.getDriver(),
                config.getPageLoadTimeout());
            EventParser eventParser = new EventParserImpl(
                driverManager.getDriver());
            EventScraper scraper = new EventScraperImpl(
                config, pageLoader, eventParser);
            return scraper.scrapeEvents(existingGuids);
        }
    }
}
