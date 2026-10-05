package goodnights.events.parser.impl;

import static goodnights.events.parser.impl.CssSelectors.HEADER;
import static goodnights.events.parser.impl.CssSelectors.LINK;
import static goodnights.events.parser.impl.HtmlConstants.EMPTY;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event title from HTML.
 */
public final class TitleExtractor {

    private static final String SPECIAL_EVENT = "Special Event: ";

    /**
     * Extracts the title from an event element.
     *
     * @param eventElement the event HTML element
     * @return the title, or empty string if not found
     * @throws NullPointerException if eventElement is null
     */
    public String extract(Element eventElement) {
        requireNonNull(eventElement, "eventElement must not be null");
        Element header = eventElement.selectFirst(HEADER);
        if (header == null) {
            return EMPTY;
        }
        Element link = header.selectFirst(LINK);
        if (link == null) {
            return EMPTY;
        }
        String title = link.text();
        if (title.isBlank()) {
            return EMPTY;
        }
        String trimmed = title.trim();
        if (trimmed.startsWith(SPECIAL_EVENT)) {
            return trimmed.substring(SPECIAL_EVENT.length());
        }
        return trimmed;
    }
}
