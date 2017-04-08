package utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;

import au.com.bytecode.opencsv.CSVReader;

public class ReadExcelPOI {
	private static XSSFSheet sheet;
	private static Map<String,String> testinputData = getAllMapTestData();
	  /* 自定义方法来获得Excel指定表单的数据，封装为@DataProvider方法需要的类型Object[][]
	   * filePath:文件路径
	   * fileName:文件名称
	   * sheetName:表单名称 */
			
		public static Object[][] getTestData(String filePath, String fileName,String sheetName) throws IOException {

			File file = new File(filePath + "\\" + fileName);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook Workbook = null;
			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			if (fileExtensionName.equals(".xlsx")) {
				Workbook = new XSSFWorkbook(inputStream);
			} else if (fileExtensionName.equals(".xls")) {
				Workbook = new HSSFWorkbook(inputStream);
			}

			Sheet Sheet = Workbook.getSheet(sheetName);
			int rowCount = Sheet.getLastRowNum() - Sheet.getFirstRowNum();
			int colCount = Sheet.getRow(0).getLastCellNum();
			List<Object[]> records = new ArrayList<Object[]>();
			System.out.println("行数："+(rowCount+1));
			System.out.println("列数："+colCount);
			for (int i = 1; i < rowCount + 1; i++) {
				Row row = Sheet.getRow(i);
				String fields[] = new String[colCount];
				for (int j = 0; j < colCount; j++) {
					System.out.println("i="+i);
					System.out.println("j="+j);
					Cell cell=row.getCell(j);
					if (j>=row.getLastCellNum() || cell==null){
						fields[j] = "";
					}else{
						cell.setCellType(Cell.CELL_TYPE_STRING);
						fields[j] = cell.getStringCellValue();
					}
				}
				records.add(fields);
			}

			Object[][] results = new Object[records.size()][];
			for (int i = 0; i < records.size(); i++) {
				results[i] = records.get(i);
				System.out.println(results[i]);
			}
			return results;
		}
		
		
		/**
		 * 获取Excel文件路径以及测试数据所在的sheet页
		 * @param firepath Excel文件所在路径
		 * @param sheetname sheet页名
		 */
		private static void setPath(String firepath,String sheetname){
			
			try {
				FileInputStream fs = new FileInputStream(firepath);
				XSSFWorkbook workbook = new XSSFWorkbook(fs); 
				sheet = workbook.getSheet(sheetname);
				MyWebDriver.getLog().info("从Excel：“"+firepath+" "+sheetname+"sheet页"+"” 读取测试数据...");
			} catch (IOException e) {
				MyWebDriver.getLog().error("读取测试数据失败");
				e.printStackTrace();
			}
		}
		
		/**
		 * 获取行列值
		 * @param testcaseRow 测试用例所在行
		 * @param colum 测试用例数据所在列
		 * @return
		 */
		private static String getCellData(int testcaseRow,int colum){
			XSSFCell cell = sheet.getRow(testcaseRow).getCell(colum);
			String cellvalue =null;
			switch(cell.getCellType()){
			case XSSFCell.CELL_TYPE_NUMERIC:
				cellvalue = cell.getRawValue();
				break;
			case XSSFCell.CELL_TYPE_STRING:
				cellvalue = cell.getStringCellValue();
				break;
			case XSSFCell.CELL_TYPE_BLANK:
				cellvalue = "";
				break;
			default:
				MyWebDriver.getLog().warn("Excel中数据类型"+cell.getCellType()+"可能存在风险");
			}
			return cellvalue;
		}
		
		/**
		 * 获取指定 key的列值
		 * @param key  定义的用例标题
		 * @param colum 
		 * @return
		 */
		private static int getRowContains(String key,int colum){
			int i;
			int rowcount = sheet.getLastRowNum();
			for(i=1;i<rowcount;i++){
				if(ReadExcelPOI.getCellData(i, colum).equalsIgnoreCase(key)){
					break;
				}
			}
			if(i>=rowcount){
				MyWebDriver.getLog().error("没有找到想要的：“"+key+"”");
			}
			return i;
		}
		
