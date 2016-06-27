package com.barelabor.barelabor.debug;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
	private static final boolean ON = true;
	
	public static boolean isDebugOn(){
		
		return ON;
		
	}
	
	public static void i(Object source, String format, Object... params) {
		if (ON) {
			print(android.util.Log.INFO, source, format, params);
		}
	}

	public static void d(Object source, String format, Object... params) {
		if (ON) {
			print(android.util.Log.DEBUG, source, format, params);
		}
	}

	public static void v(Object source, String format, Object... params) {
		if (ON) {
			print(android.util.Log.VERBOSE, source, format, params);
		}
	}

	public static void e(Object source, String format, Object... params) {
		if (ON) {
			print(android.util.Log.ERROR, source, format, params);
		}
	}

	public static void e(Object source, Throwable e) {
		if (ON) {
			StringWriter writer;
			writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			
			print(android.util.Log.ERROR, source, "%1$s\n%2$s", e.toString(), writer.toString());
		}
	}

	public static void print(int priority, Object source, String format, Object... params) {
		String message;

		message = String.format(format, params);

		android.util.Log.println(priority, source.getClass().getSimpleName(), message);
	}

}
