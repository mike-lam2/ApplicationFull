package ml.httpunit;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PrivateProperties extends Properties {

	public PrivateProperties() {
		try {
			this.load(new FileReader("h:\\private.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
