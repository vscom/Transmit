package com.bvcom.transmit.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class BufferedInputStreamDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String s = "This is a &copy; copyright symbol "
				+ "but this is &copy not.\\n";
		byte buf[] = s.getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(buf);
		BufferedInputStream f = new BufferedInputStream(in);
		int c;
		boolean marked = false;
		while ((c = f.read()) != -1) {
			switch (c) {
			case '&':
				if (!marked) {
					f.mark(32);
					marked = true;
				} else {
					marked = false;
				}
				break;
			case ';':
				if (marked) {
					marked = false;
					System.out.print("(c)");
				} else
					System.out.print((char) c);
				break;
			case ' ':
				if (marked) {
					marked = false;
					f.reset();
					System.out.print("&");
				} else
					System.out.print((char) c);
				break;
			default:
				if (!marked)
					System.out.print((char) c);
				break;
			}
		}
	}

}
