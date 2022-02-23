package project1;

import java.util.Scanner;

public class CS4551_Ramirez {
	//how to use the app
	public static void usage() {
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}

	//displays the main menu
	public static void menu() {
		System.out.println("\n" + "__________________________" + "\n");
		System.out.println("Main Menu");
		System.out.println("1. Conversion to Gray-Scale image (24bits->8bits)");
		System.out.println("2. Conversion to Binary Image using Ordered Dithering(k=4)");
		System.out.println("3. Conversion to 8bit Indexed Color Image using Uniform Color Quantization");
		System.out.println("4. Quit");
		System.out.println("Please enter the task number [1-4]");
		System.out.println("___________________________");
	}

	public static void main (String [] args) {
		String grayImg;
		Helpers helpers = new Helpers();
		Scanner input = new Scanner(System.in);
		int decision;
		
		while(true) {
			menu();
			decision = input.nextInt();

			switch(decision) {
				case 1: 
					System.out.println("**********" + "\n" + "Converting image to Gray-Scale:" + "\n" + "**********");
					helpers.makeGray(args[0]);
					System.out.println("\n" + "Select another option or press 4 to exit.");
					break;
				
				case 2: 
					System.out.println("**********" + "\n" + "Converting to Binary Image:" + "\n" + "**********");
					grayImg = helpers.makeGray(args[0]);
					helpers.orderDith(grayImg);
					System.out.println("\n" + "Select another option or press 4 to exit.");
					break;

				case 3: 
					System.out.println("**********" + "\n" + "Converting 24-bit color to 8-bit index image:" + "\n" + "**********");
					System.out.println("Making new image from quantized RGB pixels:" + "\n" + "**********");
					helpers.ucq(args[0]);
					System.out.println("\n" + "Select another option or press 4 to exit.");
					break;

				case 4: 
					System.out.println("Exiting..." + "\n" + "Goodbye.");
					System.exit(1);
			}
		}
	}
}
