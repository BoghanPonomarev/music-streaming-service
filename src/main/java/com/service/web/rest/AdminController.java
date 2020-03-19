package com.service.web.rest;

import com.service.dao.AdminTokenRepository;
import com.service.exception.NotAuthorizedException;
import com.service.web.dto.AdminTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AdminTokenRepository adminTokenRepository;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> getPlaylist(@RequestBody AdminTokenDto adminTokenDto) {
        adminTokenRepository.findByToken(adminTokenDto.getToken())
                .orElseThrow(() -> new NotAuthorizedException("No such token"));

        return ResponseEntity.ok("OK");
    }

}
