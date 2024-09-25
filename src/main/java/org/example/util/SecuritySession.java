package org.example.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * ログインユーザーのセッション情報を返すクラス.
 */
@Component
public class SecuritySession {

  /**
   * ログインユーザーのログインID(従業員番号)を返す.
   *
   * @return ログインID(従業員番号)
   */
  public String getUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getUsername();
      }
    }
    return null;
  }

  /**
   * ログインユーザーの権限を返す.
   *
   * @return 権限
   */
  public String getRole() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getAuthorities().toString();
      }
    }
    return null;
  }
}
