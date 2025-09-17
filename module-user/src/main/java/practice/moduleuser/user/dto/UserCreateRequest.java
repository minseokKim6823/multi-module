package practice.moduleuser.user.dto;

import practice.moduleuser.user.UserRole;

public record UserCreateRequest(
        String email,
        String password,
        String name,
        UserRole role
) {}

