/*
 * 
 */
package com.soyomaker.handsgo.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class StringUtil {

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

	public static String inputStream2String(InputStream in, String charset) throws IOException {
		if (in == null) {
			return "";
		}
		final int size = 128;
		byte[] buffer = new byte[size];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int cnt = 0;
		while ((cnt = in.read(buffer)) > -1) {
			baos.write(buffer, 0, cnt);
		}
		baos.flush();

		in.close();
		baos.close();

		return baos.toString(charset);
	}

	public static String inputStream2String(InputStream in) throws IOException {
		return inputStream2String(in, "utf-8");
	}

	public static String formatFileSize(long size) {
		String result = "";
		if (size < 1024 * 1024) {
			result = DECIMAL_FORMAT.format(size * 1.0 / 1024) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			result = DECIMAL_FORMAT.format(size * 1.0 / 1024 / 1024) + "MB";
		} else {
			result = DECIMAL_FORMAT.format(size * 1.0 / 1024 / 1024 / 1024) + "GB";
		}
		return result;
	}

	public static String getCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			{
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				bis.mark(0);
				int read = bis.read(first3Bytes, 0, 3);
				if (read == -1) {
					bis.close();
					return charset;
				}
				if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
					charset = "UTF-16LE";
					checked = true;
				} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
					charset = "UTF-16BE";
					checked = true;
				} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
						&& first3Bytes[2] == (byte) 0xBF) {
					charset = "UTF-8";
					checked = true;
				}

				bis.close();
			}
			{
				BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(file));
				bis2.mark(0);
				int read = bis2.read(first3Bytes, 0, 3);
				if (read == -1) {
					bis2.close();
					return charset;
				}
				if (!checked) {
					while ((read = bis2.read()) != -1) {
						if (read >= 0xF0)
							break;
						if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
							break;
						if (0xC0 <= read && read <= 0xDF) {
							read = bis2.read();
							if (0x80 <= read && read <= 0xBF)// 双字节 (0xC0 -
																// 0xDF)
																// (0x80 -
																// 0xBF),也可能在GB编码内
								continue;
							else
								break;
						} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
							read = bis2.read();
							if (0x80 <= read && read <= 0xBF) {
								read = bis2.read();
								if (0x80 <= read && read <= 0xBF) {
									charset = "UTF-8";
									break;
								} else
									break;
							} else
								break;
						}
					}
				}
				bis2.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charset;
	}
}
