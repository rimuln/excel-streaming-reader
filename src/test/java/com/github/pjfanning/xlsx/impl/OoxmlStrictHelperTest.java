package com.github.pjfanning.xlsx.impl;

import com.github.pjfanning.xlsx.StreamingReader;
import com.github.pjfanning.xlsx.impl.ooxml.OoxmlStrictHelper;
import com.github.pjfanning.xlsx.impl.ooxml.ResourceWithTrackedCloseable;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OoxmlStrictHelperTest {
  @Test
  public void testThemes() throws Exception {
    StreamingReader.Builder builder1 = StreamingReader.builder().setAvoidTempFiles(false);
    StreamingReader.Builder builder2 = StreamingReader.builder().setAvoidTempFiles(true);
    for(StreamingReader.Builder builder : new StreamingReader.Builder[]{builder1, builder2}) {
      try (OPCPackage pkg = OPCPackage.open(new File("src/test/resources/sample.strict.xlsx"), PackageAccess.READ)) {
        try (ResourceWithTrackedCloseable<ThemesTable> themes = OoxmlStrictHelper.getThemesTable(builder, pkg)) {
          assertNotNull(themes.getResource().getThemeColor(ThemesTable.ThemeElement.DK1.idx));
        }
      }
    }
  }

  @Test
  public void testStyles() throws Exception {
    StreamingReader.Builder builder1 = StreamingReader.builder().setAvoidTempFiles(false);
    StreamingReader.Builder builder2 = StreamingReader.builder().setAvoidTempFiles(true);
    for(StreamingReader.Builder builder : new StreamingReader.Builder[]{builder1, builder2}) {
      try(OPCPackage pkg = OPCPackage.open(new File("src/test/resources/sample.strict.xlsx"), PackageAccess.READ)) {
        try (
                ResourceWithTrackedCloseable<StylesTable> stylesWrapper = OoxmlStrictHelper.getStylesTable(builder, pkg);
                ResourceWithTrackedCloseable<ThemesTable> themesWrapper = OoxmlStrictHelper.getThemesTable(builder, pkg)
        ) {
          StylesTable styles = stylesWrapper.getResource();
          styles.setTheme(themesWrapper.getResource());
          assertEquals("has right borders", 1, styles.getBorders().size());
          assertEquals("has right fonts", 11, styles.getFonts().size());
          assertEquals("has right cell styles", 3, styles.getNumCellStyles());
        }
      }
    }
  }

  @Test
  public void testSharedStrings() throws Exception {
    StreamingReader.Builder builder1 = StreamingReader.builder().setAvoidTempFiles(false);
    StreamingReader.Builder builder2 = StreamingReader.builder().setAvoidTempFiles(true);
    for (StreamingReader.Builder builder : new StreamingReader.Builder[]{builder1, builder2}) {
      try (OPCPackage pkg = OPCPackage.open(new File("src/test/resources/sample.strict.xlsx"), PackageAccess.READ)) {
        SharedStringsTable sst = OoxmlStrictHelper.getSharedStringsTable(builder, pkg);
        assertEquals("has right count", 15, sst.getUniqueCount());
        assertEquals("ipsum", sst.getItemAt(1).getString());
      }
    }
  }
}
