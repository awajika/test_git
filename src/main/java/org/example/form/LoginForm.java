package org.example.form;

import lombok.Data;

/**
 * ユーザーログインのform.
 */
@Data
public class LoginForm {
  // ユーザーID
  private String userId;
  // パスワード
  private String password;
}
