package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.core.IsolationLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface ActionTransactional {
	IsolationLevel isolation() default IsolationLevel.DEFAULT;
}
