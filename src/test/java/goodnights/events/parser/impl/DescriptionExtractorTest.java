package goodnights.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DescriptionExtractorTest {

    private DescriptionExtractor extractor;

    @Test
    void extract_withEmptyTruncateDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<div class=\"truncate\"></div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withExactly500Chars_doesNotTruncate() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            text.append("b");
        }
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<div class=\"truncate\">" + text + "</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).hasSize(500);
        assertThat(result).doesNotEndWith("...");
    }

    @Test
    void extract_withLongDescription_truncatesAt500Chars() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            longText.append("a");
        }
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<div class=\"truncate\">" + longText + "</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).hasSize(503); // 500 + "..."
        assertThat(result).endsWith("...");
    }

    @Test
    void extract_withNoElDescription_returnsEmptyString() {
        String html = "<div><p>Some content</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNoTruncateDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<p>Different structure</p>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNullEventElement_throwsNullPointerException() {
        assertThatThrownBy(() -> extractor.extract(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("element must not be null");
    }

    @Test
    void extract_withShortDescription_returnsFullText() {
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<div class=\"truncate\">Short text</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Short text");
    }

    @Test
    void extract_withValidDescription_returnsText() {
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<div class=\"truncate\">Comedy show featuring multiple acts</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Comedy show featuring multiple acts");
    }

    @Test
    void extract_withWhitespace_returnsTrimmedText() {
        String html = "<div>"
            + "<div class=\"el-description\">"
            + "<div class=\"truncate\">  Stand-up comedy night  </div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Stand-up comedy night");
    }

    @BeforeEach
    void setUp() {
        extractor = new DescriptionExtractor();
    }
}