		/**
		 * 通过第一列的key值读取第二列数据
		 * @param key
		 * @return
		 */
		public static String getTestdata(String key){
			ReadExcelPOI.setPath(Contants.path+Contants.filename, Contants.sheetname);
			int rowNum = ReadExcelPOI.getRowContains(key, Contants.keycolum);
			System.out.println(rowNum);
			String cellValue = ReadExcelPOI.getCellData(rowNum, Contants.colum);
			return cellValue;
		}
		
		/**
		 * 供内部类使用，hashmap方法读取数据
		 * @return
		 */
		private static Map<String,String> getAllMapTestData(){
			Map<String,String> mapData =new HashMap<String,String>();
			setPath(Contants.path+Contants.filename, Contants.sheetname);
			int rowNumber = sheet.getPhysicalNumberOfRows();
			XSSFRow row;
			XSSFCell cell1 = null;
			XSSFCell cell2 = null;
			for(int i=1;i<rowNumber;i++){
				cell1 = sheet.getRow(i).getCell(Contants.keycolum);
				if(cell1.getCellType()!=XSSFCell.CELL_TYPE_BLANK){
					String keyValue = cell1.getStringCellValue().toString().trim().toLowerCase();
					String value;
					cell2 = sheet.getRow(i).getCell(Contants.colum);
					switch(cell2.getCellType()){
					case XSSFCell.CELL_TYPE_NUMERIC:
						value = cell2.getRawValue();
						mapData.put(keyValue, value.toString().trim().toLowerCase());
						break;
					case XSSFCell.CELL_TYPE_STRING:
						value = cell2.getStringCellValue();
						mapData.put(keyValue, value.toString().trim().toLowerCase());
						break;
					case XSSFCell.CELL_TYPE_BLANK:
						value = "";
						mapData.put(keyValue, value);
						break;
					default:
						MyWebDriver.getLog().warn("Excel中数据类型"+cell2.getCellType()+"可能存在风险");
					}
				}
					
			}
			return mapData;
		}
		
		/**
		 * hashmap方法读取key值的数据
		 * @param key excel第一列的值
		 * @return
		 */
		public static String getMapData(String key){
			return testinputData.get(key.trim());
		}
		
		/**
		 * 获取csv文件的数据信息，内容为元素定位信息
		 * @return
		 */
		private static List<String[]> getLocatorsFromObjectsFile(){
			CSVReader csvReader;
			List<String[]> lists =new ArrayList<>();
					
			try {
					csvReader = new CSVReader(new FileReader(Contants.path + Contants.objectFile));
					lists = csvReader.readAll();
					csvReader.close();
				} catch (IOException e) {
					MyWebDriver.getLog().error("没有指定路径文件");
					e.printStackTrace();
				} 
					
			return lists;
		}
		
		/**
		 * 读取指定csv文件数据
		 * @return
		 */
		private static List<String[]> getCSVDataFromObjectsFile(){
			CSVReader csvReader;
			List<String[]> lists =new ArrayList<>();
					
			try {
					csvReader = new CSVReader(new FileReader(Contants.path + Contants.csvFileName));
					lists = csvReader.readAll();
					csvReader.close();
				} catch (IOException e) {
					MyWebDriver.getLog().error("没有指定路径文件");
					e.printStackTrace();
				} 
					
			return lists;
		}
		
		/**
		 * 通过key值读取元素定位信息
		 * @param key
		 * @return
		 */
		public static By getLocator(String key){
			By locat = null;
			String locator =null;
			String locatorType =null;
			for(String[] row:ReadExcelPOI.getLocatorsFromObjectsFile()){
				if(row[0].equalsIgnoreCase(key)){
					locatorType= row[1];
					locator =row[2];
					break;
				}
			}
			switch(locatorType){
			case "id":
				locat = By.id(locator);
				break;
			case "name":
				locat = By.name(locator);
				break;
			case "xpath":
				locat = By.xpath(locator);
				break;
			default:
				MyWebDriver.getLog().warn("没有找到元素类型，元素类型为：“"+locatorType+"”");
			}
			return locat;
		}
		
		/**
		 * 获取csv文件中的测试数据
		 * @param key 
		 * @return
		 */
		public static String getCSVTestData(String key){
			String vlaue = null;
			for(String[] row:ReadExcelPOI.getCSVDataFromObjectsFile()){
				if(row[0].equalsIgnoreCase(key)){
					vlaue = row[1];
				}
			}
			return vlaue;
		}
		
  }


