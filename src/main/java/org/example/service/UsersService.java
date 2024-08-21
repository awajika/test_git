package org.example.service;

import java.util.List;
import org.example.domain.Users;
import org.example.form.UserSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * usersテーブルに関連するメソッドを持つService.
 */
public interface UsersService {

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
   * @return Users型の1件のデータ
   */
  Users findByUserId(String userId);

  /**
   * 従業員IDを元に編集するユーザーを探す.
   *
   * @return UserForm型の1件のデータ
   */
  Users editUserByUserId(String userId);

  /**
   * ユーザーを登録する.
   */
  void save(Users user);

  /**
   * ユーザーを編集する.
   */
  void update(Users user);
}
