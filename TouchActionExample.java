import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.appium.java_client.TouchAction;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.PointOption;
 
​
​
class TouchActionExample{
	String username = "YOUR_USERNAME"; 	//encode the @ symbol with %40																							//username can be found on the Manage Account page, ensure @ is encoded with %40
   	String authkey = "YOUR_AUTHEKEY";  //authkey can be found on the Manage Account page
    String score;
    
    AppiumDriver<IOSElement> driver;
    String sessionid;
​
	@BeforeEach
	void setUp() throws Exception {
	     
	     //set capabilites 
	     DesiredCapabilities caps = new DesiredCapabilities();  															//additional capabilites can be found at https://app.crossbrowsertesting.com/selenium/run   
	    	     
	     caps.setCapability("browserName", "Safari");
	     caps.setCapability("deviceName", "iPhone 11 Pro Max");
	     caps.setCapability("platformVersion", "13.2");
	     caps.setCapability("platformName", "iOS");
	     caps.setCapability("deviceOrientation", "portrait");
	     caps.setCapability("record_video", true);   
​
	     String hubAddress = String.format("http://%s:%s@hub.crossbrowsertesting.com:80/wd/hub", username, authkey);
	     URL url = new URL(hubAddress);
​
	     driver = new IOSDriver<IOSElement>(url, caps);
	     driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
	     sessionid = driver.getSessionId().toString();
	    
	}
​
	@AfterEach
	void tearDown() throws Exception {
		driver.quit(); 																													//close the driver
	}
​
	@Test
	void test() {
		try {	
			driver.get("https://sketchtoy.com/");
			Thread.sleep(5000);
			
			 IOSElement element = driver.findElement(By.xpath("//div[@class='sketch-canvas-container']/canvas"));
	
			 
			 new TouchAction(driver)
			 .longPress(LongPressOptions.longPressOptions().withPosition(PointOption.point(184, 233)))
			 .moveTo(PointOption.point(200, 200))
			 .moveTo(PointOption.point(250, 250))
			 .release()
			 .perform();
			
		     Thread.sleep(5000);
			
		
			score = "pass";																												//set score to pass
			setScore();
		}
		catch (Exception e){
			score = "fail";																												//set score to fail if any exceptions
			try{
				setScore();
			}
			catch(Exception err) {
				System.out.println(err);
			}
			System.out.println(e);
			
		}
​
	
	}
	//setScore function - calls our api and set the score to pass or fail after a test
	public void setScore() throws UnirestException{
		Unirest.put("https://crossbrowsertesting.com/api/v3/selenium/"+sessionid)
				.basicAuth(username, authkey)
				.field("action","set_score")
				.field("score", score)
				.asJson();
		
	}
	//takeSnapshot function - calls our api and takes snapshots throughout your automated test
	public void takeSnapshot(String seleniumTestId) throws UnirestException {
        Unirest.post("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .asJson();
          
​
    }
​
}
