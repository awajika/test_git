package org.example.domain;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Usersテーブルのdomain.
 */
@Data
public class Users {

  // 従業員ID
  private String userId;
  // パスワード
  private String password;
  // 氏名
  private String name;
  // 氏名（カナ）
  private String nameKana;
  // 所属
  private int departmentId;
  // 権限
  private int role;
  // 作成者ID
  private String createUser;
  // 作成日
  private LocalDateTime createDate;
  // 最終更新者ID
  private String updateUser;
  // 最終更新日
  private LocalDateTime updateDate;
  // 削除フラグ
  private boolean isDelete;
}
