package org.cisiondata.modules.datatransfer.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class USBUtils {

	//磁盘状态
	private static Map<String, Boolean> diskStatusCache = new LinkedHashMap<String, Boolean>();
	
	//磁盘定义
	private static final String[] disks = new String[] { "C:", "D:", "E:", "F:", "G:", "H:", "I:", "J:", "K:"};

	// 死循环检测每个磁盘状态
	public static void checkDiskStatus() {
		File directory = null;
		for (;;) {
			for (int i = 0, len = disks.length; i < len; i++) {
				String disk = disks[i];
				directory = new File(disk + File.separator);
				boolean isExist = directory.exists();
				// 如果磁盘现在存在,以前不存在,则表示刚插上U盘
				if (isExist && !diskStatusCache.get(disk)) {
//					return;
					System.out.println("检测到U盘挂载: " + disk);
				}
				if (!isExist && diskStatusCache.get(disk)) {
					System.out.println("检测到U盘卸载: " + disk);
				}
				// 每次状态改变时，更新保存的状态
				if (isExist != diskStatusCache.get(disk)) {
					diskStatusCache.put(disk, isExist);
				}
			}
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//初始化磁盘状态，存在true，否则false
	public static void initDiskStatus() {
		File directory = null;
		for (int i = 0, len = disks.length; i < len; i++) {
			String disk = disks[i];
			directory = new File(disk + File.separator);
			diskStatusCache.put(disk, directory.exists());
			System.err.println(disk + " " + directory.exists());
		}
	}
	
	public static void main(String[] args) {
		initDiskStatus();
		checkDiskStatus();
	}

}
