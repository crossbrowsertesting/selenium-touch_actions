<h1>Getting Started with Selenium TouchActions and CrossBrowserTesting</h1>
<p><em>For this document, we provide all example files in our <a href="https://github.com/crossbrowsertesting/selenium-touch_actions">Selenium TouchActions GitHub Repository</a>.</em></p>
<p>The <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/interactions/touch/TouchActions.html">TouchActions</a> class of <a href="https://www.selenium.dev/">Selenium</a> implements actions for touch enabled devices, reusing the available composite and builder design patterns from Actions.</p>
<p>In this guide we will use JUnit for testing using the <a href="https://www.seleniumhq.org/">Selenium Webdriver</a> and <a href="https://www.java.com/en/">Java</a> programming language to demonstrate using the longpress touch action to draw within a canvas element on iOS.</p>
<h3>Running a test</h3>
<div class="blue-alert">You’ll need to use your Username and Authkey to run your tests on CrossBrowserTesting. To get yours, sign up for a <a href="https://crossbrowsertesting.com/freetrial"><b>free trial</b></a> or purchase a <a href="https://crossbrowsertesting.com/pricing"><b>plan</b></a>.</div>
<ol>
<li>Download <a href="https://docs.seleniumhq.org/download/">Selenium WebDriver for Java</a><img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/selenium_for_java.png" /></li>
<li>Add the Selenium jars to the build path of your project
<ul>
<li>Right click your project folder and select <strong>Properties</strong><img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_properties_1.png" /></li>
<li>From the <strong>Java Build Path</strong> tab, select the <strong>Libraries</strong> tab<img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_build_path.png" /></li>
<li>Select <strong>Add External JARs</strong> and add all jars for Selenium<img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_add_jars.png" /><img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_add_jars_1.png" /></li>
<li><strong>Apply and Close</strong></li>
</ul>
</li>
<li>Create a new JUnit Test Case
<ul>
<li>Right click your project folder</li>
<li>Hover over <strong>New </strong>and select <strong>JUnit Test Case</strong><img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_junit_testcase.png" /></li>
<li>Name your test case and select <strong>Finish</strong><img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_new_junit.png" /></li>
<li>Select <strong>OK</strong> to add JUnit to the build path<img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_add_junit.png" /></li>
</ul>
</li>
<li>Copy the following content:
<pre><code>import java.net.URL;
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
	String username = "YOUR_USERNAME";																						//username can be found on the Manage Account page, ensure @ is encoded with %40
   	String authkey = "YOUR_AUTHKEY";  //authkey can be found on the Manage Account page
    String score;
    
    AppiumDriver driver;
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
	     driver = new IOSDriver(url, caps);
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
} </code></pre>
<div>
<pre>Run your test by selecting the <strong>Run</strong> button<img src="http://help.crossbrowsertesting.com/wp-content/uploads/2019/08/eclipse_run.png" /></pre>
</div>
</li>
</ol>
<p>Congratulations! You have successfully created a test using Selenium TouchActions with CBT and JUnit. Now you are ready to see your build start to run in the <a href="https://app.crossbrowsertesting.com/selenium/results">Crossbrowsertesting app</a>.</p>
<h3>Conclusions</h3>
<p>By following the steps outlined in this guide, you are now able to seamlessly run a test using Selenium TouchActions with CrossBrowserTesting. If you have any questions or concerns, please feel free to reach out to our <a href="mailto:support@crossbrowsertesting.com">support team</a>.</p>
