package goodnights.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LinkExtractorTest {

    private LinkExtractor extractor;

    @Test
    void extract_withAbsoluteUrl_returnsUrl() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"https://www.goodnightscomedy.com/events/show-123\">Event</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/events/show-123");
    }

    @Test
    void extract_withBlankHref_returnsEmptyString() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"\">Event</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withHeaderButNoLink_returnsEmptyString() {
        String html = "<div>"
            + "<h3 class=\"el-header\">Plain text</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMultipleLinks_returnsFirst() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"/first\">First</a>"
            + "<a href=\"/second\">Second</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/first");
    }

    @Test
    void extract_withNoHeader_returnsEmptyString() {
        String html = "<div><p>Some content</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withRelativeUrlStartingWithSlash_prependsBaseUrl() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"/events/show-456\">Comedy Night</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/events/show-456");
    }

    @Test
    void extract_withRelativeUrlWithoutSlash_prependsBaseUrlWithSlash() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"events/show-789\">Stand-up</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/events/show-789");
    }

    @Test
    void extract_withWhitespaceAroundUrl_returnsTrimmedUrl() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"  /events/show  \">Event</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com//events/show");
    }

    @Test
    void extract_withWhitespaceHref_returnsEmptyString() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a href=\"   \">Event</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @BeforeEach
    void setUp() {
        extractor = new LinkExtractor();
    }
}
