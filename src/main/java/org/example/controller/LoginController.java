package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ユーザーログイン処理を行うcontroller.
 */
@Controller
public class LoginController {

  /**
   * ログインformへ遷移する.
   *
   * @return login.html
   */
  @RequestMapping("/top")
  public String top() {
    // login.html表示
    return "login/login";
  }
}
