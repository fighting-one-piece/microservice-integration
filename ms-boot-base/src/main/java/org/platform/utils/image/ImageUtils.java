package org.platform.utils.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.platform.utils.endecrypt.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {

	private static Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

	private static String DEFAULT_THUMB_PREVFIX = "thumb_";
	private static String DEFAULT_CUT_PREVFIX = "cut_";
	private static Boolean DEFAULT_FORCE = false;
	
	public static String cutImage(String srcImg, String destImgPath, int x, int y, int width, int height) {
		return cutImage(new File(srcImg), destImgPath, new java.awt.Rectangle(x, y, width, height));
	}

	public static String cutImage(File srcImg, String destImgPath, int x, int y, int width, int height) {
		return cutImage(srcImg, destImgPath, new java.awt.Rectangle(x, y, width, height));
	}

	/**
	 * cutImage
	 * @param srcImg
	 * @param destImgPath 截取图片目标目录
	 * @param rect
	 */
	public static String cutImage(File srcImg, String destImgPath, java.awt.Rectangle rect) {
		File destImgDir = new File(destImgPath);
		if (!destImgDir.exists()) destImgDir.mkdirs();
		String path = destImgDir.getPath();
		try {
			if (!destImgDir.isDirectory()) path = destImgDir.getParent();
			if (!path.endsWith(File.separator)) path = path + File.separator;
			String destImg = path + DEFAULT_CUT_PREVFIX + "_" + IDUtils.genUUID() + "_" + srcImg.getName();
			cutImage(srcImg, new FileOutputStream(destImg), rect);
			return destImg;
		} catch (FileNotFoundException e) {
			LOG.error("dest image is not exist.", e);
		}
		return null;
	}
	
	public static void cutImage(File srcImg, OutputStream output, int x, int y, int width, int height) {
		cutImage(srcImg, output, new java.awt.Rectangle(x, y, width, height));
	}

	/**
	 * Title: 		 cutImage
	 * Description:  根据原图与裁切size截取局部图片
	 * @param srcImg 源图片
	 * @param output 图片输出流
	 * @param rect   需要截取部分的坐标和大小
	 */
	public static void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect) {
		if (srcImg.exists()) {
			FileInputStream fis = null;
			ImageInputStream iis = null;
			try {
				fis = new FileInputStream(srcImg);
				// ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
				String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
				String suffix = null;
				// 获取图片后缀
				if (srcImg.getName().indexOf(".") > -1) {
					suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
				} // 类型和图片后缀全部小写，然后判断后缀是否合法
				if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
					LOG.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
					return;
				}
				// 将FileInputStream 转换为ImageInputStream
				iis = ImageIO.createImageInputStream(fis);
				// 根据图片类型获取该种类型的ImageReader
				ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
				reader.setInput(iis, true);
				ImageReadParam param = reader.getDefaultReadParam();
				param.setSourceRegion(rect);
				BufferedImage bi = reader.read(0, param);
				ImageIO.write(bi, suffix, output);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			} finally {
				try {
					if (fis != null) fis.close();
					if (iis != null) iis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			LOG.warn("src image is not exist.");
		}
	}
	
	public static void thumbnailImage(String imagePath, int w, int h) {
		thumbnailImage(imagePath, w, h, DEFAULT_FORCE);
	}
	
	public static void thumbnailImage(String imagePath, int w, int h, boolean force) {
		thumbnailImage(imagePath, w, h, DEFAULT_THUMB_PREVFIX, DEFAULT_FORCE);
	}
	
	public static void thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force) {
		File srcImg = new File(imagePath);
		thumbnailImage(srcImg, w, h, prevfix, force);
	}
	
	public static void thumbnailImage(File srcImg, int w, int h, String prevfix, boolean force) {
		String p = srcImg.getAbsolutePath();
		try {
			if (!srcImg.isDirectory())
				p = srcImg.getParent();
			if (!p.endsWith(File.separator))
				p = p + File.separator;
			thumbnailImage(srcImg, new java.io.FileOutputStream(p + prevfix + srcImg.getName()), w, h, prevfix, force);
		} catch (FileNotFoundException e) {
			LOG.error("dest image is not exist.", e);
		}
	}

	/**
	 * Title: 			thumbnailImage
	 * Description: 	根据图片路径生成缩略图
	 * @param imagePath 原图片路径
	 * @param w         缩略图宽
	 * @param h         缩略图高
	 * @param prevfix   生成缩略图的前缀
	 * @param force     是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
	 */
	public static void thumbnailImage(File srcImg, OutputStream output, int w, int h, String prevfix, boolean force) {
		if (srcImg.exists()) {
			try {
				// ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
				String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
				String suffix = null;
				// 获取图片后缀
				if (srcImg.getName().indexOf(".") > -1) {
					suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
				} // 类型和图片后缀全部小写，然后判断后缀是否合法
				if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
					LOG.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
					return;
				}
				LOG.debug("target image's size, width:{}, height:{}.", w, h);
				Image img = ImageIO.read(srcImg);
				// 根据原图与要求的缩略图比例，找到最合适的缩略图比例
				if (!force) {
					int width = img.getWidth(null);
					int height = img.getHeight(null);
					if ((width * 1.0) / w < (height * 1.0) / h) {
						if (width > w) {
							h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
							LOG.debug("change image's height, width:{}, height:{}.", w, h);
						}
					} else {
						if (height > h) {
							w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
							LOG.debug("change image's width, width:{}, height:{}.", w, h);
						}
					}
				}
				BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.getGraphics();
				g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
				g.dispose();
				// 将图片保存在原目录并加上前缀
				ImageIO.write(bi, suffix, output);
				output.close();
			} catch (IOException e) {
				LOG.error("generate thumbnail image failed.", e);
			}
		} else {
			LOG.warn("the src image is not exist.");
		}
	}
	
	public static void main(String[] args) {
//		cutImage("F:\\result\\imgs\\1d760dd607214985acfb704d820179f5.jpg", "F:\\result\\imgs\\cut", 120, 3, 260, 22);
		int width = 74;
		int height = 76;
		for (int i = 0; i < 2; i++) {
			int y = 36 + i * 72;
			for (int j = 0; j < 4; j++) {
				int x = j * 72;
				cutImage("F:\\result\\imgs\\de89100d24cf4258a6bd4d2f2bd20ca0.jpg", "F:\\result\\imgs\\cut", x, y, width, height);
			}
		}
	}

}
