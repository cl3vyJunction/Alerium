package com.alerium.web.rest;

import com.alerium.domain.UserAlerium;
import com.alerium.repository.UserAleriumRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-alerium")
public class UserAleriumResource {

    private final UserAleriumRepository userAleriumRepository;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserAleriumResource(UserAleriumRepository userAleriumRepository) {
        this.userAleriumRepository = userAleriumRepository;
    }

    @GetMapping
    public List<UserAlerium> getAllUserAlerium() {
        return userAleriumRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<UserAlerium> createUserAlerium(@RequestBody UserAlerium userAlerium) {
        userAlerium.setActive(true);
        userAlerium.setResident(false);
        userAlerium.setHashPassword(hashPassword(userAlerium.getHashPassword()));
        userAlerium.setDeleted(false);

        var savedUserAlerium = userAleriumRepository.save(userAlerium);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserAlerium);
    }

    @GetMapping("/residents")
    public List<UserAlerium> getAllResidents() {
        return userAleriumRepository.findByIsResident(true);
    }

    @PatchMapping("/{id}/change-activity")
    public ResponseEntity<?> changeUserAleriumActivity(@PathVariable Long id, @RequestParam Boolean newActivityStatus) {
        Optional<UserAlerium> userAleriumOptional = userAleriumRepository.findById(id);

        if (userAleriumOptional.isPresent()) {
            UserAlerium userAlerium = userAleriumOptional.get();
            userAlerium.setActive(newActivityStatus);
            userAleriumRepository.save(userAlerium);
            return ResponseEntity.ok("Activity status changed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/change-residency")
    public ResponseEntity<?> changeUserAleriumResidency(@PathVariable Long id, @RequestParam Boolean newResidencyStatus) {
        Optional<UserAlerium> userAleriumOptional = userAleriumRepository.findById(id);

        if (userAleriumOptional.isPresent()) {
            UserAlerium userAlerium = userAleriumOptional.get();
            userAlerium.setResident(newResidencyStatus);
            userAleriumRepository.save(userAlerium);
            return ResponseEntity.ok("Residency status changed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
