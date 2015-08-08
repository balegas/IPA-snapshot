package indigo.annotations;

import indigo.annotations.impl.*;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(DecrementsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrements {
	String value();
}
