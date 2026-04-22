package common;
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
public class VaccinationRecord implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private final String personId;
    private final List<String> vaccinations;
 
    public VaccinationRecord(String personId) {
        this.personId = personId;
        this.vaccinations = new ArrayList<>();
    }
 
    public String personId()           { return personId; }
    public List<String> vaccinations() { return vaccinations; }
 
    public void addVaccination(String date, String vaccineName, int doseNumber, String center) {
        vaccinations.add(date + " | " + vaccineName + " | Dose " + doseNumber + " | " + center);
    }
 
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("=== Vaccination Record [").append(personId).append("] ===\n");
        if (vaccinations.isEmpty()) {
            sb.append("  No vaccinations recorded yet.\n");
        } else {
            for (int i = 0; i < vaccinations.size(); i++)
                sb.append("  ").append(i + 1).append(". ").append(vaccinations.get(i)).append("\n");
        }
        return sb.toString();
    }
}