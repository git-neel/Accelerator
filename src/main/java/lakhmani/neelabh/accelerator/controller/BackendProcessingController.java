package lakhmani.neelabh.accelerator.controller;

import lakhmani.neelabh.accelerator.ModuleGenerator;
import lakhmani.neelabh.accelerator.dto.GenerationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendProcessingController {

    @PostMapping("/generate")
    public ResponseEntity<String> generateApplication(@RequestBody GenerationRequest request) {
        try {
            ModuleGenerator moduleGenerator = new ModuleGenerator();

            moduleGenerator.generateModule(request.getEntityName(),request.getProperties());
            return ResponseEntity.ok("Module generation successful.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Module generation failed: " + e.getMessage());

        }
    }
}
