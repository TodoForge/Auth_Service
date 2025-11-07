package com.auth.service.service;

import com.auth.service.dto.response.PurposeResponse;
import com.auth.service.entity.User;

import java.util.List;

public interface PurposeService {
    List<PurposeResponse> getAllPurposes();
    void selectPurpose(User user, String purposeName);
}
