package com.test;

import org.apache.commons.codec.binary.Base64;

import com.util.AESUtils;

public class AESTest {
	public static void main(String[] args) {
		String msg = "hello world";
		System.out.println(msg);
		byte[] encodeMsg = AESUtils.encode(msg.getBytes());
		String miwen = Base64.encodeBase64String(encodeMsg);
		String noBase64Miwen = new String(encodeMsg);
		
		System.out.println("加密后：" + miwen);
		System.out.println("加密后>>：" + noBase64Miwen);
		
		String decodeMsg = null;
		try {
			decodeMsg = AESUtils.decodeMsg(miwen.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("解密后：" + decodeMsg);
	}

}
