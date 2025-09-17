package practice.moduleuser.user.dto;

import practice.moduleuser.user.UserRole;

public record UserUpdateRequest(
        String name,
        String password,
        UserRole role
) {}

