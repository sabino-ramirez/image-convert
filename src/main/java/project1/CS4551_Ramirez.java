package project1;

import java.util.Scanner;

public class CS4551_Ramirez {
  //how to use the app
	public static void usage() {
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}

  //this function handles main menu functionality
  public static int menu() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Main Menu---------------------");
    System.out.println("1. Conversion to Gray-Scale image (24bits->8bits)");
    System.out.println("2. Conversion to Binary Image using Ordered Dithering(k=4)");
    System.out.println("3. Conversion to 8bit Indexed Color Image using Uniform Color Quantization");
    System.out.println("4. Quit");
    System.out.println("Please enter the task number [1-4]");

    //gets the user decision and returns it 
    int decision = 0;
    int input = scanner.nextInt();
    for(int i = 1; i < 5; i++) {
      if (input == i) {
        decision = i;
        break;
      }
    }

    scanner.close();
    return decision;
  }

	public static void main(String[] args) {
		// the program expects one command line argument
		// if there is no command line argument, exit the program
		if (args.length != 1) {
			usage();
			System.exit(1);
		}

		// Create an Image object with the input PPM file name.
    String inputFileName = args[0];
		MImage img = new MImage(inputFileName);
		System.out.println(img);

    //get decision. if its 4, exit
    int decision = menu();
    if (decision == 4) {
      System.exit(1);
    }
		System.out.println(decision);

    int x = 10, y = 10;
    img.printPixel(x, y);
    int[] rgb = new int[3];
    img.getPixel(x, y, rgb);
    System.out.println("rgb pixel val at (x, y): " + rgb[0] + rgb[1] + rgb[2]);
    rgb[0] = 255;
    rgb[1] = 0;
    rgb[2] = 0;
    img.setPixel(x, y, rgb);
    img.getPixel(x, y, rgb);
    System.out.println("rgb pixel val at (x, y): " + rgb[0] + rgb[1] + rgb[2]);

    img.write2PPM(inputFileName.replace(".", "-gray."));
    // String outputFileName = inputFileName.replace(".", "-gray.");
    // System.out.println(outputFileName);

	}

}

