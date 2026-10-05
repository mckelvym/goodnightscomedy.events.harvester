package goodnights.events.parser.impl;

import static goodnights.events.parser.impl.CssSelectors.HEADER;
import static goodnights.events.parser.impl.CssSelectors.LINK;
import static goodnights.events.parser.impl.HtmlConstants.EMPTY;
import static goodnights.events.parser.impl.HtmlConstants.HREF_ATTR;
import static goodnights.events.parser.impl.HtmlConstants.HTTP_PREFIX;

import org.jsoup.nodes.Element;

/**
 * Extracts event link/URL from HTML.
 */
public final class LinkExtractor {
    private static final String BASE_URL = "https://www.goodnightscomedy.com";

    /**
     * Extracts the event link from an event element.
     *
     * @param eventElement the event HTML element
     * @return the event URL, or empty string if not found
     */
    public String extract(Element eventElement) {
        Element header = eventElement.selectFirst(HEADER);
        if (header != null) {
            Element link = header.selectFirst(LINK);
            if (link != null) {
                String href = link.attr(HREF_ATTR);
                if (!href.isBlank()) {
                    if (href.startsWith(HTTP_PREFIX)) {
                        return href.trim();
                    } else if (href.startsWith("/")) {
                        return BASE_URL + href.trim();
                    } else {
                        return BASE_URL + "/" + href.trim();
                    }
                }
            }
        }
        return EMPTY;
    }
}
