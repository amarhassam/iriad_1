package server;

import common.VaccinationService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            String dataDir = args.length > 0 ? args[0] : "data";
            DataStore store = new DataStore(dataDir);
            VaccinationService service = new VaccinationServiceImpl(store);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("VaccinationService", service);
            System.out.println("==============================================");
            System.out.println("  Vaccination RMI Server started on port 1099");
            System.out.println("  Data directory: " + dataDir);
            System.out.println("  Service bound as 'VaccinationService'");
            System.out.println("==============================================");
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
