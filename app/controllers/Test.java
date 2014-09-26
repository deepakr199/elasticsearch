package controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String mydata = "some <e>string</e> with <e>the data i want</e> inside";
		Pattern pattern = Pattern.compile("<e>(.*?)</e>");
		Matcher matcher = pattern.matcher(mydata);
		if (matcher.find())
		{
		    System.out.println(matcher.group(1));
		}

	}

}
