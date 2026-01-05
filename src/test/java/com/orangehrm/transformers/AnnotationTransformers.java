package com.orangehrm.transformers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

public class AnnotationTransformers implements IAnnotationTransformer {

	@Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {

        if (testClass == null || annotation == null) return;

        // If method does not have groups, inherit class groups
        if (annotation.getGroups() == null || annotation.getGroups().length == 0) {
            Test testAnnotation = (Test) testClass.getAnnotation(Test.class);
            if (testAnnotation != null) {
                annotation.setGroups(testAnnotation.groups());
            }
        }
    }
   
}