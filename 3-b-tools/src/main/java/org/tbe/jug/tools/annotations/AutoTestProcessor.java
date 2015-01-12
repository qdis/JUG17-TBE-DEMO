package org.tbe.jug.tools.annotations;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes(AutoTestProcessor.SUPPORTED_ANNOTATION)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AutoTestProcessor extends AbstractProcessor {

	final static String SUPPORTED_ANNOTATION = "org.tbe.jug.tools.annotations.AutoTest";

	public static String CODE_TEMPLATE = 
			"package org.tbe.jug.test.generated;\n\n"+
			"@org.tbe.jug.tools.annotations.InternalTest(persistanceUnit=\"%s\" %s )\n"+
			"public class %sTest extends org.tbe.jug.tools.GenericTest<%s,%s> {\n"+
			"}\n";	
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		for (Element element : roundEnv.getElementsAnnotatedWith(AutoTest.class)) {
			AutoTest autoTest = element.getAnnotation(AutoTest.class);
			TypeElement entityClass = ((TypeElement) element);
			String className = entityClass.getQualifiedName().toString();
			List<VariableElement> fields = ElementFilter.fieldsIn(entityClass.getEnclosedElements());
			String idClassName = null;
			for (VariableElement field : fields) {
				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					TypeMirror fieldType = field.asType();
					idClassName = fieldType.toString();
				} else {
					EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
					if (embeddedId != null) {
						TypeMirror fieldType = field.asType();
						idClassName = fieldType.toString();
					}
				}
			}
			idClassName = isPrimitive(idClassName) ? autoBox(idClassName) : idClassName;
			String TODO_PARAMS = "";
			String sourceCode = String.format(CODE_TEMPLATE, autoTest.persistanceUnit(),
					TODO_PARAMS,entityClass.getSimpleName(),className,idClassName);
			final JavaFileObject javaFileObject;
			try {
				javaFileObject = processingEnv.getFiler().createSourceFile(
						entityClass.getSimpleName()+"Test");
				final Writer w = javaFileObject.openWriter();
				w.write(sourceCode);
				w.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return true;
	}

	private String autoBox(String idClassName) {
		if(idClassName.equals("int")){
			return "Integer";
		}
		if(idClassName.equals("long")){
			return "Long";
		}
		if(idClassName.equals("double")){
			return "Double";
		}
		//TODO all primitives or cleaner Solution :)
		return null;
	}

	private boolean isPrimitive(String idClassName) {
		if(idClassName.equals("int")){
			return true;
		}
		if(idClassName.equals("long")){
			return true;
		}
		if(idClassName.equals("double")){
			return true;
		}
		//TODO all primitives or cleaner Solution :)
		return false;
	}
}
