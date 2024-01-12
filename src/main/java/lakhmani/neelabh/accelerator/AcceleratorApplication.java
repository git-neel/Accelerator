package lakhmani.neelabh.accelerator;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SpringBootApplication
public class AcceleratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcceleratorApplication.class, args);
    }


    @Component
    static class CodeGenerator implements CommandLineRunner {

        private final String BASE_PATH = System.getProperty("user.home") + "/%s-module/";

        @Override
        public void run(String... args) {

            if (args.length != 1) {
                System.out.println("Usage: java -jar CodeGeneratorApplication.jar <EntityName>");
                System.exit(1);
            }

            String entityName = args[0];
            generateModule(entityName);
        }


        private void generateModule(String entityName) {
            // Delete the '%s-module' directory if it exists
            deleteEntityModuleDirectory(entityName.toLowerCase());

            // Create module directory
            String modulePath = String.format(BASE_PATH, entityName.toLowerCase());
            File moduleDirectory = Paths.get(modulePath).toFile();
            try {
                if (moduleDirectory.mkdirs()) {
                    System.out.println("Module directory created successfully.");
                } else {
                    System.err.println("Failed to create module directory.");
                    return;  // Exit the method if directory creation fails
                }

                // Generate src/main/java folder and package structure
                String packageName = "com.example." + entityName.toLowerCase();
                String packagePath = packageName.replace(".", "/");
                File javaSrcDir = Paths.get(modulePath, "src/main/java", packagePath).toFile();
                if(javaSrcDir.mkdirs()){
                    System.out.println("src/main/java directory created successfully.");
                } else {
                    System.err.println("Failed to create src/main/java directory.");
                    return;  // Exit the method if directory creation fails
                }

                // Generate application package and class
                String applicationContent = generateApplicationClass(packageName, entityName);
                Path applicationPath = Paths.get(javaSrcDir.toString(), entityName+"CRUDApp.java");
                Util.writeFile(applicationPath, applicationContent);


                // Generate controller package and class
                String controllerPackage = packageName + ".controller";
                String controllerContent = generateController(controllerPackage, entityName);
                Path controllerPath = Paths.get(javaSrcDir.toString(), "controller", entityName + "Controller.java");
                Util.writeFile(controllerPath, controllerContent);

                // Generate entity package and class
                String entityPackage = packageName + ".entity";
                String entityContent = generateEntity(entityPackage, entityName);
                Path entityPath = Paths.get(javaSrcDir.toString(), "entity", entityName + ".java");
                Util.writeFile(entityPath, entityContent);
                // Generate service package and class
                String servicePackage = packageName + ".service";
                String serviceContent = generateService(servicePackage, entityName);
                Path servicePath = Paths.get(javaSrcDir.toString(), "service", entityName + "Service.java");
                Util.writeFile(servicePath, serviceContent);

                // Generate repository package and class
                String repositoryPackage = packageName + ".repository";
                String repositoryContent = generateRepository(repositoryPackage, entityName);
                Path repositoryPath = Paths.get(javaSrcDir.toString(), "repository", entityName + "Repository.java");
                Util.writeFile(repositoryPath, repositoryContent);

                // Generate resources folder and application.properties
                File resourcesDir = Paths.get(modulePath, "src/main/resources").toFile();
                if(resourcesDir.mkdirs()){
                    System.out.println("Resources directory created successfully.");
                } else {
                    System.err.println("Failed to create resources directory.");
                    return;  // Exit the method if directory creation fails
                }
                String propertiesContent = generateProperties(entityName);
                Path propertiesPath = Paths.get(resourcesDir.toString(), "application.properties");
                Util.writeFile(propertiesPath, propertiesContent);

                // Generate pom.xml template
                String pomFileContent = generatePomFile(entityName);
                Path pomFilePath = Paths.get(modulePath, "pom.xml");
                Util.writeFile(pomFilePath, pomFileContent);

                // Zip the directory
                Util.zipDirectory(moduleDirectory, modulePath + ".zip");


                // Terminate the application after successfully generating the module
                System.exit(0);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error occurred. Deleting generated module directory.");
                deleteDirectory(moduleDirectory);
            }
        }


        private String generateApplicationClass(String packageName, String entityName) {
            return String.format("""
        package %s;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;

        @SpringBootApplication
        class %sEntityCRUDApp {

            public static void main(String[] args) {
                SpringApplication.run(%sEntityCRUDApp.class, args);
            }
        }
        """,
                    packageName,entityName,entityName
            );
        }


        private void deleteDirectory(File directory) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteDirectory(file);
                    }
                }
            }
            if (!directory.delete()) {
                System.err.println("Failed to delete directory: " + directory.getAbsolutePath());
            }
        }

        private String generatePomFile(String entityName) {
            // You can use a template engine or simple string concatenation
            // to generate the content of the pom.xml file.
            return """
                    <project xmlns="http://maven.apache.org/POM/4.0.0"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                        <modelVersion>4.0.0</modelVersion>
                        <groupId>com.example</groupId>
                        <artifactId>%s</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                        <parent>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-parent</artifactId>
                            <version>2.6.3</version>
                        </parent>

                        <properties>
                            <java.version>17</java.version>
                        </properties>

                        <dependencies>
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-data-jpa</artifactId>
                            </dependency>
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-web</artifactId>
                            </dependency>
                            <!-- MySQL Connector -->
                                    <dependency>
                                        <groupId>mysql</groupId>
                                        <artifactId>mysql-connector-java</artifactId>
                                        <scope>runtime</scope>
                                    </dependency>
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-test</artifactId>
                                <scope>test</scope>
                            </dependency>
                        </dependencies>

                        <build>
                            <plugins>
                                <plugin>
                                    <groupId>org.springframework.boot</groupId>
                                    <artifactId>spring-boot-maven-plugin</artifactId>
                                </plugin>
                            </plugins>
                        </build>
                    </project>
                    """.formatted(entityName);
        }


        private String generateController(String packageName, String entityName) {
            return """
                    package %s;
                                    
                    import com.example.employee.entity.%s;
                    import com.example.employee.service.%sService;
                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.http.ResponseEntity;
                    import org.springframework.web.bind.annotation.*;

                    import java.util.List;

                    @RestController
                    @RequestMapping("/%s")
                    public class %sController {

                        @Autowired
                        private %sService %sService;

                        @GetMapping
                        public ResponseEntity<List<%s>> getAll%s() {
                            List<%s> %ss = %sService.getAll%s();
                            return ResponseEntity.ok(%ss);
                        }

                        @GetMapping("/{id}")
                        public ResponseEntity<%s> get%sById(@PathVariable Long id) {
                            %s %s = %sService.get%sById(id);
                            return ResponseEntity.ok(%s);
                        }

                        @PostMapping
                        public ResponseEntity<%s> create%s(@RequestBody %s %s) {
                            %s created%s = %sService.create%s(%s);
                            return ResponseEntity.ok(created%s);
                        }

                        @PutMapping("/{id}")
                        public ResponseEntity<%s> update%s(@PathVariable Long id, @RequestBody %s %s) {
                            %s updated%s = %sService.update%s(id, %s);
                            return ResponseEntity.ok(updated%s);
                        }

                        @DeleteMapping("/{id}")
                        public ResponseEntity<Void> delete%s(@PathVariable Long id) {
                            %sService.delete%s(id);
                            return ResponseEntity.noContent().build();
                        }
                    }
                    """.formatted(
                    packageName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName
            );
        }

        private String generateService(String packageName, String entityName) {
            return """
                    package %s;
                    
                    import com.example.employee.entity.%s;
                    import com.example.employee.repository.%sRepository;
                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.stereotype.Service;

                    import java.util.List;

                    @Service
                    public class %sService {

                        @Autowired
                        private %sRepository %sRepository;

                        public List<%s> getAll%s() {
                            return %sRepository.findAll();
                        }

                        public %s get%sById(Long id) {
                            return %sRepository.findById(id).orElse(null);
                        }

                        public %s create%s(%s %s) {
                            return %sRepository.save(%s);
                        }

                        public %s update%s(Long id, %s %s) {
                            %s existing%s = %sRepository.findById(id).orElse(null);
                            if (existing%s != null) {
                                // Update entity properties
                                // ...

                                return %sRepository.save(existing%s);
                            }
                            return null;
                        }

                        public void delete%s(Long id) {
                            %sRepository.deleteById(id);
                        }
                    }
                    """.formatted(
                    packageName,entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName,
                    entityName, entityName, entityName
                    );
        }



        private String generateProperties(String entityName) {
            return """
            # DataSource Configuration for MySQL
            spring.datasource.url=jdbc:mysql://localhost:3306/%s
            spring.datasource.username=root
            spring.datasource.password=Mankii@24

            # Hibernate Properties
            spring.jpa.hibernate.ddl-auto=update
            spring.jpa.show-sql=true
            spring.jpa.properties.hibernate.format_sql=true

            # Server Configuration
            server.port=9090
            """.formatted(entityName.toLowerCase());
        }

        private String generateRepository(String packageName, String entityName) {
            return """
                package %s;
                
                import com.example.employee.entity.%s;
                import org.springframework.data.jpa.repository.JpaRepository;

                /**
                 * Repository for managing %s entities.
                 */
                public interface %sRepository extends JpaRepository<%s, Long> {

                    // Additional custom queries...

                }
                """.formatted(packageName, entityName, entityName, entityName, entityName);
        }
        private String generateEntity(String packageName, String entityName) {
            return """
            package %s;

            import javax.persistence.Entity;
            import javax.persistence.GeneratedValue;
            import javax.persistence.GenerationType;
            import javax.persistence.Id;

            /**
             * Entity class for %s.
             */
            @Entity
            public class %s {

                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Long id;

                // Add fields corresponding to your entity properties
                // Example:
                // private String name;
                // private int age;

                // Add getters and setters for your fields
                // Example:
                // public String getName() {
                //     return name;
                // }
                //
                // public void setName(String name) {
                //     this.name = name;
                // }

                // Add toString() method for better logging and debugging

                // Customize equals() and hashCode() methods if needed

                // Add additional business logic if needed
            }
            """.formatted(packageName, entityName, entityName);
        }
        private void deleteEntityModuleDirectory(String entityName) {
            String entityModulePath = String.format(BASE_PATH, entityName);
            File entityModuleDirectory = Paths.get(entityModulePath).toFile();

            if (entityModuleDirectory.exists()) {
                try {
                    FileUtils.deleteDirectory(entityModuleDirectory);
                    System.out.println("Deleted existing '%s-module' directory.".formatted(entityName));
                } catch (IOException e) {
                    System.err.println("Failed to delete '%s-module' directory: %s".formatted(entityName, e.getMessage()));
                }
            }
        }

    }
    static class Util {
        static void writeFile(Path path, String content) throws Exception {
            Path parentDir = path.getParent();
            if (!parentDir.toFile().exists()) {
                if(parentDir.toFile().mkdirs()){
                    System.out.println(parentDir+" directory created successfully.");
                } else {
                    System.err.println("Failed to create "+parentDir+" directory.");
                    return;  // Exit the method if directory creation fails
                }

            }
            java.nio.file.Files.write(path, content.getBytes());
        }

        public static void zipDirectory(File sourceDirectory, String destinationZipFile) {
            try {
                FileOutputStream fos = new FileOutputStream(destinationZipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                zipDirectoryRecursive(sourceDirectory, sourceDirectory, zos);
                zos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        private static void zipDirectoryRecursive(File rootDir, File source, ZipOutputStream zos) throws IOException {
            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        zipDirectoryRecursive(rootDir, file, zos);
                    } else {
                        String entryName = rootDir.toPath().relativize(file.toPath()).toString();
                        ZipEntry zipEntry = new ZipEntry(entryName);
                        zos.putNextEntry(zipEntry);

                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        }


    }



}
