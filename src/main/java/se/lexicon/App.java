package se.lexicon;

public class App {
    public static void main(String[] args) {
        // Step 1: Create a parking lot with 3 fixed spots.
        ParkingLot parkingLot = new ParkingLot(3);

        // Step 2: Create the attendant who handles cars.
        Attendant attendant = new Attendant("Alex");

        // Step 3: Three cars arrive and get spots.
        Ticket firstTicket = attendant.parkCar(parkingLot, "STH-001");
        attendant.parkCar(parkingLot, "STH-002");
        attendant.parkCar(parkingLot, "STH-003");

        // Step 4: A fourth car arrives, but no spots are free.
        attendant.parkCar(parkingLot, "STH-004");

        // Step 5: Wait shortly so the duration is visible.
        pauseShortly();

        // Step 6: The first car leaves and frees its spot.
        attendant.leaveCar(parkingLot, firstTicket);

        // Step 7: The fourth car tries again and now gets the free spot.
        attendant.parkCar(parkingLot, "STH-004");

        // Step 8: Print the final parking lot status.
        parkingLot.printStatus();
    }

    private static void pauseShortly() {
        try {
            // Small demo pause to avoid printing 0 seconds.
            Thread.sleep(1100);
        } catch (InterruptedException exception) {
            // Restore the interrupt signal if the program is stopped.
            Thread.currentThread().interrupt();
        }
    }
}