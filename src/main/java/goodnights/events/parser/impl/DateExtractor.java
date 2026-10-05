package goodnights.events.parser.impl;

import static goodnights.events.parser.impl.CssSelectors.DATE_RANGE;
import static goodnights.events.parser.impl.CssSelectors.EVENT_DATE;
import static goodnights.events.parser.impl.HtmlConstants.EMPTY;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event date information from HTML.
 */
public final class DateExtractor {
    /**
     * Extracts the date from an event element.
     *
     * @param eventElement the event HTML element
     * @return the date string, or empty string if not found
     * @throws NullPointerException if eventElement is null
     */
    public String extractDateString(Element eventElement) {
        requireNonNull(eventElement, "eventElement must not be null");
        Element dateRange = eventElement.selectFirst(DATE_RANGE);
        if (dateRange != null) {
            String date = dateRange.text();
            if (!date.isBlank()) {
                return date.trim();
            }
        }

        Element eventDate = eventElement.selectFirst(EVENT_DATE);
        if (eventDate != null) {
            String date = eventDate.text();
            if (!date.isBlank()) {
                return date.trim();
            }
        }

        return EMPTY;
    }
}
