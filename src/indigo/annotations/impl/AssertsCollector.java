package indigo.annotations.impl;

import indigo.annotations.Assert;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AssertsCollector {
	Assert[] value();
}
