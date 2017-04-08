package utility;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.Wait;



/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class MyWebDriver {
	private int stepInterval = Integer.parseInt(ReadSetting.StepInterval);
	private WebDriver driver ;
	private boolean acceptNextAlert = true;
	private boolean boolWaitEachStep;
	private int retry;
	JavascriptExecutor javaScriptExecutor;
	private WebDriverWait wait;
	private int timeout = Integer.parseInt(ReadSetting.timeout);
//-------------------------------log4j日志文件配置---------------------------	
	private static Logger log = Logger.getLogger(MyWebDriver.class.getName());
	public static Logger getLog() {
		return log;
	}

	public static void setLog(final Logger newLog) {
		log = newLog;
	}
	
	public WebDriver getDriver() {
		return driver;
	}
//--------------------------设置driver部署执行方式-------------------------	
	/**
	 * 使用maven的pom配置执行多浏览器执行
	 * @param url 打开的网址
	 * @param browser 使用的浏览器
	 */
	public void SetDriver(){
		String browser = System.getProperty("pom.browser");
		switch(browser){
		case "firefox":
			driver = new FirefoxDriver();
			log.info("使用firefox浏览器执行");
			break;
		case "ie":
			System.setProperty("webdriver.ie.driver", ReadSetting.IEDriverPath);
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(capabilities);
			log.info("使用IE浏览器执行");
			break;
		case "chrome":
			System.setProperty("webdriver.chrome.driver", ReadSetting.ChromeDriverPath);
			driver = new ChromeDriver();
			log.info("使用Chrome浏览器执行");
		default:
			log.warn("没能找到浏览器类型，你调用的浏览器类型为： “"+browser+"”！");
			break;
		}	
		this.wait = new WebDriverWait(this.driver, 30); 
		boolWaitEachStep = false;
		retry = 1;
		javaScriptExecutor = (JavascriptExecutor) driver;
	}
	
	/**
	 * 使用pop文件配置多浏览器执行
	 */
	public void setDriver() {
		if (ReadSetting.WebDriverType ==1) {
			driver = new FirefoxDriver();
			log.info("使用firefox浏览器执行");
		}
		if (ReadSetting.WebDriverType == 2) {
			System.setProperty("webdriver.chrome.driver", ReadSetting.ChromeDriverPath);
			driver = new ChromeDriver();
			log.info("使用Chrome浏览器执行");
		}
		if (ReadSetting.WebDriverType == 3) {
			System.setProperty("webdriver.ie.driver", ReadSetting.IEDriverPath);
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(capabilities);
			log.info("使用IE浏览器执行");
		}
		if(ReadSetting.WebDriverType == 10){
			DesiredCapabilities ffDesiredcap = DesiredCapabilities.firefox(); 
			try {
				driver = new RemoteWebDriver(new URL(ReadSetting.HubUrl), ffDesiredcap);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			log.info("使用FireFox浏览器远程执行");
		}
		if(ReadSetting.WebDriverType == 20){
			DesiredCapabilities CRDesiredcap = DesiredCapabilities.chrome(); 
			try{
				driver = new RemoteWebDriver(new URL(ReadSetting.HubUrl),CRDesiredcap);
			}catch(MalformedURLException e){
				e.printStackTrace();
			}
			log.info("使用Chrome浏览器远程执行");
		}
		if(ReadSetting.WebDriverType == 30){
			DesiredCapabilities IEDesiredcap = DesiredCapabilities.internetExplorer();
			try{
				driver = new RemoteWebDriver(new URL(ReadSetting.HubUrl),IEDesiredcap);
			}catch(MalformedURLException e){
				e.printStackTrace();
			}
			log.info("使用IE浏览器远程执行");
		}
		this.wait = new WebDriverWait(this.driver, 30); 
		boolWaitEachStep = false;
		retry = 1;
		javaScriptExecutor = (JavascriptExecutor) driver;
	}
	
	/**
	 * 构造方法调用setDriver与窗口最大化
	 */
//	public MyWebDriver(){
//		setDriver();
//		maxDriverScreen(); 	
//	}
	public MyWebDriver(){
		SetDriver();
		maxDriverScreen(); 	
	}
	
//----------------------以下方法主要是判断元素存在与否的断言等-------------------------------------------------------------	
	
	
	/**
	 * 等待元素能够点击
	 * @param xpath 元素
	 */
	private void waitClickable(final String xpath){
		try{
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			log.info("元素： “"+xpath+"” 已找到");
		}catch(Exception e){
			log.info("元素： “"+xpath+"” 没有找到...");
		}
	}
	
	/**
	 * 点击能够被点击的元素
	 * @param xpath 元素的xpath
	 * @param startTime 开始等待时间
	 * @param timeout 超时时间
	 * @throws Exception 
	 */
	private void clickTheClickable(final String xpath, final long startTime, final int timeout) throws Exception {
		try {

			driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeout) {
				log.info("你要找的元素： “" + xpath + "” 未找到...");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				log.info("重新查找元素： “" + xpath + "” ...");
				clickTheClickable(xpath, startTime, timeout);
			}
		}
	}
	
	/**
	 * 期望元素存在或者不存在
	 * @param expectExist 期望类型，true或者false
	 * @param xpath 元素xpath
	 * @param timeout 超时时间
	 */
	@SuppressWarnings("deprecation")
	public void expectElementExistOrNot(final boolean expectExist, final String xpath, final int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isElementPresent(xpath, -1);
					}
				}.wait("xpath元素： “"+ xpath+"” 没有找到  " , timeout);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("期望出现的元素： “" + xpath+"” 没有找到...");
				handleFailure("无法找到元素：“" + xpath+"”... ");
			}
		} else {
			if (isElementPresent(xpath, timeout)) {
				log.info("找到不希望出现的元素 : “" + xpath+"”");
				handleFailure("找到不希望出现的元素 “" + xpath+"”... ");
			} else {
				log.info("没有找到不希望出现的元素： “" + xpath+"”");
			}
		}
	}
	
	/**
	 * 期望文本是否出现
	 * @param expectExist 期望类型，true或者false
	 * @param text 期望的文本
	 * @param timeout 超时时间
	 */
	public void expectTextExistOrNot(final boolean expectExist, final String text, final int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isTextPresent(text, -1);
					}
				}.wait("没有找到文本： “ " + text+"”", timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("期望的文本： “" + text +"” 没有找到...");
			}
		} else {
			if (isTextPresent(text, timeout)) {
				handleFailure("找到不希望出现的文本： “" + text +"”...");
			} else {
				log.info("不希望出现的文本： “" + text+"” 没有找到");
			}
		}
	}
	
	/**
	 * 判断文本信息是否存在
	 * @param text 文本信息
	 * @return boolean类型
	 */
	public boolean isTextPresent(final String text) {
		String Xpath = "//*[contains(.,\'" + text + "\')]";
		try {
			driver.findElement(By.xpath(Xpath));
			log.info("找到文本： "+text);
			return true;
		} catch (NoSuchElementException e) {
			log.info("没有找到文本： " + text);
			return false;
		}
	}
	
	/**
	 * 判断文本信息是否存在（带等待时间）
	 * @param text 文本信息
	 * @param time 等待时间
	 * @return
	 */
	public boolean isTextPresent(final String text, final int time) {
		pause(time);
		boolean isPresent = isTextPresent(text);
		if (isPresent) {
			log.info("找到文本： "+text);
			return true;
		} else {
			log.info("没有找到文本： " + text);
			return false;
		}
	}
	
	/**
	 * 判断元素是否存在（带等待时间）
	 * @param xpath 被判断的元素
	 * @param time 等待时间
	 * @return boolean类型
	 */
	public boolean isElementPresent(final String xpath, final int time) {
		pause(time);
		boolean isPresent = isElementPresent(xpath) && driver.findElement(By.xpath(xpath)).isDisplayed();
		if (isPresent) {
			log.info("找到元素： “" + xpath+"”");
			return true;
		} else {
			log.info("没有找到元素： “" + xpath+"”");
			return false;
		}
	}
	
	/**
	 * 判断元素是否存在
	 * @param xpath 被判断的元素
	 * @return boolean类型
	 */
	public boolean isElementPresent(final String xpath) {
		if(driver.findElements(By.xpath(xpath)).size() > 0){
			return true;
		}else{
			log.info("没有找到元素： “"+ xpath +"”...");
			return false;
		}
	}
	
	/**
	 * 判断Alert是否出现并且切换
	 * @return boolean类型
	 */
	public boolean isAlertPresents(){
		pause(1500);
		try{
			driver.switchTo().alert();
			log.info("切换到弹出的Alert");
			return true;
		}catch(NoAlertPresentException e){
			log.info("Alert没有找到");
			return false;
		}
	}
	/**
	 * 断言True或者false
	 * 
	 * @param isTrue 断言true或者false
	 */
	private void assertTrue(final boolean isTrue) {
		try {
			Assert.assertTrue(isTrue);
			log.info("断言通过");
		} catch (AssertionError e) {
			log.warn("断言失败，测试用例Failed");
			Assert.assertTrue(isTrue);
		}
	}
	
	
	/**
	 * 断言xpath
	 * @param xpath 元素
	 */
	public void assertXpath(final String xpath){
		log.info("开始断言... "+xpath);
		assertTrue(isElementPresent(xpath));
	}
	
	/**
	 * 通过id断言
	 * @param id 被断言的元素id
	 */
	public void assertID(String id){
		log.info("准备断言id: “"+id+"” 是否存在");
		String xpath = "//*[@id='" + id + "']";
		assertXpath(xpath);
	}
	
	/**
	 * 断言文本信息
	 * @param text 被检查的文本
	 */
	public void assertText(String text){
		log.info("准备断言文本: “"+ text +"” 是否存在");
		String xpath = "//*[contains(text(),'"+ text +"')]";
		assertXpath(xpath);
	}
	
	
	
	/**
	 * 通过id等待
	 * @param id 元素的id
	 * @return 返回webelement类型
	 */
	public WebElement waitById(final String id) {
		waitJsReady();
		return (new WebDriverWait(driver, timeout/1000)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(final WebDriver d) {
				WebElement e = d.findElement(By.id(id));
				if (e.isEnabled()) {
					log.info("需要的元素id： “"+ id +"” 已经找到");
					return e;
				}else
					log.warn("需要的元素id： “"+ id +"” 没有找到");
					handleFailure("元素： “" + id +"” 没找到...");
					return null;
			}
		});
	}
	
	/**
	 * 通过xpath等待
	 * @param xpath 元素xpath
	 * @return WebElement返回类型
	 */
	public WebElement waitByXpath(final String xpath) {
		waitJsReady();
		return (new WebDriverWait(driver, timeout/1000)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(final WebDriver d) {
				WebElement e = d.findElement(By.xpath(xpath));
				if (e.isEnabled()) {
					log.info("需要的元素xpath：  “"+ xpath +"” 已经找到");
					return e;
				}else
					log.warn("需要的元素xpath： “"+ xpath +"” 没有找到");
					handleFailure("元素： “" + xpath+"” 没找到...");
					return null;
			}
		});
	}
	
	/**
	 * 判断xpath是否存在
	 * @param xpath 元素xpath
	 * @param second 等待时间
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayXpath(final String xpath, final int second){
		setTimeout(second);
		try{
			WebElement elem = waitByXpath(xpath);
			setTimeout(second);
			return (elem!=null && elem.isDisplayed());
		}
		catch(TimeoutException e){
			setTimeout(second);
			return false;
		}
	}
	
	/**
	 * 判断文本信息是否存在
	 * @param text 被判断的文本信息
	 * @param second 等待时间（秒为单位）
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayText(final String text, final int second){
		log.info("判断文本： “"+ text + "” 是否存在");
		return isElemDisplayXpath("//*[text()='" + text + "']", second);
	}
	
	/**
	 * 判断包含的文本信息是否存在
	 * @param text 包含的文本信息
	 * @param second 等待时间（秒为单位）
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayTextContain(final String text, final int second){
		log.info("判断文本： “"+ text + "” 是否存在");
		return isElemDisplayXpath("//*[contains(text(),'" + text + "')]", second);
	}
	
	/**
	 * 判断元素id是否存在
	 * @param id 元素id
	 * @param second 等待时间（秒为单位）
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayId(final String id, final int second){
		log.info("判断文本： “"+ id + "” 是否存在");
		return isElemDisplayXpath("//*[@id='"+id+"']", second);
	}
	
	/**
	 * 判断元素xpath是否存在
	 * @param xpath 元素xpath
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayXpath(final String xpath){
		log.info("判断文本： “"+ xpath + "” 是否存在");
		return isElemDisplayXpath(xpath, 5);
	}
	
	/**
	 * 判断元素id是否存在（固定时间）
	 * @param id 元素id
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayId(final String id){
		log.info("判断文本： “"+ id + "” 是否存在");
		return isElemDisplayXpath("//*[@id='"+id+"']", 5);
	}
	
	/**
	 * 判断文本信息是否存在（固定时间）
	 * @param text 被判断的文本
	 * @return boolean返回类型
	 */
	public boolean isElemDisplayText(final String text){
		log.info("判断文本： “"+ text + "” 是否存在");
		return isElemDisplayXpath("//*[text()='" + text + "']", 5);
	}
	
	
