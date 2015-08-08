package indigo.annotations;

import indigo.annotations.impl.AssertsCollector;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(AssertsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Assert {
	String value();
}
