package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.dto.response.PurposeResponse;
import com.auth.Auth.Service.entity.Purpose;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.entity.UserPurpose;
import com.auth.Auth.Service.repository.PurposeRepository;
import com.auth.Auth.Service.repository.UserPurposeRepository;
import com.auth.Auth.Service.repository.UserRepository;
import com.auth.Auth.Service.service.PurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurposeServiceImpl implements PurposeService {
    
    private final PurposeRepository purposeRepository;
    private final UserPurposeRepository userPurposeRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<PurposeResponse> getAllPurposes() {
        return purposeRepository.findAll().stream()
                .map(purpose -> PurposeResponse.builder()
                        .id(purpose.getId())
                        .name(purpose.getName())
                        .description(purpose.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void selectPurpose(User user, String purposeName) {
        Purpose purpose = purposeRepository.findByName(purposeName)
                .orElseThrow(() -> new RuntimeException("Purpose not found: " + purposeName));
        
        // Update user's purpose directly
        user.setPurpose(com.auth.Auth.Service.enums.Purpose.valueOf(purposeName));
        userRepository.save(user);
        
        // Also create UserPurpose relationship
        if (!userPurposeRepository.existsByUserAndPurpose(user, purpose)) {
            UserPurpose userPurpose = UserPurpose.builder()
                    .user(user)
                    .purpose(purpose)
                    .build();
            userPurposeRepository.save(userPurpose);
        }
    }
}