//-------------------------------操作Frame控件--------------------------------------------------------
	/**
	 * 通过frame的xpath进入指定frame
	 * @param xpath 需要切换的frame的xpath
	 */
	public void enterFrameByXpath(final String xpath) {
		pause(1000);
		try {
			driver.switchTo().frame(waitByXpath(xpath));
			log.info("成功通过xpath： “" + xpath+ "” 切换frame");
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			log.info("切换frame失败");
		}
	}
	
	/**
	 * 从frame中离开
	 */
	public void leaveFrame() {
		pause(1000);
		try{
			driver.switchTo().defaultContent();
			log.info("成功离开frame...");
		}catch(Exception e){
			log.warn("离开frame失败");
		}
		
	}
	
	/**
	 * 通过frame的id切换frame
	 * @param id frame的id
	 */
	public void enterFrameByID(final String id){
		try{
			log.info("通过frame的id: “"+ id +"” 切换frame...");
			enterFrameByXpath("//*[@id='"+ id + "']");
		}catch(NoSuchElementException e){
			log.info("切换frame失败");
		}
	}
	
	/**
	 * 通过frame的xpath切换frame（一步完成）
	 * @param xpath 想要切换到的frame的xpath
	 */
	public void ChangeFrame(final String xpath){
		pause(1000);
		leaveFrame();
		enterFrameByXpath(xpath);
	}
	
	
