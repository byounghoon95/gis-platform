package com.gisplatform.backend.admin.upload;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvParserTest {

    private final CsvParser parser = new CsvParser();

    @Test
    void parsesQuotedValuesAndNormalizesHeaders() {
        String content = "Name,Category,Address,Latitude,Longitude\n"
                + "\"Gangnam, Cafe\",cafe,\"Seoul \"\"Gangnam\"\"\",37.4979,127.0276\n";

        CsvTable table = parser.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        assertThat(table.headers()).containsExactly("name", "category", "address", "latitude", "longitude");
        assertThat(table.rows()).hasSize(1);
        assertThat(table.rows().get(0).rowNumber()).isEqualTo(2);
        assertThat(table.rows().get(0).value("name")).isEqualTo("Gangnam, Cafe");
        assertThat(table.rows().get(0).value("address")).isEqualTo("Seoul \"Gangnam\"");
    }

    @Test
    void skipsBlankRows() {
        CsvTable table = parser.parse(new ByteArrayInputStream("""
                name,category,address,latitude,longitude

                Library,public,Seoul,37.1,127.1
                """.getBytes(StandardCharsets.UTF_8)));

        assertThat(table.rows()).hasSize(1);
        assertThat(table.rows().get(0).rowNumber()).isEqualTo(3);
    }
}
