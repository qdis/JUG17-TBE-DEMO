package org.tbe.jug.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.tbe.jug.tools.enums.TestType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InternalTest {

	String persistanceUnit();

	TestType[] testTypes() default { TestType.CREATE, TestType.READ,
			TestType.UPDATE, TestType.DELETE, TestType.QUERY_SELECT };

	String[] ignorableFields() default {};
}
