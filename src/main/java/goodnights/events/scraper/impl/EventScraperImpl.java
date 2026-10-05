package goodnights.events.scraper.impl;

import static goodnights.events.parser.impl.CssSelectors.EVENT_ITEM;
import static goodnights.events.parser.impl.CssSelectors.HEADER;
import static goodnights.events.parser.impl.CssSelectors.LINK;
import static goodnights.events.parser.impl.CssSelectors.PAGE_LOAD_SELECTOR;
import static goodnights.events.parser.impl.HtmlConstants.EMPTY;
import static goodnights.events.parser.impl.HtmlConstants.HREF_ATTR;
import static java.util.Objects.requireNonNull;

import goodnights.events.config.ScraperConfiguration;
import goodnights.events.domain.EventItem;
import goodnights.events.parser.EventParser;
import goodnights.events.scraper.EventScraper;
import goodnights.events.webdriver.PageLoader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scrapes events using single-page discovery.
 * Implements the unified 3-phase flow: discover, filter, parse.
 */
public record EventScraperImpl(ScraperConfiguration config, PageLoader pageLoader,
                               EventParser eventParser) implements EventScraper {

    private static final Logger LOG = LoggerFactory.getLogger(EventScraperImpl.class);

    public EventScraperImpl {
        requireNonNull(config, "config must not be null");
        requireNonNull(pageLoader, "pageLoader must not be null");
        requireNonNull(eventParser, "eventParser must not be null");
    }

    /**
     * Phase 1: Discovers all event URLs from the events page.
     * Extracts links from event elements for early filtering.
     *
     * @param eventElementMap Map to store URL to Element mappings for later parsing
     * @return List of discovered event URLs
     */
    private List<String> discoverEventUrls(final Map<String, Element> eventElementMap) {
        final Document doc = pageLoader.loadPage(config.getBaseUrl(), PAGE_LOAD_SELECTOR);
        final Elements eventElements = doc.select(EVENT_ITEM);

        final List<String> urls = new ArrayList<>();
        for (final Element element : eventElements) {
            final String url = extractLink(element);
            if (!url.isEmpty() && eventElementMap.putIfAbsent(url, element) == null) {
                urls.add(url);
            }
        }

        return urls;
    }

    /**
     * Extracts the event link from an event element.
     * Duplicates the logic from LinkExtractor to allow early filtering.
     *
     * @param eventElement the event HTML element
     * @return the event URL, or empty string if not found
     */
    private String extractLink(final Element eventElement) {
        final Element header = eventElement.selectFirst(HEADER);
        if (header != null) {
            final Element link = header.selectFirst(LINK);
            if (link != null) {
                final String href = link.attr(HREF_ATTR);
                if (!href.isBlank()) {
                    final URI uri = URI.create(config.getBaseUrl());
                    final String baseUrl = uri.getScheme() + "://" + uri.getHost();
                    if (href.startsWith("http")) {
                        return href.trim();
                    } else if (href.startsWith("/")) {
                        return baseUrl + href.trim();
                    } else {
                        return baseUrl + "/" + href.trim();
                    }
                }
            }
        }
        return EMPTY;
    }

    /**
     * Phase 2: Filters URLs to only those not in existingGuids.
     *
     * @param urls          All discovered URLs
     * @param existingGuids Set of existing event GUIDs
     * @return Filtered list of new URLs
     */
    private List<String> filterNewUrls(final List<String> urls, final Set<String> existingGuids) {
        return urls.stream().filter(url -> !existingGuids.contains(url)).toList();
    }

    /**
     * Parses a single event and adds it to results.
     *
     * @param url     Event URL
     * @param element Event HTML element
     * @param current Current event number (1-based)
     * @param total   Total number of events
     * @param results List to add parsed event to
     */
    private void parseAndAddEvent(final String url, final Element element, final int current,
                                  final int total, final List<EventItem> results) {
        final Optional<EventItem> eventOpt = eventParser.parseEvent(element);

        if (eventOpt.isPresent()) {
            final EventItem event = eventOpt.get();
            results.add(event);
            LOG.info("Event {}/{}: {} ({})", current, total, event.title(), event.eventDateStart());
        } else {
            LOG.warn("No event returned for: {}", url);
        }
    }

    /**
     * Phase 3: Parses events from the filtered URLs.
     *
     * @param urls            URLs to parse
     * @param eventElementMap Map of URL to Element for parsing
     * @return list of parsed events
     */
    private List<EventItem> parseEvents(final List<String> urls,
                                        final Map<String, Element> eventElementMap) {
        final List<EventItem> events = new ArrayList<>();
        int current = 0;
        for (final String url : urls) {
            current++;
            try {
                parseAndAddEvent(url, eventElementMap.get(url), current, urls.size(), events);
            } catch (final Exception e) {
                LOG.error("Failed to parse event from {}: {}", url, e.getMessage(), e);
            }
        }
        return events;
    }

    @Override
    public List<EventItem> scrapeEvents(final Set<String> existingGuids) {
        requireNonNull(existingGuids, "existingGuids must not be null");

        LOG.info("Starting event scraping from: {}", config.getBaseUrl());

        try {
            // PHASE 1: Discover event URLs
            final Map<String, Element> eventElementMap = new HashMap<>();
            final List<String> allEventLinks = discoverEventUrls(eventElementMap);
            LOG.info("Phase 1 complete: Discovered {} event links", allEventLinks.size());

            // PHASE 2: Filter to new URLs only
            final List<String> newEventLinks = filterNewUrls(allEventLinks, existingGuids);
            LOG.info("Phase 2 complete: {} new events after filtering", newEventLinks.size());

            // PHASE 3: Parse each event
            final List<EventItem> events = parseEvents(newEventLinks, eventElementMap);
            LOG.info("Phase 3 complete: Parsed {} events", events.size());

            return events;
        } catch (final Exception e) {
            LOG.error("Unable to parse events", e);
            return List.of();
        }
    }
}
