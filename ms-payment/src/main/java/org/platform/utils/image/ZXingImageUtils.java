package org.platform.utils.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZXingImageUtils {
	
	private static final int BLACK = 0xFF000000;

	private static final int WHITE = 0xFFFFFFFF;
	
	private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();

	private ZXingImageUtils() {
	}
	
	static {
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");    //指定字符编码utf-8
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);  //指定二维码的纠错等级为中级
        hints.put(EncodeHintType.MARGIN, 2);    //设置图片的边距
	}

	public static BufferedImage toBufferedImage(BitMatrix bitMatrix) {
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}
	
	public static void writeToFile(int width, int height, String format, String content, String filePath) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        writeToFile(bitMatrix, format, filePath);
	}
	
	public static void writeToFile(BitMatrix bitMatrix, String format, String filePath) throws IOException {
		writeToFile(bitMatrix, format, new File(filePath));
	}

	public static void writeToFile(BitMatrix bitMatrix, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(bitMatrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("could not write an image of format " + format + " to " + file);
		}
	}
	
	public static void writeToStream(int width, int height, String format, String content, OutputStream outputStream) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        writeToStream(bitMatrix, format, outputStream);
	}

	public static void writeToStream(BitMatrix bitMatrix, String format, OutputStream outputStream) throws IOException {
		BufferedImage image = toBufferedImage(bitMatrix);
		if (!ImageIO.write(image, format, outputStream)) {
			throw new IOException("could not write an image of format " + format);
		}
	}
	
	public static void main(String[] args) throws Exception {
		writeToFile(300, 300, "png", "weixin://wxpay/bizpayurl?pr=fgEc0Gh", "F:\\result\\tmp\\z.png");
	}
	
}
