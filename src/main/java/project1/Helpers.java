package image_convert;

import java.util.HashMap;

public class Helpers {

	//hash map to store index as key and [r, g, b] as values
	HashMap<Integer, int[]> lutMap = new HashMap<>();

	//for converting to gray-scale
	public String makeGray(String image) {
		MImage img = new MImage(image);
		int gray;
		int[] rgb = new int[3];
		
		//set rgb values of current pixel to corresponding gray value
		for(int i = 0; i < img.getW(); i++) {
			for(int j = 0; j < img.getH(); j++) {
				img.getPixel(i, j, rgb);
				gray = (int) Math.floor((0.299 * rgb[0]) + (0.587 * rgb[1]) + (0.114 * rgb[2]));
				if (gray > 255) gray = 255;
				if (gray < 0) gray = 0;
				for(int k = 0; k < rgb.length; k++) rgb[k] = gray;
				img.setPixel(i, j, rgb);
			}
		}
		
		//write new pixels to output file
		String grayImg = image.replace(".", "-gray.");
		img.write2PPM(grayImg);
		return grayImg;
	}
	
	//for ordered dithering 
	public void orderDith(String img) {
		MImage image = new MImage(img);
		int[][] dMatrix = 	{{0, 8, 2, 10}, 
							{12, 4, 14, 6}, 
							{3, 11, 1, 9}, 
							{15, 17, 13, 5}};
		
		int[] rgb = new int[3];
		int k = 4;
		
		//compare current pixel to dMatrix element
		//if pixel > element -> turn on pixel
		//else turn off
		for(int i = 0; i < image.getW(); i++) {
			for(int j = 0; j < image.getH(); j++) {
				image.getPixel(i, j, rgb);
				int x = i % k;
				int y = j % k;

				if((rgb[0]*k*(k+1))/256 < dMatrix[x][y]) {
					for(int z = 0; z < rgb.length; z++) rgb[z] = 0;
					image.setPixel(i, j, rgb);
				}

				if((rgb[0]*k*(k+1))/256 > dMatrix[x][y]) {
					for(int z = 0; z < rgb.length; z++) rgb[z] = 255;
					image.setPixel(i, j, rgb);
				} 
			}
		}
		
		//write new image to output file
		String odImg = img.toString().replace("-gray","-OD4");
		image.write2PPM(odImg);
	}
	
	//method to display LUT with alignment
	//also populates the lutMap hashmap
	public void displayLUT() {
		int dec, r, g, b;
		
		//string builder to hold the 8 bit binaryStringary string
		StringBuilder binaryString = new StringBuilder();

		System.out.println("\n" + "**********" + "\n" + "LUT" + "\n" + "**********");
		System.out.println("\n" + "Index|" + "R  |" + " G  |" + " B" + "\n" + "_____|___|____|____");
		System.out.println("     |   |    |");

		for(dec = 0; dec < 256; dec++) {
			//add 0's to binaryStringary string to make it 8 bit
			binaryString.append(Integer.toBinaryString(dec));
			while((binaryString.length() % 8) != 0) binaryString.insert(0, '0');
	
			/*
			 * get rgb values from binaryStringary string
			 * first 3 bits -> r
			 * next 3 bits -> g
			 * last 2 bits -> b
			 */
			r = Integer.parseInt(binaryString.substring(0, 3), 2);
			g = Integer.parseInt(binaryString.substring(3, 6), 2);
			b = Integer.parseInt(binaryString.substring(6, 8), 2);

			//get rgb LUT table values from binaryStringary bits
			int rTableVal = r*32 + 16;
			int gTableVal = g*32 + 16;
			int bTableVal = b*64 + 32;
	
			int[] rgbValues = {rTableVal, gTableVal , bTableVal};
	
			//add index and values to hash map
			lutMap.put(dec, rgbValues);

			//switch cases for table alignment
			String indexSpaces = "";
			switch (Integer.toString(dec).length()) {
				case 1:
					indexSpaces = "    |";
					break;
				case 2:
					indexSpaces = "   |";
					break;
				case 3:
					indexSpaces = "  |";
					break;
			}
			
			String rSpaces = "";
			switch (Integer.toString(rTableVal).length()) {
				case 2:
					rSpaces = " | ";
					break;
				case 3:
					rSpaces = "| ";
					break;
			}

			String gSpaces = "";
			switch (Integer.toString(gTableVal).length()) {
				case 2:
					gSpaces = " | ";
					break;
				case 3:
					gSpaces = "| ";
					break;
			}

			//reset string builder for next iteration
			binaryString.delete(0, binaryString.length());

			//print the current line
			System.out.println(dec + 
					indexSpaces + lutMap.get(dec)[0] + 
					rSpaces + lutMap.get(dec)[1] + 
					gSpaces + lutMap.get(dec)[2]);
		}
		System.out.println();
	}
	
	//for uniform color quantization conversion
	//makes indexed picture and ucq picture
	public void ucq(String img) {
		MImage indexImg = new MImage(img);
		MImage ucqImg = new MImage(img);
		displayLUT();

		int[] inputPixelRgb = new int[3];
		String[] rgbBinary = new String[3];
		int rgbTableValue;
		int[] indexedRgb = new int[3];
		int[] quantizedRgb = new int[3];

		//double nested for loop handles all functionality
		for(int i = 0; i < indexImg.getW(); i++) {
			for(int j = 0; j < indexImg.getH(); j++) {
				indexImg.getPixel(i, j, inputPixelRgb);
				
				//get rgb values from inputPixelRgb and turn them to binary strings
				rgbBinary[0] = Integer.toBinaryString((int) Math.floor(inputPixelRgb[0] / 32));
				rgbBinary[1] = Integer.toBinaryString((int) Math.floor(inputPixelRgb[1] / 32));
				rgbBinary[2] = Integer.toBinaryString((int) Math.floor(inputPixelRgb[2] / 64));
	
				//this loop adds 0's to the binary string rgb values in case there are
				//not enough bits
				//eg. if rgb[0] or rgb[1] binary string is 01, this loop makes it 001
				for(int k = 0; k < 3; k++) {
					switch(k) {
						case 0:
						case 1:
							while((rgbBinary[k].length() % 3) != 0) rgbBinary[k] = '0' + rgbBinary[k];
							break;
			
						case 2:
							if((rgbBinary[k].length() % 2) != 0) rgbBinary[k] = '0' + rgbBinary[k];
							break;
					}
				}

				//rgbTableValue will combine the binary strings stored in rgbBinary[]
				//and turn the 8bit binary string to an integer and use that as the 
				//lut index value
				rgbTableValue = Integer.parseInt((rgbBinary[0] + rgbBinary[1] + rgbBinary[2]), 2);
				for(int k = 0; k < indexedRgb.length; k++) indexedRgb[k] = rgbTableValue;

				//sets indexedRgb[] values to rgbTableValue (all the same)
				indexImg.setPixel(i, j, indexedRgb);
				
				//sets quantizedRgb[] values to quantized values retrieved from 
				//the lut table at index rgbTableValue
				for(int k = 0; k < lutMap.get(rgbTableValue).length; k++)
					quantizedRgb[k] = lutMap.get(rgbTableValue)[k];

				//set quantized values to new file
				ucqImg.setPixel(i, j, quantizedRgb);
			}
		}
		indexImg.write2PPM(img.replace(".", "-index."));
		ucqImg.write2PPM(img.replace(".", "-QT8."));
	}
}