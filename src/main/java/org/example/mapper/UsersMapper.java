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
   * @return int
   */
  int selectUsersCount(@Param("userSearchForm") UserSearchForm userSearchForm);

  /**
   * 従業員データをpageableで設定した件数分取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
   *
   * @return List型のUsers
   */
  List<Users> findUsers(@Param("userSearchForm") UserSearchForm userSearchForm);

  /**
   * ユーザーIDからユーザーを探す.
   *
   * @return Users型の1件のデータ
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
