package org.example.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SpringSecurityを利用するための設定クラス.
 * ログイン処理でのパラメータ、画面遷移や認証処理でのデータアクセス先を設定する
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * パスワードをハッシュ化（暗号化）する.
   *
   * @return ハッシュ化したパスワード
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 認証・認可の情報を設定する.
   * 画面遷移のURL・パラメータを取得するname属性の値を設定
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/top").permitAll()
                .requestMatchers("/person/delete").hasAuthority("ADMIN")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated()
        )
        .formLogin(login -> login
            .loginPage("/top")
            .loginProcessingUrl("/sign_in")
            .usernameParameter("userId")
            .passwordParameter("password")
            .defaultSuccessUrl("/person/list")
            .failureUrl("/top?error")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/top?logout")
        );
    return http.build();
  }
}