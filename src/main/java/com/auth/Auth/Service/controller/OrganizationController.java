package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.dto.request.CreateOrganizationRequest;
import com.auth.Auth.Service.dto.request.UpdateOrganizationRequest;
import com.auth.Auth.Service.dto.response.OrganizationResponse;
import com.auth.Auth.Service.entity.Organization;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.enums.OrganizationRole;
import com.auth.Auth.Service.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> createOrganization(
            @RequestBody CreateOrganizationRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Organization organization = organizationService.createOrganization(request, user);
        
        OrganizationResponse response = OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .industry(organization.getIndustry())
                .companySize(organization.getCompanySize())
                .website(organization.getWebsite())
                .address(organization.getAddress())
                .createdBy(organization.getCreatedBy().getFullName())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .isActive(organization.isActive())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getUserOrganizations(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Organization> organizations = organizationService.getUserOrganizations(user);
        
        List<OrganizationResponse> responses = organizations.stream()
                .map(org -> OrganizationResponse.builder()
                        .id(org.getId())
                        .name(org.getName())
                        .description(org.getDescription())
                        .industry(org.getIndustry())
                        .companySize(org.getCompanySize())
                        .website(org.getWebsite())
                        .address(org.getAddress())
                        .createdBy(org.getCreatedBy().getFullName())
                        .createdAt(org.getCreatedAt())
                        .updatedAt(org.getUpdatedAt())
                        .isActive(org.isActive())
                        .build())
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganization(
            @PathVariable UUID id,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        
        if (!organizationService.hasAccessToOrganization(user, id)) {
            return ResponseEntity.status(403).build();
        }
        
        Organization organization = organizationService.getOrganizationById(id);
        
        OrganizationResponse response = OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .industry(organization.getIndustry())
                .companySize(organization.getCompanySize())
                .website(organization.getWebsite())
                .address(organization.getAddress())
                .createdBy(organization.getCreatedBy().getFullName())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .isActive(organization.isActive())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable UUID id,
            @RequestBody UpdateOrganizationRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Organization organization = organizationService.updateOrganization(id, request, user);
        
        OrganizationResponse response = OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .industry(organization.getIndustry())
                .companySize(organization.getCompanySize())
                .website(organization.getWebsite())
                .address(organization.getAddress())
                .createdBy(organization.getCreatedBy().getFullName())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .isActive(organization.isActive())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable UUID id,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        organizationService.deleteOrganization(id, user);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addUserToOrganization(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @RequestParam OrganizationRole role,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        organizationService.addUserToOrganization(id, userId, role, user);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUserFromOrganization(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        organizationService.removeUserFromOrganization(id, userId, user);
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @RequestParam OrganizationRole role,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        organizationService.updateUserRole(id, userId, role, user);
        
        return ResponseEntity.ok().build();
    }
}
