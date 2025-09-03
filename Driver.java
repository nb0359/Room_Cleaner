import java.util.Scanner; //may be necessary for input
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane; //may be necessary for input

import kareltherobot.*;

public class Driver implements Directions {
	// declared here so it is visible in all the methods!!
	// It will be assigned a value in the getInfo method
	public static Robot roomba;
	public static int picked = 0;
	public static int numPiles = 0;
	public static int largest_pile_size = 0;
	public static int largest_pile_size_street, largest_pile_size_ave;

	private static int width, height, area;

	// You will add very many variables!!

	public static void main(String[] args) throws FileNotFoundException {
		// LEAVE THIS ALONE!!!!!!
		Driver d = new Driver();

		/**
		 * This section of code gets info from the user in the following order: 1. Ask
		 * the user
		 * which world file they wish to process. Right now, that world file name is
		 * hardcoded in. 2. Ask the user for the starting location and direction of the
		 * Robot. A new Robot should be constructed and assigned to the global
		 * (instance) variable named roomba that is declared on line 10.
		 * 
		 * An inelegant way to interact with the user is via the console, using
		 * System.out.println and a Scanner that scans System.in (input from the
		 * console). A more elegant way to get user input might include using a
		 * JOptionPane.
		 */

		/**
		 * This section will have all the logic that takes the Robot to every location
		 * and cleans up all piles of beepers. Think about ways you can break this
		 * large, complex task into smaller, easier to solve problems.
		 */

		// the line below causes a null pointer exception
		// what is that and why are we getting it?basicRoom.

		/**
		 * This method displays the results of cleaning the room. All of the info
		 * in the pdf that describes the problem need to be displayed. You can present
		 * this info in the console (boring) or you can present using JOptionPane
		 * (cool!)
		 */

		// Reading the World file name
		System.out.println("The biggest pile was ");
		Scanner scworldScanner = new Scanner(System.in);
		System.out.println("Enter the world file: ");
		String worldName = scworldScanner.nextLine().trim();

		File worldFile = new File(worldName);
		Scanner sc_file = new Scanner(worldFile);
		if (!worldFile.exists()) {
			System.out.println("World file not found: " + worldName);
			return;
		}
		while (sc_file.hasNextLine()) {
			String line = sc_file.nextLine();
			System.out.println(line);
		}

		World.readWorld(worldName);
		World.setVisible(true);

		System.out.println("What is the start street? ");
		Scanner strworldScanner = new Scanner(System.in);
		int start_street = strworldScanner.nextInt();
		System.out.println("The start street is: " + start_street);

		System.out.println("What is the start avenue? ");
		Scanner aveworldScanner = new Scanner(System.in);
		int start_avenue = aveworldScanner.nextInt();
		System.out.println("The start avenue is: " + start_avenue);

		System.out.println("What is the start direction? ");
		Scanner dirworldScanner = new Scanner(System.in);
		String start_direction = dirworldScanner.nextLine();
		System.out.println("The start direction is: " + start_direction);
		Direction dir = East;
		if (start_direction.startsWith("N"))
			dir = North;
		else if (start_direction.startsWith("E"))
			dir = East;
		else if (start_direction.startsWith("S"))
			dir = South;
		else if (start_direction.startsWith("W"))
			dir = West;

		roomba = new Robot(start_street, start_avenue, dir, 0);

		System.out
				.println("Robot ready at (" + start_street + ", " + start_avenue + ") facing " + start_direction + ".");
		System.out.println("World File Name is: " + worldName);

		// Close scanners
		strworldScanner.close();
		aveworldScanner.close();
		dirworldScanner.close();

		width = World.numberOfAvenues();
		height = World.numberOfStreets();
		area = width * height;
		System.out.println("World has a width of" + width + " and a height of " + height + " and in total " + area + ".");

		changeDir(West);

		while (roomba.frontIsClear()) {
			cleanCell();
			roomba.move();
		}

		changeDir(South);
		roomba.move();
		changeDir(East);
		while (roomba.frontIsClear()){
			cleanCell();
			roomba.move();
		}
			//roomba.move();
		changeDir(South);
		roomba.move();
		changeDir(West);
		while (roomba.frontIsClear()){
			cleanCell();
			roomba.move();
		}
			//roomba.move();
		changeDir(South);
		roomba.move();
		changeDir(East);
		while (roomba.frontIsClear()){
			cleanCell();
			roomba.move();
		}
		changeDir(South);
		roomba.move();
		changeDir(West);
		while (roomba.frontIsClear()){
			cleanCell();
			roomba.move();
		}
		

	}

	public static void changeDir(Direction dir) {
		while (roomba.direction() != dir) {
			roomba.turnLeft();
		}
	}

	public static void cleanCell() {
		int current_pile_size = 0;
		
		while (roomba.nextToABeeper()) {
			picked++;
			current_pile_size++;
			roomba.pickBeeper();	
		}
		if (current_pile_size >= 1 ) {
			numPiles++;
		}
		if (current_pile_size > largest_pile_size) {
			largest_pile_size = current_pile_size;
			largest_pile_size_street = roomba.street();
			largest_pile_size_ave = roomba.avenue();
		}
		
		System.out.println("Picked " + picked + " Number of piles " +  numPiles + " Largest Pile Size " + largest_pile_size);
		System.out.println("Largest pile ave " + largest_pile_size_ave + " Largest pile street" + largest_pile_size_street);
	}
}
