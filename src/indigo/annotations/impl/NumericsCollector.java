package indigo.annotations.impl;

import indigo.annotations.Numeric;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NumericsCollector {
	Numeric[] value();
}
