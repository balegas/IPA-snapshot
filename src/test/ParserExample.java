package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserExample {

	public static void main(String[] args) {

		String s = "prediate(arg1,arg2,arg3) = true   ";
		String pattern = "\\s*(.*)\\s*\\(\\s*(.*)\\s*\\)\\s*=\\s*(true|false|\\d)\\s*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s);
		if (m.find()) {
			System.out.println(m.groupCount());
			System.out.println(m.group(0));
			System.out.println(m.group(1));
			System.out.println(m.group(2));
			System.out.println(m.group(3));
		}

	}
}
