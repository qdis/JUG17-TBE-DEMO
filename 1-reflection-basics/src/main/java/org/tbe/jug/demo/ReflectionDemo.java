package org.tbe.jug.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReflectionDemo {

	public final static void basics() throws Exception {
		// Access
		DummyOne ussEnterprise = new DummyOne();

		try {
			Field nope = DummyOne.class.getField("name");
		} catch (Exception e) {
			System.out.println("Busted, its private and not visible!");
		}

		Field field = DummyOne.class.getDeclaredField("name");
		System.out.println("Got it!" + field);

		// you still can't access it though
		field.setAccessible(true);
		// fixed
		System.out.println("Name of ussEnterprise is : " + field.get(ussEnterprise));
		// not yet set, lets do something about it

		field.set(ussEnterprise, "TEH SPACE SHIPZ");
		System.out.println("New Name of ussEnterprise is : " + field.get(ussEnterprise));

		// now lets see about that final field.

		field = DummyOne.class.getDeclaredField("fin");

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, new FinalExample("NOT FINAL!"));
		System.out.println("Final Value : " + DummyOne.fin);

		// lets make some more Objects
		DummyOne newObject = null;

		newObject = DummyOne.class.getConstructor(String.class).newInstance("new one");

		System.out.println("Hi i'm the new one : " + newObject);

		// methods ?
		// lets try a different approach to get the class

		Class<DummyOne> clazz = (Class<DummyOne>) Class.forName("org.tbe.jug.demo.ReflectionDemo$DummyOne");
		DummyOne instance = clazz.newInstance();
		clazz.getMethod("setName", String.class).invoke(instance, "Jacobs");
		String name = (String) clazz.getMethod("getName").invoke(instance);
		System.out.println("Name is : " + name);

	}

	public static void coolStuff() throws Exception {
		// Generics are cool.
		DummyTwo<String> ds = new DummyTwo<String>("Hallo Mate");
		System.out.println("His class is : " + ds.getClass());
		// lets see about the return type, eh?
		Class<?> returnType = null;

		returnType = DummyTwo.class.getMethod("getType").getReturnType();

		System.out.println("Return type is : " + returnType);
		// what did you expect ?

		// lets try something else

		returnType = ds.getClass().getMethod("getType").getReturnType();

		System.out.println("Return type for my INSTANCE is : " + returnType);
		// what did you expect ?

		// one more ?
		Type returnTypeG = null;

		returnTypeG = ds.getClass().getMethod("getType").getGenericReturnType();

		System.out.println("Return type for my INSTANCE is : " + returnTypeG);
		// its something ..
		// actually this is not doable, but something else is :)

		Field stringListField = DummyTwoHolder.class.getDeclaredField("dummy");
		ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
		Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
		System.out.println(stringListClass); // well, its something.
		
		//lets do something cool
		GenericTest coolHuh = new GenericTest(); 
		DummyOne gotIt = coolHuh.returnNewTWithoutKnowingType();
		System.out.println(gotIt); 
	}
	
	public static class Generic<T>{
		
		public T returnNewTWithoutKnowingType() throws Exception{
			Class clazz = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	        T t = (T) clazz.getConstructor().newInstance();
	        return t;
		}
		
	}
	
	public static class GenericTest extends Generic<DummyOne>{
		
	}

	public static void main(String args[]) throws Exception {
		//basics();
		coolStuff();
	}

	public static class DummyOne {
		private String name;
		public static final FinalExample fin = new FinalExample("FINAL!");

		public DummyOne() {
			this.name = null;
		}

		public DummyOne(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "DummyOne [name=" + name + "]";
		}

	}

	public static class FinalExample {

		private String ff;

		public FinalExample(String ff) {
			this.ff = ff;
		}

		@Override
		public String toString() {
			return "FinalExample [" + ff + "]";
		}

	}

	public static class DummyTwo<T> {
		private T type;

		public DummyTwo() {
			type = null;
		}

		public DummyTwo(T type) {
			this.type = type;
		}

		public T getType() {
			return type;
		}

		public List<T> getTList() {
			return new ArrayList<T>();
		}
	}

	public static class DummyTwoHolder {
		private DummyTwo<String> dummy;

		public DummyTwo<String> getDummy() {
			return dummy;
		}

		public void setDummy(DummyTwo<String> dummy) {
			this.dummy = dummy;
		}

	}
}
