package com.auth.Auth.Service.repository;

import com.auth.Auth.Service.entity.Organization;
import com.auth.Auth.Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    
    // Find organizations created by a specific user
    List<Organization> findByCreatedBy(User user);
    
    // Find organizations where user is a member
    @Query("SELECT DISTINCT o FROM Organization o " +
           "JOIN o.userOrganizations uo " +
           "WHERE uo.user = :user AND uo.isActive = true")
    List<Organization> findByUserOrganizationsUser(@Param("user") User user);
    
    // Find organization by name
    Optional<Organization> findByName(String name);
    
    // Find active organizations
    List<Organization> findByIsActiveTrue();
    
    // Find organizations by industry
    List<Organization> findByIndustry(String industry);
    
    // Check if organization name exists
    boolean existsByName(String name);
    
    // Find organizations created by user and active
    List<Organization> findByCreatedByAndIsActiveTrue(User user);
}
