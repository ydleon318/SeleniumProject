package testcases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import utility.MyWebDriver;
import utility.ReadExcelPOI;

/**
* @author 作者 E-mail:
* @version 创建时间：2017年3月18日 下午4:37:16
* 类说明
*/
public class Demo12 {
	MyWebDriver dr;
	
	
	@BeforeClass
	public void seuUp(){
		dr = new MyWebDriver();
	}
	@AfterClass
	public void tearDown(){
		dr.closeURL();
	}
	@Test
	public void Login(){
		dr.open();

//		dr.type(ReadExcelPOI.getMapData("valid_username"), "//*[@name='account']");
//		dr.clickId("submit");
//		dr.clickText("行政办公");
//		dr.clickText("办公用品");
//		dr.clickText("办公用品管理");
//		dr.enterFrameByID("context");
//
//		dr.clickText("新增");
//		dr.type(ReadExcelPOI.getMapData("TestCase1_办公用品名称"), "//input[@name='pro_name']");
//		dr.type(ReadExcelPOI.getMapData("TestCase1_计量单位"), "//input[@name='pro_unit']");
//		dr.clickText("[添加]");
//		dr.selectWindow();
//		dr.enterFrameByXpath("//frame[@name='left']");
//		dr.clickText("信息管理部");
//		dr.ChangeFrame("//frame[@name='right']");
//		dr.clickText("管理员");
//
//		dr.selectWindow("78OA办公系统（免费版）");
//		dr.enterFrameByID("context");
//		
//		dr.clickXpath("//div[@id='_5_']/descendant::span[text()='[添加]']");
//		dr.selectWindow("部门选择");
//		dr.enterFrameByXpath("//frame[@name='head']");
//		dr.clickText("党总支");
//
//		dr.selectWindow("78OA办公系统（免费版）");
//		dr.enterFrameByID("context");
//		dr.clickText("保存");
		
	}

}
