package org.platform.utils.poi;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Excel工具类*/
public class ExcelUtils {

	private static Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);

	/**
	 * 删除路径下的Excel文件
	 * @param excelFileDir
	 */
	public static void deleteExcelFileDirect(File excelFileDir) {
		if(excelFileDir.exists() && excelFileDir.isDirectory()) {
			for (File file : excelFileDir.listFiles()) {
				file.delete();
			}
		}
	}

	/**
	 * 获取目录下的文件列表
	 * @param fileDir
	 * @return
	 */
	public static List<File> getFileListByDirect(String fileDir) {
		List<File> fileList = new ArrayList<File>();
		File file = new File(fileDir);
		if (file.exists() && file.isDirectory()) {
			for (File f : file.listFiles()) {
				fileList.add(f);
			}
		}
		return fileList;
	}

	/**
	 * 获取真实文件名称
	 * @param readFileDir
	 * @param realFile
	 * @return
	 */
	public static String getRealFileName(String readFileDir, File realFile) {
		File real = realFile;
		File base = new File(readFileDir);
		String ret = real.getName();
		while (true) {
			real = real.getParentFile();
			if (real == null)
				break;
			if (real.equals(base))
				break;
			else
				ret = real.getName() + File.separator + ret;
		}
		return ret;
	}

	/**
	 * 新建一个Wookbook对象
	 * @param excelFile
	 * @return
	 */
	public static HSSFWorkbook newHSSFWorkbook(File excelFile) {
		HSSFWorkbook wb = new HSSFWorkbook();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(excelFile);
			wb.write(fos);
			fos.flush();
		} catch (FileNotFoundException ex) {
			LOG.info(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOG.info(ex.getMessage(), ex);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOG.info(e.getMessage(), e);
				}
			}
		}
		return wb;
	}
	
	/**
	 * 新建一个Wookbook对象
	 * @param excelFile
	 * @return
	 */
	public static XSSFWorkbook newXSSFWorkbook(File excelFile) {
		XSSFWorkbook wb = new XSSFWorkbook();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(excelFile);
			wb.write(fos);
			fos.flush();
		} catch (FileNotFoundException ex) {
			LOG.info(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOG.info(ex.getMessage(), ex);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOG.info(e.getMessage(), e);
				}
			}
		}
		return wb;
	}

	/**
	 * 保存Excel文件
	 * @param wb
	 * @param excelFile
	 */
	public static void saveWorkBook(HSSFWorkbook wb, File excelFile) {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(excelFile);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					LOG.info(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 获取WorkBook对象
	 * @param excelFile
	 * @return
	 */
	public static HSSFWorkbook getWorkBook(File excelFile) {
		FileInputStream fileIn = null;
		HSSFWorkbook wb = null;
		try {
			fileIn = new FileInputStream(excelFile);
			wb = new HSSFWorkbook(fileIn);
			return wb;
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			return null;
		} finally {
			if (fileIn != null) {
				try {
					fileIn.close();
				} catch (IOException e) {
					LOG.info(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 创建Excel文件
	 * @param dataList
	 * @param excelInfoMap
	 * @param excelFile
	 * @param rowCount
	 */
	@SuppressWarnings({"rawtypes"})
	public static void createExcel(List dataList, Map excelInfoMap, File excelFile, int rowCount) {
		int excelFileCount = dataList.size() / rowCount + 1;
		for(int i = 0; i < excelFileCount; i++){
			int start = 0 + i * rowCount;
			int end = 0;
			if(i != excelFileCount - 1){
				end = (i+1) * rowCount - 1;
			}else{
				end = dataList.size();
			}
			System.out.println("~~~~~~~~~~~~~~~start:"+start+"~~~~~~~~~~~end:"+end);
			String excelFileDir = excelFile.getParent() + "\\";
			String excelFileName = excelFile.getName().substring(0, excelFile.getName().lastIndexOf("."));
			String excelFileSuffix = excelFile.getName().substring(excelFile.getName().lastIndexOf("."));
			ExcelUtils.createExcel(dataList.subList(start, end), excelInfoMap, new File(excelFileDir + excelFileName + i + excelFileSuffix));
		}
	}

	/**
	 * 创建Excel文件
	 * @param dataList
	 * @param excelInfoMap
	 * @param excelFile
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void createExcel(List dataList, Map excelInfoMap, File excelFile) {
		try{
			HSSFWorkbook wb = newHSSFWorkbook(excelFile);
			HSSFSheet sheet = wb.createSheet("sheet1");

			HSSFRow row = sheet.createRow(0);
			Map<String,String> excel_map = excelInfoMap;
			String[] titles = getTitleTexts(excel_map);
			String[] methods = getMethodTexts(excel_map);
			HSSFCell cell;
			//创建标题行
			for(int i=0; i<titles.length; i++){
				cell = row.createCell(i);
				cell.setCellValue(titles[i]);
			}

			for(int j=1; j<=dataList.size(); j++){
				row = sheet.createRow(j);
				Object object = dataList.get(j-1);
				for(int k=0;k<titles.length;k++){
					Object cellValue = getMethodValue(methods[k],object);
					cell = row.createCell(k);
					String cellString = null != String.valueOf(cellValue) ? String.valueOf(cellValue) : "";
				    cell.setCellValue(cellString);
				}

			}
			saveWorkBook(wb, excelFile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 读取Excel文件
	 * @param excelFileDir
	 * @return
	 */
	@SuppressWarnings("resource")
	public static InputStream readXlsFile(String excelFileDir) {
		File fileDir = new File(excelFileDir);
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			if(fileDir.exists()){
				File[] fileArray = fileDir.listFiles();
				for(File file : fileArray){
					LOG.info("~~~~~~~~~~fileName:"+file.getName());
					InputStream inStream = new FileInputStream(file);
					HSSFWorkbook w = new HSSFWorkbook(inStream);
					HSSFSheet sheets = wb.createSheet(file.getName());
					HSSFSheet sheet = w.getSheetAt(0);
					LOG.info("~~~~~~~~~~lastNum:"+sheet.getLastRowNum());
					int rowCount = sheet.getLastRowNum();
					for(int i=0;i<rowCount;i++){
						HSSFRow row = sheets.createRow(i);
						HSSFRow r = sheet.getRow(i);
						int columnNum = r.getLastCellNum();
						for(int j=0;j<columnNum;j++){
							HSSFCell cell = row.createCell(j);
							HSSFCell c = r.getCell(j);
							cell.setCellValue(c.getStringCellValue());
						}
					}
				}
			} else {
				fileDir.mkdir();
			}
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			wb.write(outStream);
			byte[] byteArray = outStream.toByteArray();
			InputStream is = new ByteArrayInputStream(byteArray);
			return is;
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		} finally {

		}
		return null;
	}

	/**
	 * 获取Excel标题文本
	 * @param map
	 * @return
	 */
	public static String[] getTitleTexts(Map<String,String> map) {
		String[] titleTexts = new String[map.size()];
		int i = 0;
		for(Map.Entry<String,String> entry : map.entrySet()){
			titleTexts[i] = entry.getKey();
			i++;
		}
		return titleTexts;
	}

	/**
	 * 获取标题文本所对应的属性
	 * @param map
	 * @return
	 */
	public static String[] getMethodTexts(Map<String,String> map) {
		String[] methodTexts = new String[map.size()];
		int i = 0;
		for(Map.Entry<String,String> entry : map.entrySet()){
			methodTexts[i] = entry.getValue();
			i++;
		}
		return methodTexts;
	}

	/**
	 * 获取对象属性值
	 * @param methodName
	 * @param obj
	 * @return
	 */
	public static Object getMethodValue(String methodName,Object obj) {
		Object returnValue = null;
		try {
			String mName = "get" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
			Method method = obj.getClass().getMethod(mName, new Class[]{});
			returnValue = method.invoke(obj, new Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	/**
	 * 创建ZIP文件
	 * @param excelFileDir
	 * @param zipFile
	 */
	public static void createZipFile(String excelFileDir, File zipFile) {
		String zipFileDir = zipFile.getParent();
		File zipDirFile = new File(zipFileDir);
		if(!zipDirFile.exists()){
			zipDirFile.mkdirs();
		}
		LOG.info("~~~~~~~~~~zip file dir:" + zipFileDir);
		ZipOutputStream zipOutStream = null;
		try {
		    zipOutStream = new ZipOutputStream(new FileOutputStream(zipFile));
		    ZipEntry zipEntry = null;
			List<File> fileList = getFileListByDirect(excelFileDir);
			byte[] buff = new byte[2048];
			int readLen = 0;
			for(int i=0; i<fileList.size(); i++){
				File file = fileList.get(i);
				zipEntry = new ZipEntry(getRealFileName(excelFileDir, file));
				zipEntry.setSize(file.length());
				zipEntry.setTime(file.lastModified());
				zipOutStream.putNextEntry(zipEntry);
				InputStream inStream = new BufferedInputStream(new FileInputStream(file));
				while((readLen = inStream.read(buff)) != -1){
					zipOutStream.write(buff, 0, readLen);
				}
				inStream.close();
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		} finally {
			try {
				if (zipOutStream != null) {
					zipOutStream.closeEntry();
					zipOutStream.close();
				}
			} catch (IOException e) {
				LOG.info(e.getMessage(), e);
			}
		}
	}

	/**
	 * 读取ZIP文件
	 * @param zipFile
	 * @return
	 * @throws Exception
	 */
	public static byte[] readZipFile(File zipFile) throws Exception{
		if(zipFile.exists()){
			InputStream inStream = new FileInputStream(zipFile);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			int readLen = 0;
			byte[] buff = new byte[2048];
			while((readLen = inStream.read(buff)) != -1){
				outStream.write(buff, 0, readLen);
			}
			inStream.close();
			outStream.flush();
			outStream.close();
			byte[] byteArray = outStream.toByteArray();
			return byteArray;
		}
		return null;
	}

	/**
	 * 读取ZIP文件
	 * @param zipFile
	 * @return
	 */
	public static InputStream readZipFileStream(File zipFile) {
		InputStream inStream = null;
		try {
			if(zipFile.exists()){
				LOG.info("~~~~~~~~~~zipFile exists!");
				inStream = new FileInputStream(zipFile);
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
		return inStream;
	}

}