//-------------------------以下是页面操作控件，如open，click等-----------------------------------------------------------
	
	/**
	 * 窗口最大化操作
	 */
	private void maxDriverScreen() {
		log.info("最大化窗口..");
		this.driver.manage().window().maximize();
		this.driver.manage().timeouts().implicitlyWait(Integer.parseInt(ReadSetting.timeout), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 打开网页ַ
	 * @param url 网址ַ
	 */
	public void open(){
		String url = System.getProperty("pom.url");
		pause(stepInterval);
		try{
			driver.get(url);
			log.info("打开网页： "+url);
		}catch(Exception e){
			e.printStackTrace();
			log.info("网页打开失败");
			handleFailure("无法打开网页： "+url);
		}

	}
	
	/**
	 * 暂停1000毫秒
	 */
	public void pause(){
		waitJsReady();
	}
	
	/**
	 *暂停millisecond时间
	 * @param millisecond 毫秒单位
	 */
	public void pause(final int millisecond){
		waitJsReady(millisecond);
	}
	

	/**
	 * 点击操作（有超时时间）
	 * @param xpath 被点击元素的xpath
	 * @param timeout 超时时间
	 */
	public void click(String xpath,int timeout){
		pause(stepInterval);
		waitClickable(xpath);
		expectElementExistOrNot(true, xpath, timeout);
		try {
			clickTheClickable(xpath, System.currentTimeMillis(), 5000);
			log.info("点击元素： “" + xpath +"”");
			} catch (Exception e) {
				e.printStackTrace();
				log.info(xpath+" 没有找到");
				handleFailure("点击的元素：  “"+xpath+"” 没有找到...");
			}
	}
	
	/**
	 * 通过xpath点击
	 * @param xpath 
	 */
	public void click(String xpath){
		pause(1000);
		waitClickable(xpath);
		expectElementExistOrNot(true, xpath, timeout);
		try {
			clickTheClickable(xpath, System.currentTimeMillis(), 5000);
			log.info("成功点击元素： “" + xpath +"” ！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info(xpath+ " 没有找到");
			handleFailure("被点击元素：  “"+xpath+"”  没有找到...");
		}	
	}
	
	/**
	 * 通过文本信息进行click
	 * @param text 文本信息
	 */
	public void clickText(final String text) {
		String xpath = "//*[text()='"+text+"']";
		log.info("准备点击： “"+ text+"”");
		click(xpath);
	}
	
	/**
	 * 通过元素xpath进行click
	 * @param xpath 元素xpath
	 */
	public void clickXpath(final String xpath){
		log.info("准备点击xpath： “"+ xpath +"”");
		click(xpath);
	}
	
	/**
	 * 通过包含的文本信息click操作
	 * @param text 被包含的文本信息
	 */
	public void clickTextContains(final String text) {
		String xpath = "//*[contains(text(),'"+text+"')]";
		log.info("准备点击所包含的文本： “"+ text+"”");
		click(xpath);
	}
	
	/**
	 * 通过元素id进行点击操作
	 * @param id 元素id
	 */
	public void clickId(final String id) {
		String xpath = "//*[@id='"+id+"']";
		log.info("准备点击id： “"+ id +"”");
		click(xpath);
	}
	
	/**
	 * 通过divid模糊定位，之后再定位该结点以下的元素id
	 * @param divid
	 * @param id
	 */
	public void clickByDividID(String divid,String id){
		String xpath = setDiv(divid)+"/descendant::*[@id='"+id+"']";
		log.info("准备点击Div id为： “"+divid+"”之下元素ID为： “"+id+"” 的元素");
		click(xpath);
	}
	
	/**
	 * 通过divid模糊定位，之后再定位该结点以下的文本
	 * @param divid
	 * @param text
	 */
	public void clickByDividText(String divid,String text){
		String xpath = setDiv(divid)+"/descendant::*[text()='"+text+"']";
		log.info("准备点击Div id为： “"+divid+"”之下的文本： “"+text+"”");
		click(xpath);
	}
	
	/**
	 * 通过divid模糊定位，之后再定位该结点以下的文本（包含就可以）
	 * @param divid
	 * @param text 包含的文本
	 */
	public void clickByDividContainText(String divid,String text){
		String xpath = setDiv(divid)+"/descendant::*[contains(text(),'"+text+"')]";
		log.info("准备点击Div id为： “"+divid+"”之下的文本： “"+text+"”");
		click(xpath);
	}
	
	/**
	 * 通过divid模糊定位，之后再定位该结点以下的xpath
	 * @param divid
	 * @param xpath xpath路径，最好不带"//"
	 */
	public void clickByDividXpath(String divid,String xpath){
		String Xpath = "";
		if(xpath.contains("//")){
			xpath = xpath.split("//")[1];
		}
		Xpath = "/descendant::"+xpath;
		log.info("准备点击Div id为： “"+divid+"”之下的元素xpath： “"+xpath+"”");
		click(Xpath);
	}
	
	/**
	 * 想输入框输入信息
	 * @param text 想要输入的文本信息
	 * @param xpath 输入框的xpath
	 */
	public void type(final String text,final String xpath){
		expectElementExistOrNot(true, xpath, timeout);
		WebElement we = waitByXpath(xpath);
		try {
			we.clear();
		} catch (Exception e) {
			log.warn("清除输入框  ：“" + xpath +"” 失败");
		}
		try {
			log.info("向输入框： “"+ xpath +"” 中输入：“"+ text+"”");
			we.sendKeys(text);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("向输入框：  “"+xpath+"” 输入失败...");
			
		}
		
	}
	/**
	 * 通过输入框的id查找输入框并输入信息
	 * @param id 输入框的id
	 * @param text 输入的文本信息
	 */
	public void typeId(final String id, final String text) {
		String xpath = "//*[@id='"+id+"']";
		log.info("准备通过输入框id： “"+ id +"” 定位输入框并输入 “"+text+"”");
		type(text, xpath);
	}
	
	
	/**
	 * 通过text选择列表选项
	 * @param text 列表文本
	 * @param xpath 元素
	 */
	public void selectByText(final String text,final String xpath){
		try {
			WebElement element = waitByXpath(xpath);
			Select select = new Select(element);
			log.info("选中列表： “" + text+"”");
			select.selectByVisibleText(text);
		} catch (Exception e) {
			log.info("无法选中列表： “"+text+"”");
			e.printStackTrace();
			handleFailure("选择列表： “" + text+"” 失败...");
		}
		
	}
	
	/**
	 * 通过index选择列表选项
	 * @param xpath 列表框的xpath	
	 * @param index 列表的位置，从0开始
	 */
	public void selectByIndex(final String xpath,final int index){
		try {
			WebElement element = waitByXpath(xpath);
			Select select = new Select(element);
			select.selectByIndex(index);
			log.info("选择了第： “" + index+1 +"” 个列表");
		} catch (Exception e) {
			log.info("选择的第： “"+ index+1 +"” 个列表没有找到...");
			e.printStackTrace();
			handleFailure("选择的第： “" + index+1 +"” 个列表失败...");
		}
	}
	
	/**
	 * 关闭浏览器
	 */
	public void closeURL(){
		pause(stepInterval);
		this.setWaitEachStep(false);
		try {
			    log.info("关闭浏览器");
		    	driver.quit();
		} catch (Exception e) {
			log.info("关闭浏览器失败");
			e.printStackTrace();
			handleFailure("无法关闭浏览器...");
		}
			
	}
	
	/**
	 * 获取指定xpath的text
	 * @param xpath 元素
	 * @return 返回文本信息
	 */
	public String getXpathText(final String xpath){
		String value="";
		WebElement elem = waitByXpath(xpath);
		value = elem.getText();
		log.info("获取： “"+xpath+"” 的文本");
		return value;
	}
	
	/**
	 * 获取指定id元素的text
	 * @param id 元素的id
	 * @return 返回文本信息
	 */
	public String getIdText(final String id){
		log.info("准备通过： “"+id+"” 获取文本");
		return getXpathText("//*[@id='"+id+"']");
	}
	
	/**
	 * 获取指定xpath的属性信息ֵ
	 * @param xpath 置顶元素的xpath
	 * @param attribute 元素属性信息ֵ
	 * @return
	 */
	public String getXpathAttribute(final String xpath, final String attribute){
		String value="";
		WebElement elem = waitByXpath(xpath);
		value = elem.getAttribute(attribute);
		log.info("获取元素属性  “"+ attribute+"”");
		return value;
	}
	
	/**
	 * 获取指定id的属性信息ֵ
	 * @param id 元素
	 * @param attribute 元素属性ֵ
	 * @return
	 */
	public String getIdAttribute(final String id, final String attribute){
		return getXpathAttribute("//*[@id='"+id+"']", attribute);
	}
	
	
	/**
	 * 获窗口的title
	 * @return 窗口的title
	 */
	public String getTitle(){
		String title = "";
		try{
			title = driver.getTitle();
			log.info("获取窗口title “"+ title+ "”");
		}catch(Exception e){
			e.printStackTrace();
			log.info("获取窗口title失败");
			handleFailure("获取title失败...");
		}
		return title;
	}
	
	/**
	 * 刷新操作
	 */
	public void reFresh() {
		pause(stepInterval);
		driver.navigate().refresh();
		log.info("刷新页面");
	}
	
	/**
	 * 通过窗口title切换窗口
	 * @param windowTitle 想要切换的窗口的title
	 * @return 窗口handles集合
	 */
	public Set<String> selectWindow(final String windowTitle){
		String currentHandle="";
		try{
			currentHandle = driver.getWindowHandle();
			Set<String> handles = driver.getWindowHandles();
				for (String s : handles) {
					if (s.equals(currentHandle))
						continue;
					else {
						driver.switchTo().window(s);
						pause(500);
						if (driver.getTitle().contains(windowTitle)) {
							log.info("切换窗口: “" + windowTitle + "” 成功！");
							break;
						} else
							continue;
					}
				}
		
			}catch(NoSuchWindowException e){
				e.printStackTrace();
				log.info("切换窗口: “" + windowTitle + "” 失败！");
				handleFailure("切换窗口 “" + windowTitle+"” 失败...");
			}
			return driver.getWindowHandles();
		}
	
	 /**
	  * 通过窗口title切换窗口
	 * @param windowTitle 想要切换的窗口的title
	 * @return boolean类型
	 */
	public boolean switchToWindow(String windowTitle){    
	        boolean flag = false;    
	        try {   
	            String currentHandle = driver.getWindowHandle();    
	            Set<String> handles = driver.getWindowHandles();    
	            for (String s : handles) {    
	                if (s.equals(currentHandle))    
	                    continue;    
	                else {    
	                    driver.switchTo().window(s);  
	                    if (driver.getTitle().contains(windowTitle)) {    
	                        flag = true;    
	                        log.info("切换窗口: “" + windowTitle + "” 成功！");  
	                        break;    
	                    } else    
	                        continue;    
	                }    
	            }    
	        } catch (Exception e) {    
	        	log.info("切换窗口: “" + windowTitle + "” 失败！");
				handleFailure("切换窗口 “" + windowTitle+"” 失败...");
	            flag = false;    
	        }    
	        return flag;    
	    }
	
	/**
	 * 切换窗口（适用于两个窗口）
	 */
	public void selectWindow(){
		String ori = driver.getWindowHandle();
		Set<String> all = driver.getWindowHandles();		
		Iterator<String> its = all.iterator();
		while(its.hasNext()){
		String cwh = its.next();		
		if(!cwh.equals(ori)){	
			driver.switchTo().window(cwh);
			
			log.info("已经切换到新开启的窗口...");   
			break;
			}
		}
	}
	
	/**
	 * 窗口回退操作
	 */
	public void Back(){
		pause(stepInterval);
		try{
			driver.navigate().back();
			String title = getTitle();
			log.info("返回到窗口： “"+ title +"” 界面");
		}catch(Exception e){
			log.info("窗口回退失败");
			handleFailure("窗口回退失败...");
		}
		pause(stepInterval);
	}
	
	/**
	 * 窗口前进操作
	 */
	public void forward(){
		pause(stepInterval);
		try{
			driver.navigate().forward();
			String title = getTitle();
			log.info("前进到窗口： “"+ title +"” 界面�");
		}catch(Exception e){
			log.info("窗口前进失败");
			handleFailure("窗口前进失败...");
		}
		pause(stepInterval);
	}
	
	/**
	 * 关闭当前窗口
	 */
	public void closeWindow(){
		pause(stepInterval);
		try{
			driver.close();
			log.info("成功关闭当前窗口");
		}catch(Exception e){
			e.printStackTrace();
			handleFailure("窗口关闭失败...");
		}
	}
	
//----------------------------以下是操作Alert控件---------------------------------------------------------------------
		/**
		 * 关闭alert并得到文本信息
		 * @return string 得到的alert文本
		 */
		public String closeAlertAndGetItsText() {
			pause(stepInterval);
		    try {
		      Alert alert = driver.switchTo().alert();
		      String alertText = alert.getText();
		     
		      if (acceptNextAlert) {
		        alert.accept();
		      } else {
		        alert.dismiss();
		      }
		      pause(stepInterval);
		      log.info("得到Alert文本 “"+alertText+"”" );
		      return alertText;
		    } finally {
		      acceptNextAlert = true;
		    }
		    
		  }
		
		/**
		 * 关闭alert
		 */
		public void closeAlter(){
			pause(stepInterval);
			try{
				driver.switchTo().alert().accept();
				log.info("通过确定关闭Alert");
			}catch(Exception e){
				e.printStackTrace();
				log.info("关闭Alert失败");
				handleFailure("关闭Alert失败...");
			}
		}
		
//----------------------------以下适用于封装的控件----------------------------------------------------------------
	/**
	 * 记录小步骤
	 * @param text 步骤信息
	 */
	public void step(final String text) {
		String step = "步骤--> " + text;
		log.info(step);
		Reporter.log(step);
	}
	
	/**
	 * 步骤是否有间隔
	 * @param flag
	 */
	private void setWaitEachStep(final boolean flag){
		log.info("步骤是否有间隔 = " + flag);
		this.boolWaitEachStep = flag;
	}
	
	/**
	 * 设置超时时间
	 * @param second 超时时间，以秒为单位
	 */
	public void setTimeout(final int second){
		this.timeout = second*1000;
		this.wait = new WebDriverWait(this.driver, second);
	}
	
	/**
	 * 等待js加载完成
	 */
	public void waitJsReady(){
		try{
			wait.until(new ExpectedCondition<Boolean>(){
				public Boolean apply(final WebDriver d){
					JavascriptExecutor js = (JavascriptExecutor)d;
					return (Boolean)js.executeScript("return (jQuery)&&(document.readyState == \"complete\")");//(jQuery.active == 0)&&
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			wait.until(new ExpectedCondition<Boolean>(){
				public Boolean apply(final WebDriver d){
					JavascriptExecutor js = (JavascriptExecutor)d;
					return (Boolean)js.executeScript("return (jQuery)&&(document.readyState == \"complete\")");//(jQuery.active == 0)&&
				}
			});
		}
		catch(org.openqa.selenium.WebDriverException we){
			try {
				Thread.sleep(1000);
				log.info("等待浏览器加载完成...");
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			retry--;
			if(retry > 0){
				waitJsReady();
			}
		}
	}
	
	/**
	 * 等待JS加载完成
	 * @param millisecond 等待时间（毫秒单位）
	 */
	public void waitJsReady(final int millisecond){
		try{
			wait.until(new ExpectedCondition<Boolean>(){
				public Boolean apply(final WebDriver d){
					JavascriptExecutor js = (JavascriptExecutor)d;
					return (Boolean)js.executeScript("return (jQuery)&&(document.readyState == \"complete\")");//(jQuery.active == 0)&&
				}
			});
			if(millisecond>0){
				try {
					Thread.sleep(millisecond);
					//log.info("JsReady() " + millisecond + " ms");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			wait.until(new ExpectedCondition<Boolean>(){
				public Boolean apply(final WebDriver d){
					JavascriptExecutor js = (JavascriptExecutor)d;
					return (Boolean)js.executeScript("return (jQuery)&&(document.readyState == \"complete\")");//(jQuery.active == 0)&&
				}
			});
		}
		catch(org.openqa.selenium.WebDriverException we){
			
			try {
				Thread.sleep(500);
				log.info("等待浏览器加载完成...");
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			retry--;
			if(retry > 0){
				waitJsReady(millisecond);
			}
		}
	}
	/**
	 * 获取时间
	 * @return 时间格式：时分秒
	 */
	public String getTime(){
		String time = Tools.getTime();
		log.info("获取当前时间：时分秒");
		return time;
	}
	
	/**
	 * 高亮显示页面元素
	 * @param dr webdriver类型
	 * @param element webelement类型
	 */
	public void highlightElement(WebElement element) {
		WrapsDriver wra= (WrapsDriver)element;		 
		String styles = "color:green;border:2px solid yellow��";
		JavascriptExecutor jse = (JavascriptExecutor)wra.getWrappedDriver();
		String js1 = "arguments[0].setAttribute('style',arguments[1])��";
		jse.executeScript(js1,element, styles);
		
	}
	
	/**
	 * 错误处理控件，附带截图，适用于项目二次开发时使用
	 * @param notice 想要输出的错误信息
	 * @param TestCaseName测试用例名称
	 */
	public void handleFailure(final String notice,final String TestCaseName){
		String png = Tools.screenShot(TestCaseName);
		String logStrings = notice + " 进行截图处理"
				+ png;
		Reporter.log(logStrings + png );
		log.error(logStrings);
		Assert.fail(logStrings);
	}
	/**
	 * 错误处理控件，附带截图
	 * @param notice 想要输出的错误信息
	 */
	public void handleFailure(final String notice){
		String png = Tools.screenShot(this);
		String logStrings = notice + " 进行截图处理"
				+ png;
		Reporter.log(logStrings + png );
		log.error(logStrings);
		Assert.fail(logStrings);
	}
	
	/**
	 * 设置divid
	 * @param divid div结点后面的id属性
	 * @return
	 */
	private String setDiv(String divid){
		String xpath = "//div[@id='"+divid+"']";
		return xpath;
	}

//-------------------------------以下是模拟键盘鼠标操作--------------------------------
	/**
	 * 输入数字代表的键盘字符（需要对应查找）
	 * @param keyCode 
	 */
	public void pressKeyboard(final int keyCode) {
	   pause(stepInterval);
	   Robot rb = null;
	   try {
			rb = new Robot();
	   } catch (AWTException e) {
			e.printStackTrace();
	   }
	    try{
	    	rb.keyPress(keyCode); 
	    }catch(IllegalArgumentException e){
	    	log.warn("输入的数字: "+keyCode+" 与键值不匹配");
	    	e.printStackTrace();
	    }
	    rb.delay(100); 
		rb.keyRelease(keyCode); 
	    log.info("输入了：  “" + keyCode+"” 代表的键值");
	}
	
	/**
	 * 模拟按回车键
	 */
	public void pressEnter() {
		pause(stepInterval);
		Robot rb = null;
		try {
			rb = new Robot();
			rb.keyPress(KeyEvent.VK_ENTER); 
			rb.delay(100);
			rb.keyRelease(KeyEvent.VK_ENTER);
			log.info("按下ENTER键");
		}catch (AWTException e) {
			log.info("按ENTER键失败");
			e.printStackTrace();
			handleFailure("按ENTER键失败...");
		}
	
	}
	
	/**
	 * 模拟按下Tab键
	 */
	public void pressTab() {
		pause(stepInterval);
		Robot rb = null;
		try {
			rb = new Robot();
			rb.keyPress(KeyEvent.VK_TAB); 
			rb.delay(100); 
			rb.keyRelease(KeyEvent.VK_TAB); 
			log.info("按下Tab键");
		} catch (AWTException e) {
			log.info("按下Tab键失败");
			e.printStackTrace();
			handleFailure("按下Tab键失败...");
		}
		
	}
	
	/**
	 * 模拟键盘输入
	 * @param text 想要输入的文本
	 */
	public void inputKeyboard(final String text) {
		String cmd = System.getProperty("user.dir") + "\\driver_res\\SeleniumCommand.exe" + " sendKeys " + text;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			log.info("模拟键盘输入： “" + text +"”");
			p.waitFor();
		} catch (Exception e) {
			handleFailure("模拟键盘输入： “" + text +"” 失败");
			e.printStackTrace();
		} finally {
			p.destroy();
		}
		
	}
	
	/**
	 * 模拟鼠标双击
	 * @param xpath 想要双击的元素xpath
	 */
	public void doubleClick(final String xpath) {
		pause(stepInterval);
		try {
			new Actions(driver).doubleClick(waitByXpath(xpath)).perform();
			log.info("双击元素： “" + xpath +"”");
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("双击元素： “"+xpath+"” 失败...");

		}
	}
	
	/**
	 * 点击并保持
	 * @param xpath 被点击的元素xpath
	 */
	public void clickAndHold(final String xpath) {
		pause(stepInterval);
		try {
			new Actions(driver).clickAndHold(waitByXpath(xpath)).perform();
			log.info("点击元素： “" + xpath +"”");
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("点击元素： “"+xpath+"” 失败...");
		}	
	}
	
	/**
	 * 模拟鼠标右键点击
	 * @param xpath 被点击元素的xpath
	 */
	public void clickByRight(final String xpath){
		pause(stepInterval);
		try{
			new Actions(driver).contextClick(waitByXpath(xpath)).perform();
			log.info("右键点击元素： “" + xpath +"”");
		} catch (Exception e){
			e.printStackTrace();
			handleFailure("右键点击元素： “" + xpath +"” 失败...");
		}
	}
	
	/**
	 * 鼠标移动至指定xpath上悬浮
	 * @param xpath 目标元素 Xpath
	 */
	public void MouseMoveToXpath(final String xpath){
		pause(stepInterval);
		try{
			new Actions(driver).moveToElement(waitByXpath(xpath)).build().perform();
			log.info("鼠标移动至元素： “" + xpath +"” 之上");
		}catch(Exception e){
			e.printStackTrace();
			handleFailure("鼠标移动至元素： “"+xpath+"” 失败...");
		}
	}
	
	/**
	 * 鼠标移动至目标text上悬浮
	 * @param text 目标文本
	 */
	public void MouseMoveToText(final String text){
		pause(stepInterval);
		try{
			new Actions(driver).moveToElement(waitByXpath("//*[contains(text(),'"+ text +"')]"))
			.build().perform();
			log.info("鼠标移动至元素： “" + text +"” 之上");
		}catch(Exception e){
			e.printStackTrace();
			handleFailure("鼠标移动至元素： “"+text+"” 失败...");
		}
	}
	
	/**
	 * 鼠标移动至目标id上悬浮
	 * @param id 目标元素的id
	 */
	public void MouseMoveToId(final String id){
		pause(stepInterval);
		try{
			new Actions(driver).moveToElement(waitByXpath("//*[@id='"+ id +"']"))
			.build().perform();
			log.info("鼠标移动至元素： “" + id +"” 之上");
		}catch(Exception e){
			e.printStackTrace();
			handleFailure("鼠标移动至元素： “"+ id +"” 失败...");
		}
	}
	
	/**
	 * 模拟鼠标拖拽
	 * @param source 目标元素的起始位置（xpath）
	 * @param target 目标元素的退拽目标位置（xpath）
	 */
	public void MouseDragAndDrop(String source,String target){
		pause(stepInterval);
		try{
			new Actions(driver).dragAndDrop(waitByXpath(source), 
					waitByXpath(target)).perform();
			log.info("将元素： “" + source +"” 拖拽至 “"+ target +"” 成功");
		}catch(Exception e){
			e.printStackTrace();
			handleFailure("将元素： “" + source +"” 拖拽至 “"+ target +"” 失败...");;
		}
	}			
	
}