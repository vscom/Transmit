package com.bvcom.transmit.test.task;

import java.util.Timer;
import java.util.TimerTask;

public class EggTimer {
	private final Timer timer = new Timer();
	private final int minutes;

	public EggTimer(int minutes) {
		this.minutes = minutes;
	}

	public void start() {
		timer.schedule(new TimerTask() {
			public void run() {
				playSound();
				timer.cancel();
			}

			private void playSound() {
				System.out.println("Your egg is ready!");
				// Start a new thread to play a sound... 
			}
		}, minutes * 60 * 1000);
	}

	public static void main(String[] args) {
		int time = 1;
		System.out.println("Will Start Egg " + time + " minutes later");
		EggTimer eggTimer = new EggTimer(time);
		eggTimer.start();
	}

}
