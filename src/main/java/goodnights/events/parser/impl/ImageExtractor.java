package goodnights.events.parser.impl;

import static goodnights.events.parser.impl.CssSelectors.IMAGE;
import static goodnights.events.parser.impl.CssSelectors.IMAGE_CONTAINER;
import static goodnights.events.parser.impl.HtmlConstants.EMPTY;
import static goodnights.events.parser.impl.HtmlConstants.HTTP_PREFIX;
import static goodnights.events.parser.impl.HtmlConstants.SRC_ATTR;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event image URL from HTML.
 */
public final class ImageExtractor {

    private static final String BASE_URL = "https://www.goodnightscomedy.com/";

    /**
     * Extracts the image URL from an event element.
     *
     * @param eventElement the event HTML element
     * @return the image URL, or empty string if not found
     * @throws NullPointerException if eventElement is null
     */
    public String extract(Element eventElement) {
        requireNonNull(eventElement, "eventElement must not be null");
        Element imageContainer =
            eventElement.selectFirst(IMAGE_CONTAINER);
        if (imageContainer != null) {
            Element img = imageContainer.selectFirst(IMAGE);
            if (img != null) {
                String src = img.attr(SRC_ATTR);
                if (!src.isBlank()) {
                    if (!src.startsWith(HTTP_PREFIX)) {
                        src = BASE_URL + src;
                    }
                    return src.trim();
                }
            }
        }
        return EMPTY;
    }
}
