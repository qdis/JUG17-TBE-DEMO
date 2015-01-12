package org.tbe.jug.demo.annotations;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

@SupportedAnnotationTypes(EliteSchhoolProcessor.SUPPORTED_ANNOTATION)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class EliteSchhoolProcessor extends AbstractProcessor {

	final static String SUPPORTED_ANNOTATION = "org.tbe.jug.demo.annotations.EliteSchool";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println(" \n\n---------------------  Processing");
		for (Element element : roundEnv.getElementsAnnotatedWith(EliteSchool.class)) {
			System.out.println(" Classes annotated with @EliteSchool : " + element.getSimpleName());
			// typeelement is class / interface or enum - a type
			TypeElement schoolType = ((TypeElement) element); 
			// this returns a typeMirror
			TypeMirror institutionTypeMirror = schoolType.getSuperclass();
			System.out.println(" institution typeMirror : "+institutionTypeMirror);
			
			// its a class -> its a declared type -> you can check this with typeKind();
			DeclaredType institutionDeclaredType = (DeclaredType)institutionTypeMirror;
			
			// back to element and typeelement for processing - yeah this is the only way
			TypeElement institutionType = ((TypeElement)institutionDeclaredType.asElement());
			
			//this list includes methods, fields and inven inner classes
			List<? extends Element> allInstitutionElements = institutionType.getEnclosedElements();
			for(Element instElement : allInstitutionElements){
				System.out.println(" Institution element : "+instElement.getSimpleName() + " "+instElement.getKind());
			}
			
			//lets check out the interface
			List<? extends TypeMirror> interfaces = schoolType.getInterfaces();
			TypeMirror comparable = interfaces.get(0); // we only declared comparable for school
			
			//typeMirror again -> lets turn it to an element
			TypeElement comparableTypeElement  = (TypeElement) ((DeclaredType)comparable).asElement();
			System.out.println(" Type comparable : "+comparableTypeElement);
			
			//Now lets get its typeParameters
			List<? extends TypeParameterElement> typeParameters = comparableTypeElement.getTypeParameters();
			for(TypeParameterElement comparableTypeParam : typeParameters){
				System.out.println(" TypeParameterElement : "+comparableTypeParam.getSimpleName());
			}
			
			List<VariableElement> schoolFields = ElementFilter.fieldsIn(schoolType.getEnclosedElements());
			for(VariableElement field : schoolFields){
				System.out.println(" Field of School : "+field.getSimpleName());
				if(field.asType() instanceof DeclaredType){
					DeclaredType studendsdeclaredType = (DeclaredType) field.asType();
					System.out.println(" Field Type Arguments : "+studendsdeclaredType.getTypeArguments());
				}
			}
			
		}
		System.out.println(" \n\n---------------------  Processing Finished");
		return true;
	}
}