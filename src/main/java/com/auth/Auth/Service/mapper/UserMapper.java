package com.auth.Auth.Service.mapper;

import com.auth.Auth.Service.dto.request.SignupRequest;
import com.auth.Auth.Service.dto.request.UpdateUserRequest;
import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.dto.response.UpdateUserResponse;
import com.auth.Auth.Service.entity.User;

public class UserMapper {

    public static User toEntity(SignupRequest request){
        if(request == null) return null;
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .username(request.getUsername())
                .password(request.getPassword())
                .dateOfBirth(request.getDateOfBirth())
                .purpose(request.getPurpose())
                .department(request.getDepartment())
                .workEmail(request.getWorkEmail())
                .workLocation(request.getWorkLocation())
                .companyAddress(request.getCompanyAddress())
                .companyName(request.getCompanyName())
                .companyWebsite(request.getCompanyWebsite())
                .industry(request.getIndustry())
                .roleInCompany(request.getRoleInCompany())
                .build();
    }

    public static SignupResponse toResponse(User user){
        if(user==null) return null;
        return SignupResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .username(user.getUsername())
                .purpose(user.getPurpose())
                .isActive(user.isActive())
                .build();
    }

}
