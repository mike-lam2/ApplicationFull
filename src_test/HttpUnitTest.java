import static org.junit.Assert.assertEquals;
import ml.httpunit.HttpConnect;

import org.junit.Test;

public class HttpUnitTest {

	@Test
	public void test_Home() throws Exception {
		HttpConnect tester;
		tester = new HttpConnect("52.1.154.230", "8080", "/application");
		tester.execute("", HttpConnect.POST);
		assertEquals(200, tester.wc.getCurrentPage().getResponseCode());
		assertEquals("OK", tester.wc.getCurrentPage().getResponseMessage());
		// System.out.println(tester.wc.getCurrentPage().getText());

	}

}
