package net.craftingstore.connector.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ObjectUtils {

	/**
	 * convert an object containing string back into the object
	 * 
	 * @param serializedObject
	 * @return the reconverted object
	 */
	public static Object deserializeFromString(String serializedObject) {
		Object deserializedObject = null;
		try {
			//we have encoded it, so we have to decode it for sure
			byte[] pick = Base64Coder.decodeLines(serializedObject);
			InputStream in = new ByteArrayInputStream(pick);
			ObjectInputStream ois = new ObjectInputStream(in);
			deserializedObject = ois.readObject();

			//we do not longer need it, close it!
			ois.close();
			in.close();

		} catch (ClassNotFoundException e) {
			//if readObject() can't find a valid class, an exception will be thrown...
			//this should not happen at all
			e.printStackTrace();
		} catch (IOException e) {
			//if the string does not contain an object, an exception will be thrown...
			//this should not happen at all
			e.printStackTrace();
		}
		return deserializedObject;
	}

	/**
	 * convert a serializeable object into a string
	 * 
	 * @param objectToWrite
	 * @return the object containing string
	 */
	public static String serializeToString(Object objectToWrite) {
		String output = "";
		try {
			//creating our objectoutputstream
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			//writing our object to the stream
			oos.writeObject(objectToWrite);

			//do not forget to close!
			oos.close();
			out.close();

			//if we are testing something on windows and then moving the db to our server this will prevent encoding errors
			//and we are writing the bytes to our string
			output = Base64Coder.encodeLines(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
