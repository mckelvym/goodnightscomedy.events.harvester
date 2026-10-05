package goodnights.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageExtractorTest {

    private ImageExtractor extractor;

    @Test
    void extract_withAbsoluteUrl_returnsUrl() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"https://www.goodnightscomedy.com/images/show.jpg\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/images/show.jpg");
    }

    @Test
    void extract_withBlankSrc_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withEmptyImageContainer_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"el-image-container\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMultipleImages_returnsFirst() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"/first.jpg\" />"
            + "<img src=\"/second.jpg\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com//first.jpg");
    }

    @Test
    void extract_withNoImageContainer_returnsEmptyString() {
        String html = "<div><p>No image</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNoImgTag_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<p>Text content</p>"
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
            .hasMessageContaining("eventElement must not be null");
    }

    @Test
    void extract_withRelativeUrlStartingWithSlash_prependsBaseUrl() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"/images/comedian.jpg\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com//images/comedian.jpg");
    }

    @Test
    void extract_withRelativeUrlWithoutSlash_prependsBaseUrlWithSlash() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"images/event.jpg\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/images/event.jpg");
    }

    @Test
    void extract_withWhitespaceAroundUrl_returnsTrimmedUrl() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"  /images/show.jpg  \" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.goodnightscomedy.com/  /images/show.jpg");
    }

    @Test
    void extract_withWhitespaceSrc_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"el-image-container\">"
            + "<img src=\"   \" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @BeforeEach
    void setUp() {
        extractor = new ImageExtractor();
    }
}
