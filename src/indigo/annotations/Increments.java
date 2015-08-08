package indigo.annotations;

import indigo.annotations.impl.IncrementsCollector;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(IncrementsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Increments {
	String value();
}
