package goodnights.events.parser;


import goodnights.events.domain.EventItem;
import java.util.Optional;
import org.jsoup.nodes.Element;

/**
 * Interface for parsing event information from HTML elements.
 */
public interface EventParser {
    /**
     * Parses an event from an HTML element.
     *
     * @param eventElement the HTML element containing event information
     * @return an Optional containing the parsed EventItem, or empty if parsing fails
     */
    Optional<EventItem> parseEvent(Element eventElement);
}
