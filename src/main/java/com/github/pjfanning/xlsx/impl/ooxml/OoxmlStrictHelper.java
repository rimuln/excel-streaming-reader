package com.github.pjfanning.xlsx.impl.ooxml;

import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.internal.MemoryPackagePart;
import org.apache.poi.openxml4j.opc.internal.TempFilePackagePart;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFRelation;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.List;

public class OoxmlStrictHelper {
  public static ResourceWithTrackedCloseable<ThemesTable> getThemesTable(StreamingReader.Builder builder, OPCPackage pkg)
          throws IOException, XMLStreamException, InvalidFormatException {
    List<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.THEME.getContentType());
    if (parts.isEmpty()) {
      return null;
    } else {
      PackagePart part = parts.get(0);
      try(TempDataStore tempData = createTempDataStore(builder)) {
        try(
                InputStream is = part.getInputStream();
                OutputStream os = tempData.getOutputStream();
                OoXmlStrictConverter converter = new OoXmlStrictConverter(is, os)
        ) {
          while (converter.convertNextElement()) {
            //continue
          }
        }
        PackagePart newPart = createTempPackagePart(builder, pkg, part);
        try(InputStream is = tempData.getInputStream()) {
          newPart.load(is);
        }
        return new ResourceWithTrackedCloseable(new ThemesTable(newPart), () -> newPart.close() );
      }
    }
  }

  public static ResourceWithTrackedCloseable<StylesTable> getStylesTable(StreamingReader.Builder builder, OPCPackage pkg)
          throws IOException, XMLStreamException, InvalidFormatException {
    List<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.STYLES.getContentType());
    if (parts.isEmpty()) {
      return null;
    } else {
      PackagePart part = parts.get(0);
      try(TempDataStore tempData = createTempDataStore(builder)) {
        try(
                InputStream is = part.getInputStream();
                OutputStream os = tempData.getOutputStream();
                OoXmlStrictConverter converter = new OoXmlStrictConverter(is, os)
        ) {
          while (converter.convertNextElement()) {
            //continue
          }
        }
        PackagePart newPart = createTempPackagePart(builder, pkg, part);
        try(InputStream is = tempData.getInputStream()) {
          newPart.load(is);
        }
        return new ResourceWithTrackedCloseable(new StylesTable(newPart), () -> newPart.close() );
      }
    }
  }

  public static SharedStringsTable getSharedStringsTable(StreamingReader.Builder builder, OPCPackage pkg) throws IOException, XMLStreamException, InvalidFormatException {
    List<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
    if (parts.isEmpty()) {
      return null;
    } else {
      PackagePart part = parts.get(0);
      try(TempDataStore tempData = createTempDataStore(builder)) {
        try(
                InputStream is = part.getInputStream();
                OutputStream os = tempData.getOutputStream();
                OoXmlStrictConverter converter = new OoXmlStrictConverter(is, os)
        ) {
          while (converter.convertNextElement()) {
            //continue
          }
        }
        PackagePart newPart = createTempPackagePart(builder, pkg, part);
        try(InputStream is = tempData.getInputStream()) {
          newPart.load(is);
        }
        return new SharedStringsTable(newPart) {
          @Override
          public void close() throws IOException {
            try {
              super.close();
            } finally {
              newPart.close();
            }
          }
        };
      }
    }
  }

  public static ResourceWithTrackedCloseable getCommentsTable(StreamingReader.Builder builder, PackagePart part)
          throws IOException, XMLStreamException, InvalidFormatException {
    try(TempDataStore tempData = createTempDataStore(builder)) {
      try(
              InputStream is = part.getInputStream();
              OutputStream os = tempData.getOutputStream();
              OoXmlStrictConverter converter = new OoXmlStrictConverter(is, os)
      ) {
        while (converter.convertNextElement()) {
          //continue
        }
      }
      PackagePart newPart = createTempPackagePart(builder, part.getPackage(), part);
      try(InputStream is = tempData.getInputStream()) {
        newPart.load(is);
      }
      return new ResourceWithTrackedCloseable(new CommentsTable(newPart), () -> newPart.close() );
    }
  }

  private static TempDataStore createTempDataStore(StreamingReader.Builder builder) {
    if (builder.avoidTempFiles()) {
      return new TempMemoryDataStore();
    } else {
      return new TempFileDataStore();
    }
  }

  private static PackagePart createTempPackagePart(StreamingReader.Builder builder, OPCPackage pkg,
                                                   PackagePart part) throws IOException, InvalidFormatException {
    if (builder.avoidTempFiles()) {
      return new MemoryPackagePart(pkg, part.getPartName(), part.getContentType());
    } else {
      return new TempFilePackagePart(pkg, part.getPartName(), part.getContentType());
    }
  }
}
