package org.cisiondata.utils.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacAddressUtils {

	private static Logger LOG = LoggerFactory.getLogger(MacAddressUtils.class);

	public static String callCmd(String[] cmd) {
		String result = "";
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(is);
			String line = null;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	public static String callCmd(String[] cmd, String[] ocmd) {
		String result = "";
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmd);
			process.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
			process = runtime.exec(ocmd);
			InputStreamReader is = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(is);
			String line = null;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	public static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {
		String result = "";
		String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
		Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(sourceString);
		while (matcher.find()) {
			result = matcher.group(1);
			if (sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
				break; // 如果有多个IP,只匹配本IP对应的Mac.
			}
		}
		return result;
	}

	public static String getMacInWindows(final String ip) {
		String[] cmd = { "cmd", "/c", "ping " + ip };
		String[] ocmd = { "cmd", "/c", "arp -a" };
		String cmdResult = callCmd(cmd, ocmd);
		return filterMacAddress(ip, cmdResult, "-");
	}

	public static String getMacInLinux(final String ip) {
		String[] cmd = { "/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a" };
		String cmdResult = callCmd(cmd);
		return filterMacAddress(ip, cmdResult, ":");
	}

	public static String getMacAddress(String ip) {
		String macAddress = getMacInWindows(ip).trim();
		if (macAddress == null || "".equals(macAddress)) {
			macAddress = getMacInLinux(ip).trim();
		}
		return macAddress;
	}

	public static void main(String[] args) {
		System.out.println("220.181.111.148 mac: " + getMacAddress("220.181.111.148"));
		System.out.println("125.71.160.94 mac: " + getMacAddress("125.71.160.94"));
		System.out.println("192.168.0.198 mac: " + getMacAddress("192.168.0.198"));
	}

}
