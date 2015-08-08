package indigo.annotations.impl;

import indigo.annotations.Assigns;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AssignsCollector {
	Assigns[] value();
}
