package indigo.effects;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Params {
	static final Pattern paramPattern = Pattern.compile(",");

	static Parameter[] params(Method m, String args) {
		args = args.replaceAll("[\\s\\$]", "");
		List<Parameter> res = new ArrayList<>();
		for (String i : paramPattern.split(args))
			if (!i.isEmpty())
				res.add(m.getParameters()[Integer.valueOf(i)]);

		return res.toArray(new Parameter[res.size()]);
	}

	static String params(Method m, String name, String args) {
		Parameter[] pa = params(m, args);
		StringBuilder sb = new StringBuilder().append(name).append("( ");
		for (int i = 0;;) {
			sb.append(pa[i].getType().getSimpleName()).append(" : ").append(pa[i].getName());
			if (++i < pa.length)
				sb.append(", ");
			else
				break;

		}
		return sb.append(" )").toString();
	}

	// static String[] strParamTypes(Method m, String args) {
	// return params(m, args).stream().map(i ->
	// i.getType().getSimpleName()).collect(Collectors.toList()).toArray(new
	// String[0]);
	// }
	//
	// static String[] strParamNames(Method m, String args) {
	// return params(m, args).stream().map(i ->
	// i.getName()).collect(Collectors.toList()).toArray(new String[0]);
	// }
}
