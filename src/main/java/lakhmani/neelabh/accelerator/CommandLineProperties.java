package lakhmani.neelabh.accelerator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class CommandLineProperties {
    public List<Property> getPropertiesFromUser() {
        Scanner scanner = new Scanner(System.in);
        List<Property> properties = new ArrayList<>();
        boolean entityGenerationSuccessful = false;

        try {
            System.out.println("Enter the number of properties for your entity (1-100):");
            int attempts = 0;

            while (attempts < 3) {
                if (scanner.hasNextInt()) {
                    int numProperties = scanner.nextInt();

                    if (numProperties < 1 || numProperties > 100) {
                        System.err.println("Invalid input. Please provide a numeric value between 1 and 100.");
                        attempts++;
                    } else {
                        // Clear the buffer to avoid infinite loop
                        scanner.nextLine();

                        for (int i = 0; i < numProperties; i++) {
                            System.out.println("Enter the name of property " + (i + 1) + ":");
                            String propertyName = scanner.nextLine();

                            // Validate property name
                            if (!isValidPropertyName(propertyName)) {
                                System.err.println("Invalid property name. Please follow Java naming conventions.");
                                i--; // Retry the current property
                                continue;
                            }

                            System.out.println("Enter the data type for property " + propertyName + ":");
                            String dataType = getValidDataType(scanner);

                            if (dataType == null) {
                                i--; // Retry the current property
                                continue;
                            }

                            properties.add(new Property(propertyName, dataType));
                        }

                        // If we reach here, properties were successfully entered
                        entityGenerationSuccessful = true;
                        break;
                    }
                } else {
                    System.err.println("Invalid input. Please provide a valid number.");
                    // Clear the buffer to avoid infinite loop
                    scanner.nextLine();
                    attempts++;

                    if (attempts == 3) {
                        // If three unsuccessful attempts, abort the operation and stop Spring Boot
                        System.err.println("Exceeded the maximum number of attempts. Aborting the operation.");
                        System.exit(1);
                    }
                }
            }
        } finally {
            // Close the scanner to avoid resource leaks
            scanner.close();
        }

        if (!entityGenerationSuccessful || properties.isEmpty()) {
            // Handle the case where entity generation was not successful or no properties were entered
            System.err.println("No properties were entered. Entity generation aborted.");
        }

        return properties;
    }

    private boolean isValidPropertyName(String propertyName) {
        // Validate property name according to Java naming conventions
        return propertyName.matches("^[a-z][a-zA-Z0-9]*$");
    }


    private String getValidDataType(Scanner scanner) {
        int dataTypeAttempts = 0;

        while (dataTypeAttempts < 3) {
            String dataType = scanner.nextLine();

            // Validate data type: Only allow Java wrapper classes
            if (isValidDataType(dataType)) {
                return dataType;
            }

            System.err.println("Invalid data type. Please provide a valid Java wrapper class.");
            dataTypeAttempts++;

            if (dataTypeAttempts == 3) {
                // If three unsuccessful attempts, return null to indicate failure
                System.err.println("Exceeded the maximum number of attempts for data type. Aborting the operation.");
                System.exit(1);
            }
        }

        return null;
    }

    private boolean isValidDataType(String dataType) {
        // Add your custom validation for Java wrapper classes here
        // For simplicity, we'll allow some common wrapper classes
        return Set.of("Integer", "Long", "Float", "Double", "Boolean", "String", "Character").contains(dataType);
    }

}
