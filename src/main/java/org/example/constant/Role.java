package org.example.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Role {

    ADMIN(0, "管理者"),
    GENERAL(1, "一般");

    private final int roleCode;
    private final String label;

    Role(int roleCode, String label) {
        this.roleCode = roleCode;
        this.label = label;
    }

    public static Role getRole(int roleCode) {
        return Arrays.stream(Role.values())
                .filter(data -> data.getRoleCode() == roleCode)
                .findFirst()
                .orElse(null);
    }
}
