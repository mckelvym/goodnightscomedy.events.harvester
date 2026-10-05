package goodnights.events.parser.impl;

import static goodnights.events.parser.impl.CssSelectors.DESCRIPTION;
import static goodnights.events.parser.impl.CssSelectors.DESCRIPTION_TRUNCATE;
import static goodnights.events.parser.impl.HtmlConstants.EMPTY;
import static goodnights.events.parser.impl.HtmlConstants.TRUNCATION_SUFFIX;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event description from HTML.
 */
public final class DescriptionExtractor {
    private static final int MAX_LENGTH = 500;

    /**
     * Extracts the description from an event element.
     *
     * @param element the event HTML element
     * @return the description, or empty string if not found
     * @throws NullPointerException if eventElement is null
     */
    public String extract(Element element) {
        requireNonNull(element, "element must not be null");
        Element descDiv = element.selectFirst(DESCRIPTION);
        if (descDiv != null) {
            Element truncateDiv = descDiv.selectFirst(DESCRIPTION_TRUNCATE);
            if (truncateDiv != null) {
                String text = truncateDiv.text();
                if (text.length() > MAX_LENGTH) {
                    text = text.substring(0, MAX_LENGTH) + TRUNCATION_SUFFIX;
                }
                return text.trim();
            }
        }
        return EMPTY;
    }
}
