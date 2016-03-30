package indigo.annotations.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import indigo.annotations.PreFalse;

@Retention(RetentionPolicy.RUNTIME)
public @interface PreFalseAssertsCollector {
	PreFalse[] value();
}
