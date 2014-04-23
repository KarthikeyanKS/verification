//		To validate broken links with and without login

package mslt.verification;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BrokenLinks  {
	String baseURL;
	BrokenLinksUtil util = new BrokenLinksUtil();
	
	@Test(priority = 1)
	  public void login() throws InterruptedException {
		util.login(baseURL);
	  }
	
	@Test(dataProvider = "linkSupplier",dependsOnMethods = { "login"})
	  public void loginValidate(String cname) throws InterruptedException, IOException {
		util.brokenLinks(cname);
	  }
	
	@Test(dependsOnMethods = { "loginValidate"})
	  public void logout() throws InterruptedException {
		util.logout();
	  }
	
	@Test(dataProvider = "linkSupplier",dependsOnMethods = { "logout"})
	  public void logoutValidate(String cname) throws InterruptedException, IOException {
		util.brokenLinks(cname);
	  }

	@DataProvider(name = "linkSupplier", parallel = false)
	public Object[][] data() throws Exception {
		Object[][] retObjArr={{".link.profile"},{".link.search"},{".link.guide"},{".link.activity"},{".home-link"}};
		return(retObjArr);
	}

	

																//	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(){                    		//	(String url) {
		baseURL = "http://www.mi.tv";							// 	url;
	}

	@AfterClass
	  public void teardown() {
		util.driver.quit();
	  }
	}