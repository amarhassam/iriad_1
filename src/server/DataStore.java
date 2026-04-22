package server;

import common.Appointment;
import common.Person;
import common.VaccinationRecord;

import java.io.*;
import java.util.*;

public class DataStore {
    private final String dataDir;
    private final String personsFile;
    private final String appointmentsFile;
    private final String recordsFile;
    private final String counterFile;

    public DataStore(String dataDir) {
        this.dataDir = dataDir;
        this.personsFile = dataDir + "/persons.dat";
        this.appointmentsFile = dataDir + "/appointments.dat";
        this.recordsFile = dataDir + "/records.dat";
        this.counterFile = dataDir + "/counter.dat";
        new File(dataDir).mkdirs();
    }

    public synchronized int nextId() throws IOException {
        int current = 1;
        File f = new File(counterFile);
        if (f.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                current = (int) in.readObject();
            } catch (ClassNotFoundException e) {
                current = 1;
            }
        }
        int next = current + 1;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(next);
        }
        return current;
    }

    public synchronized Map<String, Person> loadPersons() {
        return loadMap(personsFile);
    }

    public synchronized void savePersons(Map<String, Person> m) {
        saveMap(personsFile, m);
    }

    public synchronized Map<String, Appointment> loadAppointments() {
        return loadMap(appointmentsFile);
    }

    public synchronized void saveAppointments(Map<String, Appointment> m) {
        saveMap(appointmentsFile, m);
    }

    public synchronized Map<String, VaccinationRecord> loadRecords() {
        return loadMap(recordsFile);
    }

    public synchronized void saveRecords(Map<String, VaccinationRecord> m) {
        saveMap(recordsFile, m);
    }

    @SuppressWarnings("unchecked")
    private <K, V> Map<K, V> loadMap(String path) {
        File f = new File(path);
        if (!f.exists())
            return new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (Map<K, V>) in.readObject();
        } catch (Exception e) {
            System.err.println("Warning: could not load " + path + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    private <K, V> void saveMap(String path, Map<K, V> map) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(map);
        } catch (IOException e) {
            System.err.println("Error saving " + path + ": " + e.getMessage());
        }
    }
}