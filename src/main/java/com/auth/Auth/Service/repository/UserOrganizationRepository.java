package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.Organization;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserOrganization;
import com.auth.Auth.Service.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UUID> {
    
    // Find all user organizations for a specific user
    List<UserOrganization> findByUser(User user);
    
    // Find all user organizations for a specific organization
    List<UserOrganization> findByOrganization(Organization organization);
    
    // Find specific user-organization relationship
    Optional<UserOrganization> findByUserAndOrganization(User user, Organization organization);
    
    // Find active user organizations for a user
    List<UserOrganization> findByUserAndIsActiveTrue(User user);
    
    // Find active user organizations for an organization
    List<UserOrganization> findByOrganizationAndIsActiveTrue(Organization organization);
    
    // Find users by role in organization
    List<UserOrganization> findByOrganizationAndRoleAndIsActiveTrue(Organization organization, OrganizationRole role);
    
    // Check if user is member of organization
    boolean existsByUserAndOrganizationAndIsActiveTrue(User user, Organization organization);
    
    // Find user role in organization
    @Query("SELECT uo.role FROM UserOrganization uo " +
           "WHERE uo.user = :user AND uo.organization = :organization AND uo.isActive = true")
    Optional<OrganizationRole> findRoleByUserAndOrganization(@Param("user") User user, @Param("organization") Organization organization);
    
    // Count active members in organization
    @Query("SELECT COUNT(uo) FROM UserOrganization uo " +
           "WHERE uo.organization = :organization AND uo.isActive = true")
    Long countActiveMembersByOrganization(@Param("organization") Organization organization);
    
    // Find organizations where user has specific role
    @Query("SELECT uo.organization FROM UserOrganization uo " +
           "WHERE uo.user = :user AND uo.role = :role AND uo.isActive = true")
    List<Organization> findOrganizationsByUserAndRole(@Param("user") User user, @Param("role") OrganizationRole role);
}
