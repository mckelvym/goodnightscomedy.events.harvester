package goodnights.events.parser.impl;

import static java.util.Objects.requireNonNull;

import goodnights.events.domain.EventItem;
import goodnights.events.parser.EventParser;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses and extracts event fields.
 */
public final class EventParserImpl implements EventParser {
    private static final DateTimeFormatter DATE_NO_YEAR =
        DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.US);
    private static final DateTimeFormatter DATE_NO_YEAR_SHORT =
        DateTimeFormatter.ofPattern("MMM d yyyy", Locale.US);
    private static final DateTimeFormatter DATE_WITH_YEAR =
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);
    private static final DateTimeFormatter DATE_WITH_YEAR_SHORT =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);
    private static final Pattern EVENT_ID_PATTERN =
        Pattern.compile("/events/(\\d+)");
    private static final Logger LOG =
        LoggerFactory.getLogger(EventParserImpl.class);
    private final DateExtractor dateExtractor;
    private final DescriptionExtractor descriptionExtractor;
    private final EventDetailsFetcher detailExtractor;
    private final ImageExtractor imageExtractor;
    private final LinkExtractor linkExtractor;
    private final TitleExtractor titleExtractor;

    /**
     * Creates a EventParserImpl with a WebDriver.
     *
     * @param driver the WebDriver for fetching event detail pages,
     *               or null to skip detailed descriptions
     */
    public EventParserImpl(@Nullable WebDriver driver) {
        this.titleExtractor = new TitleExtractor();
        this.dateExtractor = new DateExtractor();
        this.descriptionExtractor = new DescriptionExtractor();
        this.imageExtractor = new ImageExtractor();
        this.linkExtractor = new LinkExtractor();
        this.detailExtractor = driver != null
            ? new EventDetailsFetcher(driver) : null;
    }

    private String extractEventId(String link) {
        Matcher matcher = EVENT_ID_PATTERN.matcher(link);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractFirstDateFromRange(String dateRange) {
        String[] parts = dateRange.split(" - ");
        if (parts.length < 2) {
            return dateRange;
        }

        String firstPart = parts[0].trim();
        String secondPart = parts[1].trim();

        // Check if second part has a year (contains comma followed by 4 digits)
        int commaIndex = secondPart.indexOf(',');
        if (commaIndex >= 0) {
            String year = secondPart.substring(commaIndex);
            return firstPart + year;
        }

        // No year in range, return just the first date part
        return firstPart;
    }

    @Override
    public Optional<EventItem> parseEvent(Element eventElement) {
        requireNonNull(eventElement, "eventElement must not be null");
        String link = linkExtractor.extract(eventElement);
        if (link.isBlank()) {
            LOG.warn("Skipping event: no link found");
            return Optional.empty();
        }

        String id = extractEventId(link);
        if (id == null) {
            LOG.warn("Skipping event: could not extract ID from {}", link);
            return Optional.empty();
        }

        String dateStr = dateExtractor.extractDateString(eventElement);
        String title = titleExtractor.extract(eventElement);
        if (title.isBlank()) {
            LOG.warn("Skipping event ({}): no title found", dateStr);
            return Optional.empty();
        }

        // Try to fetch detailed description from event page
        String description = "";
        if (detailExtractor != null) {
            description = detailExtractor.extract(link);
        }

        // Fall back to truncated description from main page if needed
        if (description.isBlank()) {
            description = descriptionExtractor.extract(eventElement);
        }

        String imageUrl = imageExtractor.extract(eventElement);
        LocalDate eventDateStart = parseEventDate(dateStr);

        if (eventDateStart == null) {
            throw new IllegalStateException(
                "Failed to parse date for event '" + title + "'. "
                    + "Raw date string: '" + dateStr + "'. "
                    + "Event link: " + link);
        }

        return Optional.of(new EventItem(id, title, link, description, eventDateStart,
            null, imageUrl, null));
    }

    /**
     * Parses a date string to LocalDate.
     * Handles formats with or without year, and date ranges.
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate, or null if parsing fails
     */
    private LocalDate parseEventDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            LOG.warn("Empty date string provided");
            return null;
        }

        String cleanDateStr = dateStr.trim();

        // Handle date ranges by extracting the first date
        if (cleanDateStr.contains(" - ")) {
            cleanDateStr = extractFirstDateFromRange(cleanDateStr);
        }

        // Try parsing with year first
        LocalDate date = tryParseWithYear(cleanDateStr);
        if (date != null) {
            return date;
        }

        // Try parsing without year (infer year)
        date = tryParseWithoutYear(cleanDateStr);
        if (date != null) {
            return date;
        }

        LOG.warn("Could not parse date '{}'", dateStr);
        return null;
    }

    private LocalDate tryParseWithYear(String dateStr) {
        // Try full month name with year: "January 15, 2025"
        try {
            return LocalDate.parse(dateStr, DATE_WITH_YEAR);
        } catch (DateTimeParseException e) {
            // Continue to next format
        }

        // Try abbreviated month with year: "Jan 15, 2025"
        try {
            return LocalDate.parse(dateStr, DATE_WITH_YEAR_SHORT);
        } catch (DateTimeParseException e) {
            // Continue to next format
        }

        return null;
    }

    private LocalDate tryParseWithoutYear(String dateStr) {
        int currentYear = Year.now().getValue();
        String dateWithYear = dateStr + " " + currentYear;

        LocalDate date;

        // Try full month name: "January 15" -> "January 15 2025"
        try {
            date = LocalDate.parse(dateWithYear, DATE_NO_YEAR);
        } catch (DateTimeParseException e) {
            // Try abbreviated month: "Jan 15" -> "Jan 15 2025"
            try {
                date = LocalDate.parse(dateWithYear, DATE_NO_YEAR_SHORT);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }

        // If date is more than 2 months in the past, assume next year
        if (date.isBefore(LocalDate.now().minusMonths(2))) {
            date = date.plusYears(1);
        }

        return date;
    }
}
