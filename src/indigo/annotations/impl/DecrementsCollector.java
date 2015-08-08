package indigo.annotations.impl;

import indigo.annotations.Decrements;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DecrementsCollector {
	Decrements[] value();
}
