package org.example.common.annotations;

import io.qameta.allure.LabelAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allure TestOps Microservice Annotation.
 * Maps automated tests to target backend/frontend microservices.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = "microservice")
public @interface Microservice {
    String value();
}
