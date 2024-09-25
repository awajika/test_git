package org.example.service.data;

import java.util.Collection;
import lombok.Getter;
import org.example.constant.Role;
import org.example.domain.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * ログインしたユーザーのユーザーID,パスワード,権限を格納するクラス.
 */
@Getter
public class LoginUser implements UserDetails {

  private final Users user;

  /**
   * UserDetailsServiceで使用するコンストラクタ.
   *
   * @param user User(ユーザーID,パスワード,権限を格納)
   */
  public LoginUser(Users user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (Role.ADMIN.getRoleCode() == user.getRole()) {
      return AuthorityUtils.createAuthorityList("ADMIN");
    }
    return AuthorityUtils.createAuthorityList("GENERAL");
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUserId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
