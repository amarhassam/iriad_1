package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface VaccinationService extends Remote {
        String registerPerson(String firstName, String lastName, String dateOfBirth, List<String> diseases)
                        throws RemoteException;

        String scheduleAppointment(String personId, String date, String time, String center) throws RemoteException;

        String createVaccinationRecord(String personId) throws RemoteException;

        List<String> getDiseases(String personId) throws RemoteException;

        boolean isEligibleForVaccination(String personId) throws RemoteException;

        String updateVaccinationRecord(String personId, String date, String vaccineName, int doseNumber, String center)
                        throws RemoteException;

        java.util.List<String> listAllPersons() throws RemoteException;
}