package org.example.repository;

import java.util.List;
import org.example.domain.Users;
import org.example.form.UserSearchForm;

/**
 * usersテーブルに関連するメソッドを持つRepository.
 */
public interface UsersRepository {

  int selectUsersCount();

  List<Users> findAll(UserSearchForm userSearchForm);

  /**
   * 従業員IDとパスワードからユーザーを探す.
   *
   * @return Users
   */
  Users findByUserId(String userId);

  /**
   * ユーザーを登録する.
   */
  void save(Users user);

  /**
   * ユーザーを更新する.
   */
  void update(Users user);
}
