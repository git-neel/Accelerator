package lakhmani.neelabh.accelerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
    public class CodeGenerator implements CommandLineRunner {

    @Autowired
    ModuleGenerator moduleGenerator;

        @Override
        public void run(String... args) {

            if (args.length != 1) {
                System.out.println("Usage: java -jar CodeGeneratorApplication.jar <EntityName>");
                System.exit(1);
            }

            String entityName = args[0];
            moduleGenerator.generateModule(entityName);
        }

    }