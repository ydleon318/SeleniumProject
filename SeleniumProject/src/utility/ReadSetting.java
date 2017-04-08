package utility;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 环境变量读取控制类
* @author Chris Yeung 
* @version V1.0
* 
*/
public class ReadSetting {
	public static Properties prop = getProperties() ;
	
	public static int WebDriverType = Integer.parseInt(prop.getProperty("WebDriverType"));
	
	public static String HubUrl = prop.getProperty("HubUrl");
	
	public static String ChromeDriverPath = prop.getProperty("ChromeDriverPath");

	public static String IEDriverPath = prop.getProperty("IEDriverPath");

	public static String StepInterval = prop.getProperty("StepInterval");

	public static String ClickTimeout = prop.getProperty("ClickTimeout");
	
	public static String DatabaseIP = prop.getProperty("DatabaseIP");
	
	public static String Sid = prop.getProperty("Sid");
	
	public static String User = prop.getProperty("User");
	
	public static String Password = prop.getProperty("Password");
	
	public static String DatabasePort = prop.getProperty("DatabasePort");
	
	public static String driver = prop.getProperty("driver");
	
	public static String userName = prop.getProperty("userName");
	
	public static String passWord = prop.getProperty("passWord");
	
	public static String url = prop.getProperty("url");
	
	public static String timeout = Integer.toString(Integer.parseInt(prop.getProperty("ClickTimeout"))*1000);
	
	public static String getProperty(final String property) {
		return prop.getProperty(property);
	}
	
	public  static  Properties getProperties() {
	try{
		prop = new Properties();
	
		String path = ReadSetting.class.getResource("ReadSetting.class").getPath();
//		System.out.println(path);
	    String dir = path.split("\\/target/classes/utility/ReadSetting.class")[0];
//	    System.out.println(dir);
	    String filepath = dir.split("/", 2)[1] + "/prop/prop.properties";
//		System.out.println(filepath);
		InputStream input = new FileInputStream(filepath);
		prop.load(input);
		input.close();
	} catch (Exception e) {
		System.out.println("工作空间路径不能有\".\"");
		e.printStackTrace();
	}
	return prop;
		
	}
	public static void main(String args[]){
		System.out.println(ClickTimeout);
	}
	
}

