package indigo.annotations.impl;

import indigo.annotations.Increments;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IncrementsCollector {
	Increments[] value();
}
