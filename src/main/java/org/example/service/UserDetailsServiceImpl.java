package org.example.service;

import org.example.domain.Users;
import org.example.repository.UsersRepository;
import org.example.service.data.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ログインユーザーのパスワードチェックを行う.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    Users user = usersRepository.findByUserId(userId);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new LoginUser(user);
  }
}