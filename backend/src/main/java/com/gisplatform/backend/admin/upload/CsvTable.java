package com.gisplatform.backend.admin.upload;

import java.util.List;

record CsvTable(List<String> headers, List<CsvRow> rows) {
}
