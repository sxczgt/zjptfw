package com.tencent.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import cn.tsinghua.sftp.util.Base64;

public class RsaUtil {

	public static PublicKey getPubKey(String filename) throws Exception {
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	/**
	 * 读取输出流数据
	 * 
	 * @param p 进程
	 * @return 从输出流中读取的数据
	 * @throws IOException
	 */
	public static final String getShellOut(Process p) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedInputStream in = null;
		BufferedReader br = null;
		try {
			in = new BufferedInputStream(p.getInputStream());
			br = new BufferedReader(new InputStreamReader(in));
			String s;
			while ((s = br.readLine()) != null) {
				// 追加换行符
				sb.append("/n");
				sb.append(s);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			br.close();
			in.close();
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param certFile
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String encode(String certFile, String content) throws Exception {
		// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING");
		// 判断商户公钥是否存在
		File file = new File(certFile);
		if (!file.exists()) {
			throw new Exception("Certificate file does not exist");
		}
		// 获取二进制公钥文件
		RSAPublicKey pubKey = (RSAPublicKey) getPubKey(certFile);
		// 选定加密模式
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] cipherText = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
		// 加密后的东西
		String outB64 = Base64.encode(cipherText);
		return outB64;
	}

	public static void main(String[] args) throws Exception {
		String input = "微信支付";
		String output = encode("14498638021", input);
		// 加密后的东西
		System.out.println("原文：" + input);
		System.out.println("密文: " + output);

	}

}
