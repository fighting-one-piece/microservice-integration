package org.platform.utils.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(IPUtils.class);
	
	private static final List<String> FILTER_IP_LIST = new ArrayList<String>();
	
	static {
		FILTER_IP_LIST.add("106.14.52.33");
		FILTER_IP_LIST.add("106.15.34.120");
		FILTER_IP_LIST.add("106.15.156.227");
	}

	private IPUtils() {
	}

	public static String getIPAddress(HttpServletRequest request) {
		if (request == null) return "unknown";
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		ip = ip.replace(" ", "");
		for (int i = 0, len = FILTER_IP_LIST.size(); i < len; i++) {
			String filterIp = FILTER_IP_LIST.get(i);
			ip = ip.replace(filterIp + ",", "").replace("," + filterIp, "");
		}
		return ip.contains(",") ? ip.split(",")[0] : ip;
	}

	/**
	 * 通过IP地址获取MAC地址
	 * @param ip 127.0.0.1格式
	 * @return mac 
	 * @throws Exception
	 */
	public static String getLocalMACAddress(HttpServletRequest request) {
		String ip = getIPAddress(request);
		String macAddress = "";
		try {
			final String LOOPBACK_ADDRESS_1 = "127.0.0.1";
			final String LOOPBACK_ADDRESS_2 = "0:0:0:0:0:0:0:1";
			// 如果为127.0.0.1,则获取本地MAC地址。
			if (LOOPBACK_ADDRESS_1.equals(ip) || LOOPBACK_ADDRESS_2.equals(ip)) {
				InetAddress inetAddress = InetAddress.getLocalHost();
				// 貌似此方法需要JDK1.6。
				byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
				// 下面代码是把mac地址拼装成String
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					if (i != 0) {
						sb.append("-");
					}
					// mac[i] & 0xFF 是为了把byte转化为正整数
					String s = Integer.toHexString(mac[i] & 0xFF);
					sb.append(s.length() == 1 ? 0 + s : s);
				}
				// 把字符串所有小写字母改为大写成为正规的mac地址并返回
				return sb.toString().trim().toUpperCase();
			}
			// 获取非本地IP的MAC地址
			final String MAC_ADDRESS_PREFIX_EN = "MAC Address = ";
			final String MAC_ADDRESS_PREFIX_CH = "MAC 地址 = ";
			Process process = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine()) != null) {
				int index = line.indexOf(MAC_ADDRESS_PREFIX_EN);
				if (index != -1) {
					macAddress = line.substring(index + MAC_ADDRESS_PREFIX_EN.length()).trim().toUpperCase();
				} else {
					index = line.indexOf(MAC_ADDRESS_PREFIX_CH);
					if (index != -1) {
						macAddress = line.substring(index + MAC_ADDRESS_PREFIX_CH.length()).trim().toUpperCase();
					}
				}
				if (!"".equals(macAddress)) break;
			}
			br.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} 
		return macAddress;
	}
	
	/**
	 * 获取MAC地址
	 * 
	 * @return 返回MAC地址
	 */
	public static String getMACAddress(HttpServletRequest request) {
//		String ip = getIPAddress(request);
//		String macAddress = "";
//		macAddress = getMacInWindows(ip).trim();
//		if (macAddress == null || "".equals(macAddress)) {
//			macAddress = getMacInLinux(ip).trim();
//		}
		return "b0:83:fe:72:39:4c";
	}
	
	/**
	 * 
	 * @param ip
	 *            目标ip
	 * @return Mac Address
	 * 
	 */
	public static String getMacInWindows(final String ip) {
		String result = "";
		String[] cmd = { "cmd", "/c", "ping " + ip };
		String[] another = { "cmd", "/c", "arp -a" };
		String cmdResult = callCmd(cmd, another);
		result = filterMacAddress(ip, cmdResult, "-");
		return result;
	}

	/**
	 * @param ip
	 *            目标ip
	 * @return Mac Address
	 * 
	 */
	public static String getMacInLinux(final String ip) {
		String result = "";
		String[] cmd = { "/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a" };
		String cmdResult = callCmd(cmd);
		result = filterMacAddress(ip, cmdResult, ":");
		return result;
	}
	
	public static String callCmd(String[] cmd) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param cmd
	 *            第一个命令
	 * @param another
	 *            第二个命令
	 * @return 第二个命令的执行结果
	 */
	public static String callCmd(String[] cmd, String[] another) {
		String result = "";
		String line = "";
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
			proc = rt.exec(another);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param ip
	 *            目标ip,一般在局域网内
	 * @param sourceString
	 *            命令处理的结果字符串
	 * @param macSeparator
	 *            mac分隔符号
	 * @return mac地址，用上面的分隔符号表示
	 */
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
	
}
