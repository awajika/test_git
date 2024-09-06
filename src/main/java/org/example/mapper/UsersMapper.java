package org.example.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.Users;
import org.example.form.UserSearchForm;

/**
 * Usersテーブルのmapper.
 */
@Mapper
public interface UsersMapper {

  /**
   * 削除フラグが建っていない従業員のレコード件数を取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @param userSearchForm UserSearchForm
   * @return int
   */
  int selectUsersCount(@Param("userSearchForm") UserSearchForm userSearchForm);

  /**
   * 従業員データをpageableで設定した件数分取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
   *
   * @param userSearchForm UserSearchForm
   * @return List型のUsers
   */
  List<Users> findUsers(@Param("userSearchForm") UserSearchForm userSearchForm);

  /**
   * ユーザーIDからユーザーを探す.
   *
   * @param userId 社員番号
   * @return Users型の1件のデータ
   */
  Users findByUserId(String userId);

  /**
   * ユーザーを登録する.
   *
   * @param user Users
   */
  void save(Users user);

  /**
   * ユーザーを更新する.
   *
   * @param user Users
   */
  void update(Users user);

  /**
   * ユーザーを論理削除する.
   *
   * @param lists 社員番号
   */
  void delete(String[] lists);

  void test(String id);

  /**
   * csvファイルから取得したユーザーを登録、または論理削除する.
   *
   * @param userList csvファイルから取得したユーザーレコードが入っているlist
   */
  void saveFromCsvFile(@Param("userList") List<Users> userList);
}
