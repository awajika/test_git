package org.example.service;

import java.util.List;
import org.example.domain.Users;
import org.example.form.UserSearchForm;

/**
 * usersテーブルに関連するメソッドを持つService.
 */
public interface UsersService {

  /**
   * 削除フラグが建っていない従業員のレコード件数を取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @param userSearchForm UserSearchForm
   * @return int
   */
  int selectUsersCount(UserSearchForm userSearchForm);

  /**
   * 従業員データをpageableで設定した件数分取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
   *
   * @param userSearchForm UserSearchForm
   * @return ListのUsers
   */
  List<Users> findUsers(UserSearchForm userSearchForm);

  /**
   * 従業員IDからユーザーを探す.
   *
   * @param userId 社員番号
   * @return Users型の1件のデータ
   */
  Users findByUserId(String userId);

  /**
   * 従業員IDを元に編集するユーザーを探す.
   *
   * @param userId 社員番号
   * @return UserForm型の1件のデータ
   */
  Users editUserByUserId(String userId);

  /**
   * ユーザーを登録する.
   *
   * @param user Users
   */
  void save(Users user);

  /**
   * ユーザーを編集する.
   *
   * @param user Users
   */
  void update(Users user);

  /**
   * ユーザーを論理削除する.
   *
   * @param userId 社員番号
   */
  void delete(String[] userId);

  void test(String id);

  /**
   * csvファイルから取得したユーザーを登録、または論理削除する.
   *
   * @param userList csvファイルから取得したユーザーレコードが入っているlist
   */
  void saveFromCsvFile(List<Users> userList);
}
