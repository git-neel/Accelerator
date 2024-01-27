package lakhmani.neelabh.accelerator.controller;

import lakhmani.neelabh.accelerator.dto.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lakhmani.neelabh.accelerator.dto.GenerationRequest;
import lakhmani.neelabh.accelerator.ModuleGenerator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/")
public class ModuleGenerationController {

    @Autowired
    private ModuleGenerator moduleGenerator;

    @PostMapping("/generate-module")
    public ResponseEntity<?> generateModule(@RequestBody GenerationRequest request) {
        try {
            String entityName = request.getEntityName();
            List<Property> properties = request.getProperties();

            // Generate the module
            moduleGenerator.generateModuleThroughJSON(entityName, properties);

            // Return a success response
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Return an error response if generation fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate module: " + e.getMessage());
        }
    }

    @RequestMapping("/download-module")
    public ResponseEntity<byte[]> downloadModule(@RequestBody GenerationRequest request) {
        try {
            String entityName = request.getEntityName();
            List<Property> properties = request.getProperties();
            File generatedModule = moduleGenerator.generateModule(entityName, properties);

            // Check if the generated module file exists
            if (!generatedModule.exists()) {
                // Return 404 Not Found if the file does not exist
                return ResponseEntity.notFound().build();
            }

            // Get the generated module file
            Path moduleFilePath = generatedModule.toPath();

            // Read the module file as byte array
            byte[] moduleFileBytes = Files.readAllBytes(moduleFilePath);

            // Set the response headers for downloading the file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", entityName+"-module.zip");
            headers.setContentLength(moduleFileBytes.length);

            // Return the file bytes with appropriate headers
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(moduleFileBytes);
        } catch (Exception e) {
            // Convert the error message to bytes
            byte[] errorMessageBytes = ("Failed to download module: " + e.getMessage()).getBytes();

            // Return an error response with the error message as bytes
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessageBytes);
        }
    }
}
