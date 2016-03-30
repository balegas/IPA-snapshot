package indigo.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import indigo.annotations.impl.PreFalseAssertsCollector;

@Repeatable(PreFalseAssertsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreFalse {
	String value();
}
