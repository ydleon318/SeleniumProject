package testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class test1 {
	private WebDriver driver;
	
	@BeforeClass
	public void setup(){
		driver = new FirefoxDriver();
	}
	
	@Test
	public void test01(){
		driver.get("http://www.baidu.com");
	}

}
