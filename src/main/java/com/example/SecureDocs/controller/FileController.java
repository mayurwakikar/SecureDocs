package com.example.SecureDocs.controller;

import com.example.SecureDocs.model.Document;
import com.example.SecureDocs.model.User;
import com.example.SecureDocs.repository.DocumentRepository;
import com.example.SecureDocs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(currentPrincipalName);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            try {
                Document document = new Document();
                document.setFileName(file.getOriginalFilename());
                document.setContentType(file.getContentType());
                document.setSize(file.getSize());
                document.setUploadDate(LocalDateTime.now());
                document.setData(file.getBytes());
                document.setUser(user);

                documentRepository.save(document);
                redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload file. Please try again.");
                e.printStackTrace();
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found. Cannot upload file.");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewFile(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(currentPrincipalName);

        if (userOptional.isPresent()) {
            Optional<Document> documentOptional = documentRepository.findByIdAndUserId(id, userOptional.get().getId());
            if (documentOptional.isPresent()) {
                Document document = documentOptional.get();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getFileName() + "\"")
                        .contentType(MediaType.valueOf(document.getContentType()))
                        .body(document.getData());
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(currentPrincipalName);

        if (userOptional.isPresent()) {
            Optional<Document> documentOptional = documentRepository.findByIdAndUserId(id, userOptional.get().getId());
            if (documentOptional.isPresent()) {
                Document document = documentOptional.get();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                        .contentType(MediaType.valueOf(document.getContentType()))
                        .body(document.getData());
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(currentPrincipalName);

        if (userOptional.isPresent()) {
            Optional<Document> documentOptional = documentRepository.findByIdAndUserId(id, userOptional.get().getId());
            if (documentOptional.isPresent()) {
                documentRepository.delete(documentOptional.get());
                redirectAttributes.addFlashAttribute("message", "File deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Document not found or access denied.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/search")
    public String searchFiles(@RequestParam("query") String query, Authentication authentication, Model model) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user != null) {
            List<Document> documents = documentRepository.findByFileNameContainingIgnoreCaseAndUser(query, user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("documents", documents);
            model.addAttribute("searchQuery", query);
        }
        return "dashboard";
    }
}