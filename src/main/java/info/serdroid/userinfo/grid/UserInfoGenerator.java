package info.serdroid.userinfo.grid;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

public class UserInfoGenerator {

	void generateFile() throws IOException {
		long start = System.currentTimeMillis();
		String passwd = "1234567890abcdef1234567890abcdef";
		String seperator = "#";
		String file = "userinfo.txt";
		RandomPINGenerator pinGenerator = new RandomPINGenerator((byte) 10);
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		int partitionCount = 100;
		int totalUsers = 3602000;
		int innerLoop = totalUsers / partitionCount;
		int partitionId = 0;
		int userId = 0;
		for (int jjx = 0; jjx < partitionCount; ++jjx) {
			for (int idx = 0; idx < innerLoop; ++idx) {
				String pin = pinGenerator.generateString();
				++userId;
				writer.write(String.format(Locale.US, "%08d", userId)); // userid
				writer.write(seperator);
				writer.write(passwd); // passwd
				writer.write(seperator);
				writer.write(passwd); // watchwd
				writer.write(seperator);
				writer.write("Lynyrd"); // name
				writer.write(seperator);
				writer.write("Skynyrd"); // lastname
				writer.write(seperator);
				writer.write(pin.concat("0")); // personid
				writer.write(seperator);
				writer.write(pin); // accountid
				writer.write(seperator);
				writer.write("0"); // user type
				writer.write(seperator);
				writer.write("0"); // account state
				writer.write(seperator);
				writer.write("20180305145100"); // last updated
				writer.write(seperator);
				writer.write(String.valueOf(partitionId)); // partition id
				writer.write("\n");
			}
			++partitionId;
		}
		writer.close();
		long stop = System.currentTimeMillis();
		System.out.println("total time=" + (stop - start));
	}
	
	public static void main(String[] args) throws IOException {
		UserInfoGenerator generator = new UserInfoGenerator();
		generator.generateFile();
	}

}
