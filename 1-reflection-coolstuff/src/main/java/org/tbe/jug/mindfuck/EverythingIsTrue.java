package org.tbe.jug.mindfuck;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class EverythingIsTrue {
	
	static void setFinalStatic(Field field, boolean newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	public static void main(String args[]) throws Exception {
		setFinalStatic(Boolean.class.getField("FALSE"), true);

		System.out.format(" Everything is %s \n", false); // "Everything is true"
		
		System.out.format(" Just to clarify false is : %s ",false); 
		
	}

}
