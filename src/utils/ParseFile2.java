package utils;

import java.util.Scanner;

public class ParseFile2 {

	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		while (scn.hasNext()) {
			String line = scn.nextLine();
			if (line.startsWith("real")) {
				String[] tokens = line.split("\t")[1].split("m");
				int min = Integer.parseInt(tokens[0])*1000;
				int msec = Integer.parseInt(tokens[1].substring(0, tokens[1].length() - 1).replace(".", ""));
				System.out.println("" + (min * 60 + msec));
			}else {
				int value = scn.nextInt();
				System.out.println(value);
				break;
			}
		}
		scn.close();
	}

}
