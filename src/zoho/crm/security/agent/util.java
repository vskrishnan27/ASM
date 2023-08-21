//$Id$
package zoho.crm.security.agent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class util {
	
	private static Logger LOGGER = Logger.getLogger(util.class.getName());
	
	public static void printer(String value1,String value2,String className) {
		StringBuilder sb = new StringBuilder()
							.append(value1)
							.append(",")
							.append(className)
							.append(",")
							.append(value2);
		LOGGER.info(sb.toString());
	}
	
	public static void printer(Object value1,Object value2,String className) {
		LOGGER.info(value1.toString()+value2.toString()+className+"==OBJECT");
	}
	
	
	public static void printer(String value) {
		LOGGER.info(value);
	}
	
	public static void printer(int value) {
		LOGGER.info(String.valueOf(value));
	}
	
	public static void printer(boolean value) {
		LOGGER.info(String.valueOf(value));
	}
	
	public static void writeToFile(String className, byte[] data) {
		String filePath = "/Users/santhana-16396/check/test/jar/" + className + ".class";
		try (FileOutputStream stream = new FileOutputStream(filePath)) {
			stream.write(data);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to write instrumented class", e);
		}
	}
	
}
