package server;

import common.*;
import common.VaccinationService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VaccinationServiceImpl extends UnicastRemoteObject implements VaccinationService {
    private static final long serialVersionUID = 1L;
    private final DataStore store;

    public VaccinationServiceImpl(DataStore store) throws RemoteException {
        super();
        this.store = store;
    }

    @Override
    public synchronized String registerPerson(String firstName, String lastName,
            String dateOfBirth, List<String> diseases)
            throws RemoteException {
        try {
            String id = String.format("VAC-%05d", store.nextId());
            var person = new Person(id, firstName, lastName, dateOfBirth);
            if (diseases != null)
                diseases.forEach(d -> person.addDisease(d.trim()));

            var persons = store.loadPersons();
            persons.put(id, person);
            store.savePersons(persons);

            System.out.println("[SERVER] Registered: " + person);
            return "SUCCESS: Person registered with ID = " + id;
        } catch (Exception e) {
            throw new RemoteException("Error registering person: " + e.getMessage());
        }
    }

    @Override
    public synchronized String scheduleAppointment(String personId, String date,
            String time, String center)
            throws RemoteException {
        var persons = store.loadPersons();
        if (!persons.containsKey(personId))
            return "ERROR: Person " + personId + " not found.";

        var appointments = store.loadAppointments();
        var appt = new Appointment(personId, date, time, center);
        appointments.put(personId, appt);
        store.saveAppointments(appointments);

        System.out.println("[SERVER] Appointment: " + appt);
        return "SUCCESS: Appointment scheduled for " + personId + " on " + date + " at " + time + " (" + center + ")";
    }

    @Override
    public synchronized String createVaccinationRecord(String personId) throws RemoteException {
        if (!store.loadPersons().containsKey(personId))
            return "ERROR: Person " + personId + " not found.";

        var records = store.loadRecords();
        if (records.containsKey(personId))
            return "INFO: Vaccination record already exists for " + personId + ".";

        records.put(personId, new VaccinationRecord(personId));
        store.saveRecords(records);

        System.out.println("[SERVER] Carnet created for: " + personId);
        return "SUCCESS: Vaccination record (carnet) created for " + personId + ".";
    }

    @Override
    public List<String> getDiseases(String personId) throws RemoteException {
        var person = store.loadPersons().get(personId);
        if (person == null)
            return List.of("ERROR: Person " + personId + " not found.");
        var diseases = person.diseases();
        return diseases.isEmpty() ? List.of("No diseases recorded for " + personId + ".") : diseases;
    }

    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Set<String> HIGH_RISK_KEYWORDS = Set.of(
            "diabetes", "hypertension", "asthma", "cancer", "cardiac", "heart",
            "chronic", "respiratory", "kidney", "liver", "immune", "pulmonary");

    @Override
    public boolean isEligibleForVaccination(String personId) throws RemoteException {
        if (!store.loadPersons().containsKey(personId))
            return false;

        var record = store.loadRecords().get(personId);
        if (record != null && record.vaccinations().size() >= 2)
            return false;

        var person = store.loadPersons().get(personId);
        if (person == null)
            return false;

        return hasPriorityCondition(person);
    }

    private boolean hasPriorityCondition(Person person) {
        if (hasHighRiskDisease(person))
            return true;

        int age = calculateAge(person.dateOfBirth());
        return age >= 60 || age <= 18;
    }

    private boolean hasHighRiskDisease(Person person) {
        return person.diseases().stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .anyMatch(disease -> HIGH_RISK_KEYWORDS.stream().anyMatch(disease::contains));
    }

    private int calculateAge(String dateOfBirth) {
        try {
            var birthDate = LocalDate.parse(dateOfBirth, DOB_FORMATTER);
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

    @Override
    public synchronized String updateVaccinationRecord(String personId, String date,
            String vaccineName, int doseNumber,
            String center) throws RemoteException {
        if (!store.loadPersons().containsKey(personId))
            return "ERROR: Person " + personId + " not found.";

        var records = store.loadRecords();
        var record = records.get(personId);
        if (record == null)
            return "ERROR: No carnet found for " + personId + ". Please create one first.";

        record.addVaccination(date, vaccineName, doseNumber, center);
        store.saveRecords(records);

        System.out.println("[SERVER] Updated carnet for " + personId + ": " + vaccineName + " dose " + doseNumber);
        return "SUCCESS: Vaccination record updated for " + personId + ".\n" + record;
    }

    @Override
    public java.util.List<String> listAllPersons() throws RemoteException {
        var persons = store.loadPersons();
        if (persons.isEmpty())
            return List.of("No persons registered.");
        return persons.values().stream()
                .sorted((p1, p2) -> p1.id().compareTo(p2.id()))
                .map(p -> {
                    var diseases = p.diseases();
                    var diseaseLabel = diseases.isEmpty() ? "None" : String.join(", ", diseases);
                    return p.id() + " | " + p.firstName() + " " + p.lastName() + " | " + p.dateOfBirth()
                            + " | Diseases: " + diseaseLabel;
                })
                .toList();
    }
}