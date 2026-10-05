package goodnights.events.feed;

import goodnights.events.domain.EventItem;
import java.util.List;
import java.util.Set;

/**
 * Interface for managing RSS feed operations.
 */
public interface RssFeedManager {
    /**
     * Generates an RSS feed with new and existing events.
     *
     * @param filePath         the path to write the RSS file
     * @param newEvents        the new events to add
     * @param existingFilePath the path to the existing feed (for merging)
     * @throws Exception if generation fails
     */
    void generateFeed(String filePath, List<EventItem> newEvents,
                      String existingFilePath)
        throws Exception;

    /**
     * Loads existing event GUIDs from the RSS file.
     *
     * @param filePath the path to the RSS feed file
     * @return set of existing GUIDs
     * @throws Exception if loading fails
     */
    Set<String> loadExistingGuids(String filePath)
        throws Exception;
}
