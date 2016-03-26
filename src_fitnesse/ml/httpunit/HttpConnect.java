package ml.httpunit;

//test
import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

//import com.meterware.httpunit.UploadFileSpec;

public class HttpConnect {

	public WebConversation wc;
	public WebResponse response;

	String host = "";
	String port = "";
	String address = "";
	boolean useProxy;

	static final String[] NO_PROXY = new String[] { "localhost", "(\\w)*.isvcs.net", "(\\w)*.prod.net", "(\\w)*.dev.net",
			"(\\w)*.tst.net", "7.(\\w)*.(\\w)*.(\\w)*", "(\\w)*.rc.gc.ca", "(\\w)*.cra-arc.net", "(\\w)*.dev.dce-eir.net",
			"(\\w)*.tst.dce-eir.net", "(\\w)*.omega.dce-eir.net", "(\\w)*.prv", "(\\w)*.hrdc-drhc.net",
			"(\\w)*.service.gc.ca", "7.29.239.245", "7.29.239.243", "courrielweb.canada.ca:443",
			"courrielweb-webmail.canada.ca:443", "msg.ssctest.itsso.gc.ca:443", "webmail.canada.ca:443",
			"webmail-courrielweb.canada.ca:443", "(\\w)*.ctst.canada.ca:443", "(\\w)*.email-courriel.canada.ca",
			"(\\w)*.courriel-email.canada.ca", "autodiscover.canada.ca", "email-courriel.canada.ca", "emailportal.canada.ca",
			"emailportal-portailcourriel.canada.ca", "portailcourriel.canada.ca", "portailcourriel-emailportal.canada.ca",
			"(\\w)*.telephony.local" };

	public static final int GET = 0;
	public static final int POST = 1;

	public HttpConnect(String host, String port, String addresssuffix) throws Exception {
		this.setUp(host, port, addresssuffix);
	}

	protected void setUp(String host, String port, String addresssuffix) throws Exception {
		HttpUnitOptions.setScriptingEnabled(false);
		HttpUnitOptions.setExceptionsThrownOnErrorStatus(false);
		this.host = host;
		this.port = port;
		if (!(host.equals("") && port.equals("") && addresssuffix.equals(""))) {
			this.address = "http://" + host + ":" + port + addresssuffix;
		} else {
			this.address = "";
		}
		useProxy = true;
		for (int i = 0; i < NO_PROXY.length && useProxy; i++) {
			if (Pattern.matches(NO_PROXY[i], host)) {
				useProxy = false;
			}
		}
		reset();
	}

	public void reset() throws Exception {
		// client = new HttpClient();
		// setMethod();
		wc = new WebConversation();
		if (useProxy) {
			wc.setProxyServer("proxy.omega.dce-eir.net", 8080);
		}
	}

	public int execute(String url, int methodType) throws Exception {
		WebRequest request = null;
		if (methodType == GET) {
			request = new GetMethodWebRequest(this.address + url);
		} else if (methodType == POST) {
			request = new PostMethodWebRequest(this.address + url);
		}
		// response = wc.getResponse(this.address + url);
		// PostMethodWebRequest request = new PostMethodWebRequest(this.address +
		// url);
		response = wc.getResponse(request);
		for (int i = 0; i < response.getHeaderFields("SET-COOKIE").length; i++) { // not
																																							// all
																																							// cookies
																																							// are
																																							// handled
																																							// so
																																							// this
																																							// fix
			// it?
			String name = response.getHeaderFields("SET-COOKIE")[i].substring(0,
					response.getHeaderFields("SET-COOKIE")[i].indexOf('='));
			if (wc.getCookieValue(name) == null) {
				String value = response.getHeaderFields("SET-COOKIE")[i].substring(
						response.getHeaderFields("SET-COOKIE")[i].indexOf('=') + 1,
						response.getHeaderFields("SET-COOKIE")[i].length());
				int idx = value.indexOf(';');
				if (idx > 0) {
					value = value.substring(0, idx);
				}
				wc.putCookie(name, value);
			}
		}
		return response.getResponseCode();
	}

	public int execute(String url, int methodType, int sc) throws Exception {
		int statusCode = execute(url, methodType);
		if (sc != statusCode) {
			System.out.println(response.getResponseMessage());
		}
		assertEquals(sc, statusCode);
		return statusCode;
	}

}
