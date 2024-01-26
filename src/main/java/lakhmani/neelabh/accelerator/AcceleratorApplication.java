package lakhmani.neelabh.accelerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;


@SpringBootApplication
public class AcceleratorApplication{
        public static void main(String[] args) {
            SpringApplication.run(AcceleratorApplication.class, args);

            if (args.length == 0) {
                // If no command-line arguments are provided, start UI or perform any other logic
                // For simplicity, let's print a message indicating that UI can be accessed
                System.out.println("Application started. Access the UI to generate modules.");
            } else {
                // If command-line arguments are provided, proceed with module generation
                String entityName = args[0];
                // Generate module with the provided entity name
                // You might want to add validation or error handling here
                new ModuleGenerator().generateModule(entityName);
            }
        }
}
