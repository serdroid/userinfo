package info.serdroid.userinfo.grid;

import java.util.Locale;
import java.util.Random;

public class RandomPINGenerator {
	byte digits;
	long upperBound;
	Random random;

	public RandomPINGenerator(byte digits) {
		this.digits = digits;
		upperBound = (long) Math.pow(10, digits);
		random = new Random();
	}

	long generateLong() {
		return Math.abs(random.nextLong() % upperBound);
	}

	String generateString() {
		long randomLong = generateLong();
		String formatString = "%0" + digits + "d";
		return String.format(Locale.US, formatString, randomLong);
	}

}
