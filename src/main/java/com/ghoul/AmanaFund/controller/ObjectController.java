package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.service.DTOObjectMapper;
import com.ghoul.AmanaFund.service.ObjectGDTOMapper;
import com.ghoul.AmanaFund.service.ObjectService;
import com.ghoul.AmanaFund.service.PersonDTOMapper;
import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/object")
public class ObjectController {
    private final ObjectGDTOMapper objectGDTOMapper;
    @Autowired
    private ObjectService objectService;
    @PostMapping(value = "/add_objectG", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ObjectG> addObject(
            @RequestPart("object") ObjectGDTO objectGDTO,
            @RequestPart("file") MultipartFile file) throws IOException {

        if (objectGDTO == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create the directory if it doesn't exist
        String uploadDir = "uploads/object-documents/";
        Files.createDirectories(Paths.get(uploadDir));

        // Generate the filename (based on ownership certificate or timestamp fallback)
        String filename = "object_" + objectGDTO.getOwnershipCertifNumber() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, filename);
        Files.write(filePath, file.getBytes());

        // Set the relative file path in the DTO
        objectGDTO.setDocuments(filePath.toString());

        // Save the object
        ObjectG savedObject = objectService.addDTOObject(objectGDTO);
        return ResponseEntity.ok(savedObject);
    }
    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> downloadObjectDocument(@PathVariable Long id, HttpServletResponse response) {
        try {
            ObjectGDTO objectG = objectService.retrieveObjectG(id);

            if (objectG == null || objectG.getDocuments() == null || objectG.getDocuments().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(objectG.getDocuments());

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) mimeType = "application/pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getall_object")
    public List<ObjectGDTO> GetAllObject()
    {
        return objectService.retrieveObjectGs();
    }
    @PutMapping(value = "/update_object", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ObjectGDTO> updateObject(
            @RequestPart("object") ObjectGDTO objectGDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        if (objectGDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        // If a new file was provided, save it and update the DTO’s documents path
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/object-documents/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = "object_"
                    + objectGDTO.getOwnershipCertifNumber()
                    + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            objectGDTO.setDocuments(filePath.toString());
        }

        // Perform update via service
        ObjectG updated = objectService.updateObjectGFromDTO(objectGDTO);
        ObjectGDTO responseDto = objectGDTOMapper.apply(updated);

        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/remove_object/{id}")
    public void removeObject(@PathVariable long id) {
        objectService.removeObjectG(id);
    }
    @GetMapping("/paginated")
    public Page<ObjectGDTO> getPaginatedPerson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ownershipCertifNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Allow only "start" or "end" for sorting
        List<String> allowedSortFields = Arrays.asList("estimatedValue", "ownershipCertifNumber" , "type");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field. Choose between 'name' or 'age' or 'revenue'.");
        }

        // Apply sorting direction
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return objectService.getAllPaginated(pageable);
    }
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ObjectG> deactivatePerson(@PathVariable Long id) {
        try {
            ObjectG updatedPolice = objectService.deactivatePerson(id);
            return new ResponseEntity<>(updatedPolice, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
