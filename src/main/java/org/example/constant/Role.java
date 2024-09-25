package org.example.constant;

import java.util.Arrays;
import lombok.Getter;

/**
 * 権限に関する定数をまとめたクラス.
 */
@Getter
public enum Role {

  ADMIN(0, "管理者", "[ADMIN]"),
  GENERAL(1, "一般", "[GENERAL]");

  private final int roleCode;
  private final String label;
  private final String userRole;

  Role(int roleCode, String label, String userRole) {
    this.roleCode = roleCode;
    this.label = label;
    this.userRole = userRole;
  }

  /**
   * 入力されたロールコードが存在するかをチェックする.
   *
   * @param roleCode 入力されたロールコード
   *
   * @return success: 該当のロールコード、 fail: null
   */
  public static Role getRole(int roleCode) {
    return Arrays.stream(Role.values())
        .filter(data -> data.getRoleCode() == roleCode)
        .findFirst()
        .orElse(null);
  }
}
