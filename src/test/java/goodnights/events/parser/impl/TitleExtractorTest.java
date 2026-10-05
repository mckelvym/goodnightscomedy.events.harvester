package goodnights.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleExtractorTest {

    private TitleExtractor extractor;

    @Test
    void extract_withBlankLink_returnsEmptyString() {
        String html = "<div><h3 class=\"el-header\"><a>   </a></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withEmptyLink_returnsEmptyString() {
        String html = "<div><h3 class=\"el-header\"><a></a></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withLinksOutsideHeader_returnsEmptyString() {
        String html = "<div>"
            + "<h3 class=\"el-header\"></h3>"
            + "<a>Comedy Show</a>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMissingHeader_returnsEmptyString() {
        String html = "<div><a>Stand-up Comedy</a></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMissingLink_returnsEmptyString() {
        String html = "<div><h3 class=\"el-header\">No Link Here</h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMultipleHeaders_returnsFirstOne() {
        String html = "<div>"
            + "<h3 class=\"el-header\"><a>First Show</a></h3>"
            + "<h3 class=\"el-header\"><a>Second Show</a></h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Show");
    }

    @Test
    void extract_withMultipleLinks_returnsFirstLink() {
        String html = "<div>"
            + "<h3 class=\"el-header\">"
            + "<a>First Show</a>"
            + "<a>Second Show</a>"
            + "</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Show");
    }

    @Test
    void extract_withNestedElements_returnsLinkText() {
        String html = "<div><h3 class=\"el-header\"><a>Comedy <span>Night</span></a></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Comedy Night");
    }

    @Test
    void extract_withNoMatchingElements_returnsEmptyString() {
        String html = "<div><p>Some content</p><span>More content</span></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNullElement_throwsNullPointerException() {
        assertThatThrownBy(() -> extractor.extract(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("eventElement must not be null");
    }

    @Test
    void extract_withTitleHavingWhitespace_returnsTrimmedTitle() {
        String html = "<div><h3 class=\"el-header\"><a>  Funny Night  </a></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Funny Night");
    }

    @Test
    void extract_withValidHeaderAndLink_returnsTitle() {
        String html = "<div><h3 class=\"el-header\"><a>Comedy Show</a></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Comedy Show");
    }

    @Test
    void extract_withWrongHeaderClass_returnsEmptyString() {
        String html = "<div><h3 class=\"wrong-class\"><a>Comedy Show</a></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @BeforeEach
    void setUp() {
        extractor = new TitleExtractor();
    }
}
