package com.barelabor.barelabor.debug;

import java.util.Random;

public class Debug {
	public static final boolean isOn = false;

	public static final long REQUEST_DELAY = 0;
	public static final String TRANSLATION_PREFIX = "";

	private static final int FAIL_FACTOR = 2; // fail every second time (randomly)
	private static Random random = new Random();

	public static final boolean fail() {
		if (isOn) {
			return random.nextInt() % FAIL_FACTOR == 0;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unused")
	public static void delayRequest() {
		if (Debug.REQUEST_DELAY > 0) {
			try {
				Thread.sleep(Debug.REQUEST_DELAY);
			} catch (InterruptedException e) {
				Log.e(Debug.class, e);
			}
		}
	}
}
