package com.auth.Auth.Service.service;

import com.auth.Auth.Service.dto.response.PurposeResponse;
import com.auth.Auth.Service.entity.User;

import java.util.List;

public interface PurposeService {
    List<PurposeResponse> getAllPurposes();
    void selectPurpose(User user, String purposeName);
}
