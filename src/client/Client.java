package client;

import common.VaccinationService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static VaccinationService service;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";

        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            service = (VaccinationService) registry.lookup("VaccinationService");
            System.out.println("Connected to Vaccination RMI Server at " + host);
        } catch (Exception e) {
            System.err.println("Cannot connect to server: " + e.getMessage());
            System.exit(1);
        }

        mainMenu();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("  VACCINATION MANAGEMENT SYSTEM");
            System.out.println("==============================");
            System.out.println("1. Register a person");
            System.out.println("2. Schedule vaccination appointment");
            System.out.println("3. Create vaccination record (carnet)");
            System.out.println("4. Consult diseases of a person");
            System.out.println("5. Check vaccination eligibility");
            System.out.println("6. Update vaccination record");
            System.out.println("7. List all patients");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            try {
                switch (choice) {
                    case "1":
                        registerPerson();
                        break;
                    case "2":
                        scheduleAppointment();
                        break;
                    case "3":
                        createVaccinationRecord();
                        break;
                    case "4":
                        consultDiseases();
                        break;
                    case "5":
                        checkEligibility();
                        break;
                    case "6":
                        updateVaccinationRecord();
                        break;
                    case "7":
                        listAllPatients();
                        break;
                    case "0":
                        System.out.println("Goodbye.");
                        System.exit(0);
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Remote error: " + e.getMessage());
            }
        }
    }

    private static void registerPerson() throws Exception {
        System.out.println("--- Register a Person ---");
        System.out.print("First name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Date of birth (dd/MM/yyyy): ");
        String dob = scanner.nextLine().trim();
        System.out.print("Enter diseases (comma-separated, or press Enter for none): ");
        String diseasesInput = scanner.nextLine().trim();

        List<String> diseases = new ArrayList<>();
        if (!diseasesInput.isEmpty()) {
            for (String d : diseasesInput.split(",")) {
                diseases.add(d.trim());
            }
        }

        String result = service.registerPerson(firstName, lastName, dob, diseases);
        System.out.println(result);
    }

    private static void scheduleAppointment() throws Exception {
        System.out.println("--- Schedule Vaccination Appointment ---");
        System.out.print("Person ID (e.g. VAC-00001): ");
        String personId = scanner.nextLine().trim().toUpperCase();
        System.out.print("Date (dd/MM/yyyy): ");
        String date = scanner.nextLine().trim();
        System.out.print("Time (HH:mm): ");
        String time = scanner.nextLine().trim();
        System.out.print("Vaccination center name: ");
        String center = scanner.nextLine().trim();

        String result = service.scheduleAppointment(personId, date, time, center);
        System.out.println(result);
    }

    private static void createVaccinationRecord() throws Exception {
        System.out.println("--- Create Vaccination Record (Carnet) ---");
        System.out.print("Person ID (e.g. VAC-00001): ");
        String personId = scanner.nextLine().trim().toUpperCase();

        String result = service.createVaccinationRecord(personId);
        System.out.println(result);
    }

    private static void consultDiseases() throws Exception {
        System.out.println("--- Consult Diseases ---");
        System.out.print("Person ID (e.g. VAC-00001): ");
        String personId = scanner.nextLine().trim().toUpperCase();

        List<String> diseases = service.getDiseases(personId);
        System.out.println("Diseases for " + personId + ":");
        for (String d : diseases) {
            System.out.println("  - " + d);
        }
    }

    private static void checkEligibility() throws Exception {
        System.out.println("--- Check Vaccination Eligibility ---");
        System.out.print("Person ID (e.g. VAC-00001): ");
        String personId = scanner.nextLine().trim().toUpperCase();

        boolean eligible = service.isEligibleForVaccination(personId);
        if (eligible) {
            System.out.println("RESULT: Person " + personId + " IS eligible for vaccination.");
        } else {
            System.out.println(
                    "RESULT: Person " + personId + " is NOT eligible (not found, or already fully vaccinated).");
        }
    }

    private static void updateVaccinationRecord() throws Exception {
        System.out.println("--- Update Vaccination Record ---");
        System.out.print("Person ID (e.g. VAC-00001): ");
        String personId = scanner.nextLine().trim().toUpperCase();
        System.out.print("Vaccination date (dd/MM/yyyy): ");
        String date = scanner.nextLine().trim();
        System.out.print("Vaccine name: ");
        String vaccineName = scanner.nextLine().trim();
        System.out.print("Dose number (e.g. 1 or 2): ");
        int doseNumber = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Vaccination center: ");
        String center = scanner.nextLine().trim();

        String result = service.updateVaccinationRecord(personId, date, vaccineName, doseNumber, center);
        System.out.println(result);
    }

    private static void listAllPatients() throws Exception {
        System.out.println("--- List All Patients ---");
        var persons = service.listAllPersons();
        for (String person : persons) {
            System.out.println(person);
        }
        System.out.println("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }
}
