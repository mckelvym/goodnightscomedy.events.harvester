package goodnights.events.parser.impl;

/**
 * Constants for CSS selectors used in HTML parsing.
 *
 * <p>This class centralizes all CSS selector strings used throughout the
 * parser implementation to avoid magic strings and improve maintainability.
 */
public final class CssSelectors {

    /**
     * Selector for event items on the main page.
     */
    public static final String EVENT_ITEM = "div.event-list-item";

    /**
     * Selector for event header.
     */
    public static final String HEADER = "h3.el-header";

    /**
     * Selector for link within header.
     */
    public static final String LINK = "a";

    /**
     * Selector for event date range.
     */
    public static final String DATE_RANGE = "div.el-date-range";

    /**
     * Selector for event date (alternative).
     */
    public static final String EVENT_DATE = "h6.event-date";

    /**
     * Selector for event description container.
     */
    public static final String DESCRIPTION = "div.el-description";

    /**
     * Selector for truncated description.
     */
    public static final String DESCRIPTION_TRUNCATE = "div.truncate";

    /**
     * Selector for image container.
     */
    public static final String IMAGE_CONTAINER = "div.el-image-container";

    /**
     * Selector for image element.
     */
    public static final String IMAGE = "img";

    /**
     * Selector for page load wait condition.
     */
    public static final String PAGE_LOAD_SELECTOR = ".events-list";

    private CssSelectors() {
        // Utility class - prevent instantiation
    }
}
