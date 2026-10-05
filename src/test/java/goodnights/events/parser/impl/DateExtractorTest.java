package goodnights.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for DateExtractor.
 * Tests date extraction from el-date-range and event-date elements.
 */
class DateExtractorTest {

    private DateExtractor extractor;

    @Test
    void extract_DateString_withBlankElDateRange_fallsBackToEventDate() {
        String html = """
            <div>
                <div class="el-date-range">   </div>
                <h6 class="event-date">Jan 20, 2026</h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Jan 20, 2026");
    }

    @Test
    void extract_DateString_withBlankEventDate_returnsEmptyString() {
        String html = """
            <div>
                <h6 class="event-date">   </h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withBothElements_prefersElDateRange() {
        String html = """
            <div>
                <div class="el-date-range">December 15, 2025</div>
                <h6 class="event-date">Jan 20, 2026</h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withDateRange_extractsCorrectly() {
        String html = """
            <div>
                <div class="el-date-range">Dec 15-20, 2025</div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Dec 15-20, 2025");
    }

    @Test
    void extract_DateString_withDayOfWeek_extractsCorrectly() {
        String html = """
            <div>
                <div class="el-date-range">Friday, December 15, 2025</div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Friday, December 15, 2025");
    }

    @Test
    void extract_DateString_withElDateRange_returnsDate() {
        String html = """
            <div>
                <div class="el-date-range">December 15, 2025</div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withEmptyElDateRange_fallsBackToEventDate() {
        String html = """
            <div>
                <div class="el-date-range"></div>
                <h6 class="event-date">Jan 20, 2026</h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Jan 20, 2026");
    }

    @Test
    void extract_DateString_withEmptyEventDate_returnsEmptyString() {
        String html = """
            <div>
                <h6 class="event-date"></h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withEventDate_returnsDate() {
        String html = """
            <div>
                <h6 class="event-date">Jan 20, 2026</h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Jan 20, 2026");
    }

    @Test
    void extract_DateString_withMultipleElDateRange_usesFirst() {
        String html = """
            <div>
                <div class="el-date-range">December 15, 2025</div>
                <div class="el-date-range">January 20, 2026</div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withMultipleEventDate_usesFirst() {
        String html = """
            <div>
                <h6 class="event-date">Jan 20, 2026</h6>
                <h6 class="event-date">Feb 15, 2026</h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Jan 20, 2026");
    }

    @Test
    void extract_DateString_withNestedElements_findsCorrectly() {
        String html = """
            <div>
                <div class="outer">
                    <div class="el-date-range">March 10, 2026</div>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("March 10, 2026");
    }

    @Test
    void extract_DateString_withNoDateElements_returnsEmptyString() {
        String html = "<div><p>No date information</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withNullElement_throwsNullPointerException() {
        assertThatThrownBy(() -> extractor.extractDateString(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("eventElement must not be null");
    }

    @Test
    void extract_DateString_withTimeIncluded_extractsCorrectly() {
        String html = """
            <div>
                <div class="el-date-range">December 15, 2025 at 8:00 PM</div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025 at 8:00 PM");
    }

    @Test
    void extract_DateString_withWhitespaceInElDateRange_trimsCorrectly() {
        String html = """
            <div>
                <div class="el-date-range">  December 15, 2025  </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withWhitespaceInEventDate_trimsCorrectly() {
        String html = """
            <div>
                <h6 class="event-date">  Jan 20, 2026  </h6>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Jan 20, 2026");
    }

    @BeforeEach
    void setUp() {
        extractor = new DateExtractor();
    }
}
