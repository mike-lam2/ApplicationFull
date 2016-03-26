package ml.httpunit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.xml.sax.SAXException;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class WebConversation extends com.meterware.httpunit.WebConversation {

	protected String host = "";
	protected String port = "";
	protected String address = "";
	String password;
	boolean useProxy;

	static final String[] NO_PROXY = new String[] { "localhost",
			"(\\w)*.isvcs.net", "(\\w)*.prod.net", "(\\w)*.dev.net",
			"(\\w)*.tst.net", "7.(\\w)*.(\\w)*.(\\w)*", "(\\w)*.rc.gc.ca",
			"(\\w)*.cra-arc.net", "(\\w)*.dev.dce-eir.net", "(\\w)*.tst.dce-eir.net",
			"(\\w)*.omega.dce-eir.net", "(\\w)*.prv", "(\\w)*.hrdc-drhc.net",
			"(\\w)*.service.gc.ca", "7.29.239.245", "7.29.239.243",
			"courrielweb.canada.ca:443", "courrielweb-webmail.canada.ca:443",
			"msg.ssctest.itsso.gc.ca:443", "webmail.canada.ca:443",
			"webmail-courrielweb.canada.ca:443", "(\\w)*.ctst.canada.ca:443",
			"(\\w)*.email-courriel.canada.ca", "(\\w)*.courriel-email.canada.ca",
			"autodiscover.canada.ca", "email-courriel.canada.ca",
			"emailportal.canada.ca", "emailportal-portailcourriel.canada.ca",
			"portailcourriel.canada.ca", "portailcourriel-emailportal.canada.ca",
			"(\\w)*.telephony.local" };

	public static final String GET = "GET";
	public static final String POST = "POST";

	public WebConversation(String host, String port, String addresssuffix)
			throws Exception {
		HttpUnitOptions.setExceptionsThrownOnErrorStatus(false);
		this.host = host;
		this.port = port;
		setAddress(host, port, addresssuffix);
		setProxy();
	}

	protected void setAddress(String host2, String port2, String addresssuffix) {
		if (!(host.equals("") && port.equals("") && addresssuffix.equals(""))) {
			this.address = "http://" + host + ":" + port + addresssuffix;
		} else {
			this.address = "";
		}
	}

	protected void setProxy() {
		useProxy = true;
		for (int i = 0; i < NO_PROXY.length && useProxy; i++) {
			if (Pattern.matches(NO_PROXY[i], host)) {
				useProxy = false;
			}
		}
		if (useProxy) {
			setProxyServer("proxy.omega.dce-eir.net", 8080);
		}
	}

	@Override
	public WebResponse getResponse(String urlString)
			throws MalformedURLException, IOException, SAXException {
		super.getResponse(this.address + urlString);
		copyCookies();
		return this.getCurrentPage();
	}

	public WebResponse getPostResponse(String urlString)
			throws MalformedURLException, IOException, SAXException {
		WebRequest request = new PostMethodWebRequest(this.address + urlString);
		super.getResponse(request);
		copyCookies();
		return this.getCurrentPage();
	}

	public String getQuery(Object obj) throws UnsupportedEncodingException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringBuilder str = new StringBuilder();
		if (obj == null) {
			return str.toString();
		}
		Map props = null;
		if (obj instanceof Map) {
			props = (Map) obj;
		} else {
			props = BeanUtils.describe(obj);
		}
		if (props.size() > 0) {
			str.append("?");
		}
		for (Iterator iterator = props.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (props.get(key) != null && (props.get(key) + "").length() > 0) {
				str.append(encode(key));
				str.append("=");
				str.append(encode(props.get(key)));
				if (iterator.hasNext()) {
					str.append("&");
				}
			}
		}
		return str.toString();
	}

	public String encode(Object obj) throws UnsupportedEncodingException {
		return URLEncoder.encode(obj + "", "UTF-8");
	}

	// not all cookies are handled so this fixes it
	private void copyCookies() {
		for (int i = 0; i < this.getCurrentPage().getHeaderFields("SET-COOKIE").length; i++) {
			String name = this.getCurrentPage().getHeaderFields("SET-COOKIE")[i]
					.substring(0, this.getCurrentPage().getHeaderFields("SET-COOKIE")[i]
							.indexOf('='));
			if (getCookieValue(name) == null) {
				String value = this.getCurrentPage().getHeaderFields("SET-COOKIE")[i]
						.substring(this.getCurrentPage().getHeaderFields("SET-COOKIE")[i]
								.indexOf('=') + 1,
								this.getCurrentPage().getHeaderFields("SET-COOKIE")[i].length());
				int idx = value.indexOf(';');
				if (idx > 0) {
					value = value.substring(0, idx);
				}
				putCookie(name, value);
			}
		}
	}

}
