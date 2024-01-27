package lakhmani.neelabh.accelerator;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lakhmani.neelabh.accelerator.dto.Property;

@Component
public class ModuleGenerator {

    private final String BASE_PATH = System.getProperty("user.home") + "/%s-module/";
    public void generateModule(String entityName) {
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

            // Generate entity package and class
            String entityPackage = packageName + ".entity";
            String entityContent = generateEntity(entityPackage, entityName,new CommandLineProperties().getPropertiesFromUser());
            Path entityPath = Paths.get(javaSrcDir.toString(), "entity", entityName + ".java");
            Util.writeFile(entityPath, entityContent);

            // Generate application package and class
            String applicationContent = generateApplicationClass(packageName, entityName);
            Path applicationPath = Paths.get(javaSrcDir.toString(), entityName+"CRUDApp.java");
            Util.writeFile(applicationPath, applicationContent);


            // Generate controller package and class
            String controllerPackage = packageName + ".controller";
            String controllerContent = generateController(controllerPackage, entityName);
            Path controllerPath = Paths.get(javaSrcDir.toString(), "controller", entityName + "Controller.java");
            Util.writeFile(controllerPath, controllerContent);


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

            // Generate src/main/java/config folder
            String configPackage = packageName + ".config";
            Path config = Paths.get(javaSrcDir.toString(), "config");
            String configPath = config.toString();
            File configDir = config.toFile();
            if (configDir.mkdirs()) {
                System.out.println("src/main/java/config directory created successfully.");
            } else {
                System.err.println("Failed to create src/main/java/config directory.");
                return;  // Exit the method if directory creation fails
            }

            // Generate SwaggerConfig class
            String swaggerConfigContent = generateSwaggerConfig(configPackage, entityName);
            Path swaggerConfigPath = Paths.get(configPath, "SwaggerConfig.java");
            Util.writeFile(swaggerConfigPath, swaggerConfigContent);


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

    public void generateModuleThroughJSON(String entityName,List<Property> properties) {
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

            // Generate entity package and class
            String entityPackage = packageName + ".entity";
            String entityContent = generateEntity(entityPackage, entityName,properties);
            Path entityPath = Paths.get(javaSrcDir.toString(), "entity", entityName + ".java");
            Util.writeFile(entityPath, entityContent);

            // Generate application package and class
            String applicationContent = generateApplicationClass(packageName, entityName);
            Path applicationPath = Paths.get(javaSrcDir.toString(), entityName+"CRUDApp.java");
            Util.writeFile(applicationPath, applicationContent);


            // Generate controller package and class
            String controllerPackage = packageName + ".controller";
            String controllerContent = generateController(controllerPackage, entityName);
            Path controllerPath = Paths.get(javaSrcDir.toString(), "controller", entityName + "Controller.java");
            Util.writeFile(controllerPath, controllerContent);


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

            // Generate src/main/java/config folder
            String configPackage = packageName + ".config";
            Path config = Paths.get(javaSrcDir.toString(), "config");
            String configPath = config.toString();
            File configDir = config.toFile();
            if (configDir.mkdirs()) {
                System.out.println("src/main/java/config directory created successfully.");
            } else {
                System.err.println("Failed to create src/main/java/config directory.");
                return;  // Exit the method if directory creation fails
            }

            // Generate SwaggerConfig class
            String swaggerConfigContent = generateSwaggerConfig(configPackage, entityName);
            Path swaggerConfigPath = Paths.get(configPath, "SwaggerConfig.java");
            Util.writeFile(swaggerConfigPath, swaggerConfigContent);


            // Zip the directory
            Util.zipDirectory(moduleDirectory, modulePath + ".zip");


            // Terminate the application after successfully generating the module
            //System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error occurred. Deleting generated module directory.");
            deleteDirectory(moduleDirectory);
        }
    }
    public File generateModule(String entityName,List<Property> properties) {
        String modulePath = String.format(BASE_PATH, entityName.toLowerCase());

        // Create the directory for saving the generated module
        File moduleDirectory = new File(modulePath);
        if (!moduleDirectory.exists()) {
            if (!moduleDirectory.mkdirs()) {
                System.err.println("Failed to create module directory.");
                return null;
            }
        }
        try {
            // Generate the module files in the temporary directory
            generateModuleFiles(entityName, properties, modulePath);

            // Zip the directory
            String zipFilePath = modulePath + ".zip";
            Util.zipDirectory(moduleDirectory, zipFilePath);

            // Return the File object representing the generated zip file
            return new File(zipFilePath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error occurred while generating module. Deleting generated module directory.");
            deleteDirectory(moduleDirectory);
            return null;
        }
    }

    private void generateModuleFiles(String entityName, List<Property> properties, String modulePath) throws IOException {
        // Generate src/main/java folder and package structure
        String packageName = "com.example." + entityName.toLowerCase();
        String packagePath = packageName.replace(".", "/");
        File javaSrcDir = Paths.get(modulePath, "src/main/java", packagePath).toFile();
//        if(javaSrcDir.mkdirs()){
//            System.out.println("src/main/java directory created successfully.");
//        } else {
//            System.err.println("Failed to create src/main/java directory.");
//            return;  // Exit the method if directory creation fails
//        }

        // Generate entity package and class
        String entityPackage = packageName + ".entity";
        String entityContent = generateEntity(entityPackage, entityName,properties);
        Path entityPath = Paths.get(javaSrcDir.toString(), "entity", entityName + ".java");
        Util.writeFile(entityPath, entityContent);

        // Generate application package and class
        String applicationContent = generateApplicationClass(packageName, entityName);
        Path applicationPath = Paths.get(javaSrcDir.toString(), entityName+"CRUDApp.java");
        Util.writeFile(applicationPath, applicationContent);


        // Generate controller package and class
        String controllerPackage = packageName + ".controller";
        String controllerContent = generateController(controllerPackage, entityName);
        Path controllerPath = Paths.get(javaSrcDir.toString(), "controller", entityName + "Controller.java");
        Util.writeFile(controllerPath, controllerContent);


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

        // Generate src/main/java/config folder
        String configPackage = packageName + ".config";
        Path config = Paths.get(javaSrcDir.toString(), "config");
        String configPath = config.toString();
        File configDir = config.toFile();
        if (configDir.mkdirs()) {
            System.out.println("src/main/java/config directory created successfully.");
        } else {
            System.err.println("Failed to create src/main/java/config directory.");
            return;  // Exit the method if directory creation fails
        }

        // Generate SwaggerConfig class
        String swaggerConfigContent = generateSwaggerConfig(configPackage, entityName);
        Path swaggerConfigPath = Paths.get(configPath, "SwaggerConfig.java");
        Util.writeFile(swaggerConfigPath, swaggerConfigContent);



    }
    private String generateApplicationClass(String packageName, String entityName) {
        return String.format("""
        package %s;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import springfox.documentation.swagger2.annotations.EnableSwagger2;

        @SpringBootApplication
        @EnableSwagger2
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

        // Include Swagger dependencies by default
        String dependenciesSection = """
                    <dependencies>
                        <dependency>
                            <groupId>io.springfox</groupId>
                            <artifactId>springfox-boot-starter</artifactId>
                            <version>3.0.0</version>
                        </dependency>
                        <dependency>
                            <groupId>io.springfox</groupId>
                            <artifactId>springfox-swagger-ui</artifactId>
                            <version>3.0.0</version>
                        </dependency>    
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
                    """;

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

                %s <!-- Add dependencies section dynamically -->

                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """.formatted(entityName, dependenciesSection);
    }


    private String generateController(String packageName, String entityName) {
        return """
                    package %s;
                                    
                    import com.example.%s.entity.%s;
                    import com.example.%s.service.%sService;
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
                packageName, entityName.toLowerCase(), entityName,entityName.toLowerCase(),entityName,
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
                    
                    import com.example.%s.entity.%s;
                    import com.example.%s.repository.%sRepository;
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
                packageName,entityName.toLowerCase(), entityName,entityName.toLowerCase(), entityName,
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
                                
                    #Swagger configuration
                    spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER
                    """.formatted(entityName.toLowerCase());
    }

    private String generateRepository(String packageName, String entityName) {
        return """
                package %s;
                
                import com.example.%s.entity.%s;
                import org.springframework.data.jpa.repository.JpaRepository;

                /**
                 * Repository for managing %s entities.
                 */
                public interface %sRepository extends JpaRepository<%s, Long> {

                    // Additional custom queries...

                }
                """.formatted(packageName, entityName.toLowerCase(),entityName, entityName, entityName, entityName);
    }
    private String generateEntity(String packageName, String entityName, List<Property> properties) {
        StringBuilder entityContent = new StringBuilder();
        entityContent.append("package ").append(packageName).append(";\n\n");
        entityContent.append("import javax.persistence.Entity;\n");
        entityContent.append("import javax.persistence.GeneratedValue;\n");
        entityContent.append("import javax.persistence.GenerationType;\n");
        entityContent.append("import javax.persistence.Id;\n\n");

        entityContent.append("@Entity\n");
        entityContent.append("public class ").append(entityName).append(" {\n\n");

        // Generate fields based on user-provided properties
        for (Property property : properties) {
            // Check if the property name is "id" and annotate with @Id and @GeneratedValue
            if ("id".equalsIgnoreCase(property.name())) {
                entityContent.append("    @Id\n");
                entityContent.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            }
            entityContent.append("    private ").append(property.dataType()).append(" ").append(property.name()).append(";\n");
        }

        // Generate getters and setters based on user-provided properties
        for (Property property : properties) {
            entityContent.append("\n    public ").append(property.dataType()).append(" get").append(Character.toUpperCase(property.name().charAt(0))).append(property.name().substring(1)).append("() {\n");
            entityContent.append("        return ").append(property.name()).append(";\n");
            entityContent.append("    }\n\n");

            entityContent.append("    public void set").append(Character.toUpperCase(property.name().charAt(0))).append(property.name().substring(1)).append("(").append(property.dataType()).append(" ").append(property.name()).append(") {\n");
            entityContent.append("        this.").append(property.name()).append(" = ").append(property.name()).append(";\n");
            entityContent.append("    }\n\n");
        }

        // Add additional methods if needed

        entityContent.append("}\n");

        return entityContent.toString();
    }
    // Add this method in your EntityGenerator class
    private static String generateSwaggerConfig(String packageName, String entityName) {
        return """
            package %s;

            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import springfox.documentation.builders.PathSelectors;
            import springfox.documentation.builders.RequestHandlerSelectors;
            import springfox.documentation.spi.DocumentationType;
            import springfox.documentation.spring.web.plugins.Docket;
            

            @Configuration
            public class SwaggerConfig {
                @Bean
                public Docket api() {
                    return new Docket(DocumentationType.SWAGGER_2)
                        .select()
                        .apis(RequestHandlerSelectors.any())
                        .paths(PathSelectors.any())
                        .build();
                }
            }
            """.formatted(
                packageName, packageName + "." + entityName
        );
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
