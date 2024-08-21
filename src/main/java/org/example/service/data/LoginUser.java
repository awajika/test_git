package org.example.service.data;

import lombok.Getter;
import org.example.domain.Users;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
public class LoginUser extends User {

  private final Users user;

  /**
   * UserDetailsServiceで使用するコンストラクタ
   *
   * @param user User(ユーザーID,パスワード,権限を格納)
   */
  public LoginUser(Users user) {
    super(user.getUserId(), user.getPassword(),
        AuthorityUtils.createAuthorityList("T"));
    this.user = user;
  }
}
