package org.example.repository;

import java.util.List;
import org.example.domain.Users;
import org.example.form.UserSearchForm;
import org.example.mapper.UsersMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * UsersService実装クラス.
 */
@Repository
public class UsersRepositoryImpl implements UsersRepository {

  @Autowired
  SqlSessionTemplate sqlsessiontemplate;

  /**
   * 削除フラグが建っていない従業員のレコード件数を取得.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @param userSearchForm UserSearchForm
   * @return int
   */
  @Override
  public int selectUsersCount(UserSearchForm userSearchForm) {
    return this.sqlsessiontemplate.getMapper(UsersMapper.class).selectUsersCount(userSearchForm);
  }

  /**
   * 従業員データをpageableで設定した件数分取得する.
   * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
   *
   * @param userSearchForm UserSearchForm
   * @return ListのUsers
   */
  @Override
  public List<Users> findUsers(UserSearchForm userSearchForm) {
    return this.sqlsessiontemplate.getMapper(UsersMapper.class).findUsers(userSearchForm);
  }

  /**
   * 従業員IDからユーザーを探す.
   *
   * @param userId 社員番号
   * @return Users
   */
  @Override
  public Users findByUserId(String userId) {
    return this.sqlsessiontemplate.getMapper(UsersMapper.class).findByUserId(userId);
  }

  /**
   * ユーザーを登録する.
   *
   * @param user Users
   */
  @Override
  public void save(Users user) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).save(user);
  }

  /**
   * ユーザーを更新する.
   *
   * @param user Users
   */
  @Override
  public void update(Users user) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).update(user);
  }

  /**
   * ユーザーを論理削除する.
   *
   * @param userId 社員番号
   */
  @Override
  public void delete(String[] userId) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).delete(userId);
  }

  @Override
  public void test(String id) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).test(id);
  }

  /**
   * csvファイルから取得したユーザーを登録、または論理削除する.
   *
   * @param userList csvファイルから取得したユーザーレコードが入っているlist
   */
  @Override
  public void saveFromCsvFile(List<Users> userList) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).saveFromCsvFile(userList);
  }
}
