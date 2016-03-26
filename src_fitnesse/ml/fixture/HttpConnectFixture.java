package ml.fixture;

import java.io.IOException;

import ml.httpunit.HttpConnect;
import fit.ColumnFixture;

public class HttpConnectFixture extends ColumnFixture {

	public String path = "";
	public String contains = "";
	private HttpConnect con;

	public int getResponseCode() {
		return con.response.getResponseCode();
	}

	public String getResponseMessage() {
		return con.response.getResponseMessage();
	}

	public String getServerType() {
		return con.response.getHeaderField("SERVER");
	}

	public String getTitle() throws IOException {
		int idxStart;
		idxStart = con.wc.getCurrentPage().getText().indexOf("<TITLE>") + "<TITLE>".length();
		int idxEnd = con.wc.getCurrentPage().getText().indexOf("</TITLE>");
		return con.wc.getCurrentPage().getText().substring(idxStart, idxEnd);
	}

	public boolean getContains() throws IOException {
		return con.wc.getCurrentPage().getText().contains(contains);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		con = new HttpConnect("52.1.154.230", "8080", "");
		con.execute(path, HttpConnect.POST);
	}

}