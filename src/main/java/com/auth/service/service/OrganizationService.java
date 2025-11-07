package com.auth.service.service;

import com.auth.service.dto.request.CreateOrganizationRequest;
import com.auth.service.dto.request.UpdateOrganizationRequest;
import com.auth.service.dto.response.OrganizationResponse;
import com.auth.service.entity.Organization;
import com.auth.service.entity.User;
import com.auth.service.enums.OrganizationRole;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    
    // Create organization
    Organization createOrganization(CreateOrganizationRequest request, User creator);
    
    // Update organization
    Organization updateOrganization(UUID id, UpdateOrganizationRequest request, User user);
    
    // Delete organization
    void deleteOrganization(UUID id, User user);
    
    // Get user's organizations
    List<Organization> getUserOrganizations(User user);
    
    // Get organization by ID
    Organization getOrganizationById(UUID id);
    
    // Add user to organization
    void addUserToOrganization(UUID organizationId, UUID userId, OrganizationRole role, User admin);
    
    // Remove user from organization
    void removeUserFromOrganization(UUID organizationId, UUID userId, User admin);
    
    // Update user role in organization
    void updateUserRole(UUID organizationId, UUID userId, OrganizationRole role, User admin);
    
    // Check if user has access to organization
    boolean hasAccessToOrganization(User user, UUID organizationId);
    
    // Check if user is admin of organization
    boolean isAdminOfOrganization(User user, UUID organizationId);
    
    // Get organization members
    List<User> getOrganizationMembers(UUID organizationId);
    
    // Get organization admins
    List<User> getOrganizationAdmins(UUID organizationId);
}
