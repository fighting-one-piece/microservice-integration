package org.platform.utils.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Normalizer;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class FastXlsx2XmlSerializer extends DefaultHandler {

    private static final Logger logger = LoggerFactory.getLogger(FastXlsx2XmlSerializer.class);

    public static final String PREFIX = "pre";

    public static final String LIGNE = "ligne";

    public static void main(String[] args) {
        FastXlsx2XmlSerializer serializer = new FastXlsx2XmlSerializer();
        try {
            serializer.serializeXlsFile(new File("src/main/resources/Classeur1.xlsx"));
        } catch (IOException ex) {
            logger.error("", ex);
        } catch (InvalidFormatException ex) {
            logger.error("", ex);
        }
    }

    @SuppressWarnings("deprecation")
	public void serializeXlsFile(File xls) throws IOException, InvalidFormatException {
        final String xlsName = xls.getName();
        String name = FilenameUtils.removeExtension(xlsName);
        File serializedXlsFile = new File(xls.getParent(), name + ".xml");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(serializedXlsFile);
            try {
                output = new OutputStreamWriter(fos, "UTF-8");
                try {
                    OPCPackage p = OPCPackage.open(xls.getPath(), PackageAccess.READ);
                    sharedStringsTable = new ReadOnlySharedStringsTable(p);
                    XSSFReader xssfReader = new XSSFReader(p);
                    stylesTable = xssfReader.getStylesTable();
                    writeXmlStart(output);
                    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                    while (iter.hasNext()) {
                        InputStream stream = iter.next();
                        InputSource sheetSource = new InputSource(stream);
                        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                        SAXParser saxParser = saxFactory.newSAXParser();
                        XMLReader sheetParser = saxParser.getXMLReader();
                        String sheetName = iter.getSheetName();
                        String normalizedName = normalize(sheetName);
                        writeSheet(output, normalizedName);
                        sheetParser.setContentHandler(this);
                        try {
                            sheetParser.parse(sheetSource);
                        } catch (NoHeaderException e) {
                            logger.error("", e);
                        }
                        writeClosingTag(output, normalizedName);
                        stream.close();
                    }
                    writeClosingTag(output, "root");
                } catch (SAXException ex) {
                    logger.error("", ex);
                } catch (OpenXML4JException ex) {
                    logger.error("", ex);
                } catch (ParserConfigurationException ex) {
                    logger.error("", ex);
                }
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public void writeXmlStart(Writer writer) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writeTag(writer, "root");
        writer.write(" xmlns:");
        writer.write(PREFIX);
        writer.write("=\"");
        writer.write("http://uri/");
        writer.write('"');
        writeSuperior(writer);
    }

    public void writeSheet(Writer writer, String normalizedName) throws IOException {
        writeTag(writer, normalizedName);
        writeSuperior(writer);
    }

    public void writeLine(Writer writer, StringWriter rowStr) throws IOException {
        writer.write('\t');
        writeTag(writer, LIGNE);
        writeSuperior(writer);
        writer.write(rowStr.toString());
        writer.write('\t');
        writeClosingTag(writer, LIGNE);
    }

    public void writeCell(Writer writer, String cellToString, String colHeader) throws IOException {
        writer.write("\t\t");
        writeTag(writer, colHeader);
        writer.write('>');
        String escaped = StringEscapeUtils.escapeXml(cellToString);
        writer.write(escaped);
        writeClosingTag(writer, colHeader);
    }

    public void writeSuperior(Writer writer) throws IOException {
        writer.write(">\n");
    }

    public void writeClosingTag(Writer writer, String name) throws IOException {
        writer.write("</");
        writeQName(writer, name);
        writeSuperior(writer);
    }

    public void writeTag(Writer writer, String name) throws IOException {
        writer.write('<');
        writeQName(writer, name);
    }

    public void writeQName(Writer writer, String name) throws IOException {
        writer.write(PREFIX);
        writer.write(':');
        writer.write(name);
    }

    @SuppressWarnings("serial")
	public class NoHeaderException extends RuntimeException {
        public NoHeaderException() {
        }
    }

    public static String normalize(final String input) {
        final StringBuilder validName = new StringBuilder();
        final String spaceNormalized = StringUtils.normalizeSpace(input);
        final String trimed = Normalizer.normalize(spaceNormalized, Normalizer.Form.NFD);
        if (trimed.isEmpty()) {
            return "_";
        }
        final char firstChar = trimed.charAt(0);
        if (Character.isLetter(firstChar) || firstChar == '_') {
            validName.append(Character.toLowerCase(firstChar));
        } else if (Character.isDigit(firstChar)) {
            validName.append("_");
            validName.append(firstChar);
        }
        for (int i = 1; i < trimed.length(); i++) {
            final char charAt = trimed.charAt(i);
            if (Character.isWhitespace(charAt)) {
                validName.append("_");
            } else if (Character.isDigit(charAt) || charAt == '_') {
                validName.append(charAt);
            } else if (Character.isLetter(charAt)) {
                validName.append(Character.toLowerCase(charAt));
            }
        }
        return validName.toString();
    }

    /**
     * The type of the data value is indicated by an attribute on the cell. The value is usually in a "v" element within the cell.
     */
    enum xssfDataType {

        BOOL,
        ERROR,
        FORMULA,
        INLINESTR,
        SSTINDEX,
        NUMBER,
    }

    /**
     * Table with styles
     */
    private StylesTable stylesTable;

    /**
     * Table with unique strings
     */
    private ReadOnlySharedStringsTable sharedStringsTable;

    /**
     * Destination for data
     */
    private Writer output;

    // Set when V start element is seen
    private boolean vIsOpen;

    // Set when cell start element is seen;
    // used when cell close element is seen.
    private xssfDataType nextDataType;

    // Used to format numeric cell values.
    private short formatIndex;
    private String formatString;
    private final DataFormatter formatter = new DataFormatter();

    // Gathers characters as they are seen.
    private final StringBuffer value = new StringBuffer();

    private final LinkedHashMap<Integer, String> mapColNum2HeaderNameTmp = new LinkedHashMap<Integer, String>();

    private LinkedHashMap<Integer, String> mapColNum2HeaderName;

    // we accept 3 empty rows
    int row = 0;

    int col = 0;

    int nbEmptyRows = 0;

    StringWriter rowStr = new StringWriter();

    @Override
    public void startDocument() throws SAXException {
        vIsOpen = false;
        value.setLength(0);
        mapColNum2HeaderNameTmp.clear();
        mapColNum2HeaderName = null;
        nbEmptyRows = 0;
        rowStr = new StringWriter();
    }


    /*
     * (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if ("t".equals(name) || "v".equals(name)) {
            vIsOpen = true;
            // Clear contents cache
            value.setLength(0);
        } // c => cell
        else if ("c".equals(name)) {
            // Get the cell reference
            String r = attributes.getValue("r");
            int firstDigit = -1;
            for (int c = 0; c < r.length(); ++c) {
                if (Character.isDigit(r.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            col = nameToColumn(r.substring(0, firstDigit));
            // Set up defaults.
            vIsOpen = false;
            nextDataType = xssfDataType.NUMBER;
            formatIndex = -1;
            formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType)) {
                nextDataType = xssfDataType.BOOL;
            } else if ("e".equals(cellType)) {
                nextDataType = xssfDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                nextDataType = xssfDataType.INLINESTR;
            } else if ("s".equals(cellType)) {
                nextDataType = xssfDataType.SSTINDEX;
            } else if ("str".equals(cellType)) {
                nextDataType = xssfDataType.FORMULA;
            } else if (cellStyleStr != null) {
                // It's a number, but almost certainly one
                //  with a special style or format 
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                formatIndex = style.getDataFormat();
                formatString = style.getDataFormatString();
                if (formatString == null) {
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
            }
        } else if ("row".equals(name)) {
            String r = attributes.getValue("r");
            row = Integer.valueOf(r);
            // s'il n'y a qu'une seule en-tÃªte de colonne, ce n'est pas la ligne d'en-tÃªtes de colonnes
            if (mapColNum2HeaderNameTmp.size() == 1) {
                mapColNum2HeaderNameTmp.clear();
            } else if (!mapColNum2HeaderNameTmp.isEmpty()) {
                mapColNum2HeaderName = mapColNum2HeaderNameTmp;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (mapColNum2HeaderName == null) {
            fillColHeaders(name);
        } else {
            fillDataRow(name);
        }
    }

    protected void fillColHeaders(String name) throws NumberFormatException, NoHeaderException {
        if ("v".equals(name) || "t".equals(name)) {
            String stringCellValue = getStringCellValue();
            if (!StringUtils.isEmpty(stringCellValue)) {
                nbEmptyRows = 0;
                final String colHeader = normalize(stringCellValue);
                mapColNum2HeaderNameTmp.put(col, colHeader);
            }
        } else if ("row".equals(name)) {
            if (nbEmptyRows > 3) {
                throw new NoHeaderException();
            }
            nbEmptyRows++;
        }
    }

    protected void fillDataRow(String name) throws NumberFormatException {
        if ("v".equals(name) || "t".equals(name)) {
            String stringCellValue = getStringCellValue();
            String colHeader = mapColNum2HeaderName.get(col);
            if (colHeader != null) {
                try {
                    if (!stringCellValue.isEmpty()) {
                        writeCell(rowStr, stringCellValue, colHeader);
                        nbEmptyRows = 0;
                    }
                } catch (IOException ex) {
                    logger.error("", ex);
                }
            }
        } else if ("row".equals(name)) {
            // s'il y a des donnÃ©es Ã  Ã©crire
            if (nbEmptyRows == 0) {
                try {
                    writeLine(output, rowStr);
                } catch (IOException ex) {
                    logger.error("", ex);
                }
            }
            nbEmptyRows++;
            rowStr = new StringWriter();
        }
    }

    protected String getStringCellValue() throws NumberFormatException {
        // Process the value contents as required.
        // To be done in endElement(), as characters() may be called more than once
        switch (nextDataType) {
            case BOOL:
                char first = value.charAt(0);
                return first == '0' ? "FALSE" : "TRUE";
            case ERROR:
                return "\"ERROR:" + value.toString() + '"';
            case FORMULA:
                // A formula could result in a string value,
                // so always add double-quote characters.
                return value.toString();
            case INLINESTR:
                // TODO: have seen an example of this, so it's untested.
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                return rtsi.toString();
            case SSTINDEX:
                String sstIndex = value.toString();
                try {
                    int idx = Integer.parseInt(sstIndex);
                    String entryAt = sharedStringsTable.getEntryAt(idx);
                    XSSFRichTextString rtss = new XSSFRichTextString(entryAt);
                    return rtss.toString();
                } catch (NumberFormatException ex) {
                    throw ex;
                }
            case NUMBER:
                String n = value.toString();
                double parseDouble = Double.parseDouble(n);
                // s'il n'y a pas de dÃ©cimales, on retourne le nombre comme un entier (cas gÃ©nÃ©ral)
                if (parseDouble % 1 == 0) {
                    return String.valueOf((int) parseDouble);
                }
                if (this.formatString != null) {
                    return formatter.formatRawCellContents(parseDouble, this.formatIndex, this.formatString);
                }
                return n;
            default:
                return "(TODO: Unexpected type: " + nextDataType + ")";
        }
    }

    /**
     * Captures characters only if a suitable element is open. Originally was just "v"; extended for inlineStr also.
     *
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (vIsOpen) {
            value.append(ch, start, length);
        }
    }

    /**
     * Converts an Excel column name like "C" to a zero-based index.
     *
     * @param name
     * @return Index corresponding to the specified name
     */
    private int nameToColumn(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
    }

}
