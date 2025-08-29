import java.util.Scanner;  //may be necessary for input
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;  //may be necessary for input

import kareltherobot.*;

public class Driver implements Directions {
// declared here so it is visible in all the methods!! 
// It will be assigned a value in the getInfo method
	private static Robot roomba; 
	private static int width, height, area;


	// You will add very many variables!!

	
	public static void main(String[] args) throws FileNotFoundException {
			// LEAVE THIS ALONE!!!!!!
			Driver d = new Driver();

  /**
	 * This section of code gets info from the user in the following order: 1. Ask the user
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
    	

	/** This section will have all the logic that takes the Robot to every location 
	 * and cleans up all piles of beepers.  Think about ways you can break this
	 * large, complex task into smaller, easier to solve problems.
	 */

		// the line below causes a null pointer exception
		// what is that and why are we getting it?basicRoom.


  	/** This method displays the results of cleaning the room.  All of the info
	 * in the pdf that describes the problem need to be displayed.  You can present
	 * this info in the console (boring) or you can present using JOptionPane (cool!)
	 */


		//Reading the World file name
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

		Robot roomba = new Robot(start_street, start_avenue, North, 0);
		roomba.move();

		System.out.println("Robot ready at (" + start_street + ", " + start_avenue + ") facing " + start_direction + ".");
        System.out.println("World File Name is: " + worldName);

        // Close scanners (optional; JVM exit will close stdin)
        strworldScanner.close();
        aveworldScanner.close();
        dirworldScanner.close();

		width = World.numberOfAvenues();     // avenues (x)
		height = World.numberOfStreets();   // streets (y)
		area = width * height;
		System.out.println("World has a width of" + width + " and a height of " + height + " and in total " + area + ".");

		// First turn east
		while (roomba.direction() != East) roomba.turnLeft();

        while (roomba.frontIsClear()) {
			roomba.move();
			//clean the cell
		}

		return;


  }
}
