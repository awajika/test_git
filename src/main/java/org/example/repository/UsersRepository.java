package org.example.repository;

import java.util.List;
import org.example.domain.Users;
import org.example.form.UserSearchForm;

/**
 * usersテーブルに関連するメソッドを持つRepository.
 */
public interface UsersRepository {

  /**
   * 削除フラグが建っていない従業員のレコード件数を取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @return int
   */
  int selectUsersCount(UserSearchForm userSearchForm);

  /**
   * 従業員データをpageableで設定した件数分取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
   *
   * @return ListのUsers
   */
  List<Users> findUsers(UserSearchForm userSearchForm);

  /**
   * 従業員IDからユーザーを探す.
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
