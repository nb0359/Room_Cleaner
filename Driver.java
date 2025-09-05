// package robot; // ← uncomment if Driver.java is inside a `robot/` folder

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import kareltherobot.*;

public class Driver implements Directions {
	public static Robot roomba;
	public static int picked = 0;
	public static double numPiles = 0.0;
	public static int largest_pile_size = 0;
	public static int largest_pile_size_street, largest_pile_size_ave;

	public static Direction horizontalDir = West; // East/West
	public static Direction rowStepDir = South; // (South/North)

	static int minAveVisited; // westmost avenue we actually stepped on
	static int maxStreetAtMinAveVisited; // northmost street on that *same* avenue

	// True NW corner of the enclosure (found by WEST→NORTH probe)
	static int topLeftAve;
	static int topLeftStreet;

	public static int area = 0;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);

		System.out.println("Enter the world file: ");
		String worldName = in.nextLine().trim();

		File worldFile = new File(worldName);
		if (!worldFile.exists()) {
			System.out.println("World file not found: " + worldName);
			return;
		}

		World.readWorld(worldName);
		World.setVisible(true);
		World.setDelay(0); // fastest

		System.out.println("What is the start street? ");
		int start_street = in.nextInt();

		System.out.println("What is the start avenue? ");
		int start_avenue = in.nextInt();

		in.nextLine(); // consume newline
		System.out.println("What is the start direction? ");
		String start_direction = in.nextLine().trim();

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

		area = 1;
		minAveVisited = roomba.avenue();
		maxStreetAtMinAveVisited = roomba.street();

		computeTopLeftNW();

		// Start with east or west
		changeDir(West);
		if (roomba.frontIsClear()) {
			horizontalDir = West;
		} else {
			changeDir(East);
			horizontalDir = East;
		}

		// move to south or noth
		changeDir(South);
		if (roomba.frontIsClear()) {
			rowStepDir = South;
		} else {
			changeDir(North);
			if (roomba.frontIsClear())
				rowStepDir = North;
			else
				rowStepDir = South; // dummy; we’ll break at first check if no vertical movement is possible
		}

		changeDir(horizontalDir);
		cleanCell();

		// Keep looping till frontIsClear
		while (true) {
			// Sweep across current row
			changeDir(horizontalDir);
			while (roomba.frontIsClear()) {
				roomba.move();
				updateVisitedCorner();
				area++;
				cleanCell();
			}

			// exit condition
			changeDir(rowStepDir);
			if (!roomba.frontIsClear()) {
				break;
			}
			roomba.move();
			updateVisitedCorner();
			area++;
			cleanCell();

			if (horizontalDir == West) {
				horizontalDir = East;
			} else {
				horizontalDir = West;
			}
		}

		// ===== Summary =====
		System.out.println("The area is " + area + " square units");
		System.out.println("The total number of piles is " + (int) numPiles);
		System.out.println("The total number of beepers is " + picked);

		System.out.println("The largest pile of beepers has " + largest_pile_size + " beepers");
		System.out.println("Largest pile absolute: (street=" + largest_pile_size_street +
				", ave=" + largest_pile_size_ave + ")");

		int right = largest_pile_size_ave - topLeftAve;
		int down = topLeftStreet - largest_pile_size_street;
		System.out.println("The largest pile (from top left corner) is right " + right + " and down " + down);

		System.out.println("The average pile size " + picked / numPiles);
		System.out.println("The percent dirty is " + numPiles / area);

		// error during exit
		try {
			Thread.sleep(150);
		} catch (InterruptedException ignored) {
		}
		System.exit(0);
	}

	// keep turning till we get desired direction
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

	// minAveVisited = westmost avenue reached,
	// maxStreetAtMinAveVisited = northmost street on that same avenue
	static void updateVisitedCorner() {
		int a = roomba.avenue();
		int s = roomba.street();

		if (a < minAveVisited) {
			minAveVisited = a;
			maxStreetAtMinAveVisited = s;
		} else if (a == minAveVisited && s > maxStreetAtMinAveVisited) {
			maxStreetAtMinAveVisited = s;
		}
	}

	// Find the true NW corner of the current enclosure: go WEST to a wall, then
	// NORTH to a wall; return to start. */
	static void computeTopLeftNW() {
		int s0 = roomba.street();
		int a0 = roomba.avenue();

		changeDir(West);
		while (roomba.frontIsClear())
			roomba.move();
		int westAve = roomba.avenue(); // true local west boundary for this enclosure

		changeDir(North);
		while (roomba.frontIsClear())
			roomba.move();
		int northStreet = roomba.street(); // true local north boundary

		topLeftAve = westAve;
		topLeftStreet = northStreet;

		changeDir(South);
		while (roomba.street() > s0 && roomba.frontIsClear())
			roomba.move();
		changeDir(East);
		while (roomba.avenue() < a0 && roomba.frontIsClear())
			roomba.move();

	}
}
