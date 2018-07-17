package org.cisiondata.utils.poi;

import java.util.ArrayList;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;

public class ExcelTest {
	
	@SuppressWarnings("resource")
	public static void test1() throws Exception {
		XSSFEventBasedExcelExtractor extractor = new XSSFEventBasedExcelExtractor("F:\\document\\code\\b.xlsx");
		System.err.println(extractor.getText());
	}
	
	@SuppressWarnings("resource")
	public static void test2() throws Exception {
		OPCPackage opcPackage = OPCPackage.open("F:\\document\\code\\b.xlsx");
        XSSFExcelExtractor xw = new XSSFExcelExtractor(opcPackage);
	    System.err.println(xw.getText());
	}
	
	public static void test3() throws Exception {
		OPCPackage opcPackage = OPCPackage.open("F:\\document\\code\\b.xlsx");
		try {
			ArrayList<PackagePart> parts = opcPackage.getParts();
			for (PackagePart part : parts) {
				FileUtils.read(part.getInputStream(), new DefaultLineHandler()).forEach(d -> System.err.println(d));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		test3();
	
	}
	
}
