package com.auth.service.service.impl;

import com.auth.service.dto.request.CreateOrganizationRequest;
import com.auth.service.dto.request.UpdateOrganizationRequest;
import com.auth.service.entity.Organization;
import com.auth.service.entity.User;
import com.auth.service.entity.UserOrganization;
import com.auth.service.enums.OrganizationRole;
import com.auth.service.repository.OrganizationRepository;
import com.auth.service.repository.UserOrganizationRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;

    @Override
    public Organization createOrganization(CreateOrganizationRequest request, User creator) {
        // Check if organization name already exists
        if (organizationRepository.existsByName(request.getName())) {
            throw new RuntimeException("Organization with this name already exists");
        }

        // Create organization
        Organization organization = Organization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .industry(request.getIndustry())
                .companySize(request.getCompanySize())
                .website(request.getWebsite())
                .address(request.getAddress())
                .createdBy(creator)
                .isActive(true)
                .build();

        Organization savedOrganization = organizationRepository.save(organization);

        // Add creator as admin
        UserOrganization userOrganization = UserOrganization.builder()
                .user(creator)
                .organization(savedOrganization)
                .role(OrganizationRole.ADMIN)
                .isActive(true)
                .build();

        userOrganizationRepository.save(userOrganization);

        return savedOrganization;
    }

    @Override
    public Organization updateOrganization(UUID id, UpdateOrganizationRequest request, User user) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Check if user has admin access
        if (!isAdminOfOrganization(user, id)) {
            throw new RuntimeException("You don't have permission to update this organization");
        }

        // Update fields if provided
        if (request.getName() != null) {
            if (organizationRepository.existsByName(request.getName()) && 
                !organization.getName().equals(request.getName())) {
                throw new RuntimeException("Organization with this name already exists");
            }
            organization.setName(request.getName());
        }
        if (request.getDescription() != null) {
            organization.setDescription(request.getDescription());
        }
        if (request.getIndustry() != null) {
            organization.setIndustry(request.getIndustry());
        }
        if (request.getCompanySize() != null) {
            organization.setCompanySize(request.getCompanySize());
        }
        if (request.getWebsite() != null) {
            organization.setWebsite(request.getWebsite());
        }
        if (request.getAddress() != null) {
            organization.setAddress(request.getAddress());
        }

        return organizationRepository.save(organization);
    }

    @Override
    public void deleteOrganization(UUID id, User user) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Check if user is the creator
        if (!organization.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the organization creator can delete the organization");
        }

        // Deactivate organization
        organization.setActive(false);
        organizationRepository.save(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> getUserOrganizations(User user) {
        return organizationRepository.findByUserOrganizationsUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getOrganizationById(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Override
    public void addUserToOrganization(UUID organizationId, UUID userId, OrganizationRole role, User admin) {
        Organization organization = getOrganizationById(organizationId);
        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if admin has permission
        if (!isAdminOfOrganization(admin, organizationId)) {
            throw new RuntimeException("You don't have permission to add users to this organization");
        }

        // Check if user is already a member
        if (userOrganizationRepository.existsByUserAndOrganizationAndIsActiveTrue(userToAdd, organization)) {
            throw new RuntimeException("User is already a member of this organization");
        }

        // Add user to organization
        UserOrganization userOrganization = UserOrganization.builder()
                .user(userToAdd)
                .organization(organization)
                .role(role)
                .isActive(true)
                .build();

        userOrganizationRepository.save(userOrganization);
    }

    @Override
    public void removeUserFromOrganization(UUID organizationId, UUID userId, User admin) {
        Organization organization = getOrganizationById(organizationId);
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if admin has permission
        if (!isAdminOfOrganization(admin, organizationId)) {
            throw new RuntimeException("You don't have permission to remove users from this organization");
        }

        // Find and deactivate user organization relationship
        UserOrganization userOrganization = userOrganizationRepository
                .findByUserAndOrganization(userToRemove, organization)
                .orElseThrow(() -> new RuntimeException("User is not a member of this organization"));

        userOrganization.setActive(false);
        userOrganizationRepository.save(userOrganization);
    }

    @Override
    public void updateUserRole(UUID organizationId, UUID userId, OrganizationRole role, User admin) {
        Organization organization = getOrganizationById(organizationId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if admin has permission
        if (!isAdminOfOrganization(admin, organizationId)) {
            throw new RuntimeException("You don't have permission to update user roles in this organization");
        }

        // Find and update user organization relationship
        UserOrganization userOrganization = userOrganizationRepository
                .findByUserAndOrganization(user, organization)
                .orElseThrow(() -> new RuntimeException("User is not a member of this organization"));

        userOrganization.setRole(role);
        userOrganizationRepository.save(userOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessToOrganization(User user, UUID organizationId) {
        Organization organization = getOrganizationById(organizationId);
        return userOrganizationRepository.existsByUserAndOrganizationAndIsActiveTrue(user, organization);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdminOfOrganization(User user, UUID organizationId) {
        Organization organization = getOrganizationById(organizationId);
        return userOrganizationRepository.existsByUserAndOrganizationAndIsActiveTrue(user, organization) &&
               userOrganizationRepository.findRoleByUserAndOrganization(user, organization)
                       .orElse(null) == OrganizationRole.ADMIN;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getOrganizationMembers(UUID organizationId) {
        Organization organization = getOrganizationById(organizationId);
        return userOrganizationRepository.findByOrganizationAndIsActiveTrue(organization)
                .stream()
                .map(UserOrganization::getUser)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getOrganizationAdmins(UUID organizationId) {
        Organization organization = getOrganizationById(organizationId);
        return userOrganizationRepository.findByOrganizationAndRoleAndIsActiveTrue(organization, OrganizationRole.ADMIN)
                .stream()
                .map(UserOrganization::getUser)
                .toList();
    }
}
