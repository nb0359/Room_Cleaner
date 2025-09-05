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
	public static double numPiles = 0.0;
	public static int largest_pile_size = 0;
	public static int largest_pile_size_street, largest_pile_size_ave;
	public static Direction prev_east_west = West;
	private static int width, height;
	static int minAve, maxStreet; // top-left = (maxStreet, minAve)
	public static int area = 0;

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
		World.setDelay(0); // speed

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
		minAve = roomba.avenue();
		maxStreet = roomba.street();

		System.out
				.println("Robot ready at (" + start_street + ", " + start_avenue + ") facing " + start_direction + ".");
		System.out.println("World File Name is: " + worldName);

		// Close scanners
		strworldScanner.close();
		aveworldScanner.close();
		dirworldScanner.close();

		width = World.numberOfAvenues();
		height = World.numberOfStreets();
		area = 1;

		changeDir(West);
		cleanCell();

		while (true) {
			// go through current row
			while (roomba.frontIsClear()) {
				roomba.move();
				minAve = Math.min(minAve, roomba.avenue());
				maxStreet = Math.max(maxStreet, roomba.street());
				area++;
				cleanCell();
			}

			// go down
			changeDir(South);
			// exit if cant go down
			if (!roomba.frontIsClear()) {
				// can't go down -> finished
				break;
			}

			roomba.move();
			minAve = Math.min(minAve, roomba.avenue());
			maxStreet = Math.max(maxStreet, roomba.street());
			area++;
			cleanCell();

			if (prev_east_west == West) {
				changeDir(East);
				prev_east_west = East;
			} else {
				changeDir(West);
				prev_east_west = West;
			}
		}
		System.out.println("The area is " + area + " square units");
		System.out.println("The total number of piles is " + numPiles);
		System.out.println("The total number of beepers is " + picked);
		System.out.println("The largest pile of beepers has " + largest_pile_size + " beepers");
		System.out.println("The largest pile (from top left corner) is right " + (largest_pile_size_ave - minAve)
				+ " and down " + (maxStreet - largest_pile_size_street));
		System.out.println("The average pile size is  " + picked / numPiles);
		System.out.println("The percent dirty is  " + numPiles / area);
		try {
			Thread.sleep(200);
		} catch (InterruptedException ignored) {
		}
		System.exit(0);

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
		if (current_pile_size >= 1) {
			numPiles++;
		}
		if (current_pile_size > largest_pile_size) {
			largest_pile_size = current_pile_size;
			largest_pile_size_street = roomba.street();
			largest_pile_size_ave = roomba.avenue();
		}
	}
}
