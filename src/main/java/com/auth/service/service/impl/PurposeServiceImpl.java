package com.auth.service.service.impl;

import com.auth.service.dto.response.PurposeResponse;
import com.auth.service.entity.Purpose;
import com.auth.service.entity.User;
import com.auth.service.entity.UserPurpose;
import com.auth.service.repository.PurposeRepository;
import com.auth.service.repository.UserPurposeRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.service.PurposeService;
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
        user.setPurpose(com.auth.service.enums.Purpose.valueOf(purposeName));
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
