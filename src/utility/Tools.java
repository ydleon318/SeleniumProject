package utility;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;

public class Tools {
	private static Date dd;
	/**
	 *获取时间空间
	 * @return 返回201702251057时间格式
	 */
	public static String getCurrentTime(){
		Calendar ca = Calendar.getInstance();
		String y = String .valueOf(ca.get(Calendar.YEAR));
		String M = String .valueOf(ca.get(Calendar.MONTH));
		String d = String .valueOf(ca.get(Calendar.DATE));
		String h = String .valueOf(ca.get(Calendar.HOUR));
		String m = String .valueOf(ca.get(Calendar.MINUTE));
		String s = String .valueOf(ca.get(Calendar.SECOND));
		return y+d+h+m+s;
	}
	/**
	 * 获取时间“时分秒”
	 * @return 如105832
	 */
	public static String getTime() {
		String time = new SimpleDateFormat("HHmmss").format(new Date());
		return time;
	}
	
	/**
	 * 获取时间"年-月-日"
	 * @return 例如2017-02-25
	 */
	public static String getDate() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		return date;
	}
	
	/**
	 * 完全时间
	 * @return 年月日-时分秒毫秒
	 */
	public static String getAbosoluteTime() {
		String time = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
		return time;
	}
	
	
	/**
	 * 截屏控件
	 * @param be webdriver类型
	 * @return
	 */
	public static String screenShot(final MyWebDriver dr){
		String dir = "screenshot";
		dd = new Date();
		String time = getAbosoluteTime();
		System.out.println("目前的时间是:" + time);
		String screenShotPath = dir + File.separator + time + ".png";
		try {
			File sourceFile = ((TakesScreenshot) dr.getDriver()).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(sourceFile, new File(screenShotPath));
		} catch (Exception e) {
			e.printStackTrace();
			return "截图失败";
		}
		return screenShotPath;
	}
	
	
	/**
	 * 截屏控件，输出到指定带测试用例名称的文件夹当中
	 * @param sTestCaseName
	 * @return 
	 */
	public static String screenShot(String TestCaseName) {
		String time = getAbosoluteTime();
		System.out.println("目前的时间是:" + time);
		String screenShotPath = "screenshot"+ TestCaseName 
				+"/"+TestCaseName+ " # " + time + ".png";
		File file = ((TakesScreenshot) new MyWebDriver().getDriver()).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(file, new File(screenShotPath));

		} catch (Exception e) {
			MyWebDriver.getLog().error("截屏错误...");
		}
		return screenShotPath;
	}
	
}
