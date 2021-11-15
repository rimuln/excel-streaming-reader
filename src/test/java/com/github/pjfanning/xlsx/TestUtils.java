package com.github.pjfanning.xlsx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

final class TestUtils {

  static InputStream getInputStream(String fileName) throws IOException {
    return TestUtils.class.getResourceAsStream("/" + fileName);
  }

  static Workbook openWorkbook(String fileName) throws IOException {
      try (InputStream stream = getInputStream(fileName)) {
          return StreamingReader.builder()
                  .open(stream);
      }
  }

  static void expectSameStringContent(Cell cell1, Cell cell2) {
    assertEquals("Cell " + ref(cell1) + " has should equal cell " + ref(cell2) + " string value.",
        cell1.getStringCellValue(), cell2.getStringCellValue());
  }

  static void expectStringContent(Cell cell, String value) {
    assertEquals("Cell " + ref(cell) + " has wrong string content.", value, cell.getStringCellValue());
  }

  static void expectRichStringContent(Cell cell, String value) {
    assertEquals("Cell " + ref(cell) + " has wrong rich-string content.",
            value, cell.getRichStringCellValue().getString());
  }

  static void expectFormattedContent(Cell cell, String value) {
    assertEquals("Cell " + ref(cell) + " has wrong formatted content.",
            value, new DataFormatter().formatCellValue(cell));
  }

  static void expectCachedType(Cell cell, CellType cellType) {
    assertEquals("Cell " + ref(cell) + " has wrong cached type." + cellType, cellType, cell.getCachedFormulaResultType());
  }

  static void expectType(Cell cell, CellType cellType) {
    assertEquals("Cell " + ref(cell) + " has wrong type.", cellType, cell.getCellType());
  }

  static void expectFormula(Cell cell, String formula) {
    assertEquals("Cell " + ref(cell) + " has wrong formula.", formula, cell.getCellFormula());
  }

  private static String ref(Cell cell) {
    return new CellReference(cell).formatAsString();
  }

  static Cell getCellFromNextRow(Iterator<Row> rowIterator, int index) {
    return nextRow(rowIterator)
            .getCell(index);
  }

  static Row nextRow(Iterator<Row> rowIterator) {
    return rowIterator.next();
  }

}
