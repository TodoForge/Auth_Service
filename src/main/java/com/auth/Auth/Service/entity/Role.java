package com.auth.Auth.Service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Role Name cannot be null")
    @Size(max = 50, message = "Role name must be at most 50 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
}
