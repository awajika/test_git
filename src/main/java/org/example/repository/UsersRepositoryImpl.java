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

  @Override
  public int selectUsersCount() {
    return this.sqlsessiontemplate.getMapper(UsersMapper.class).selectUsersCount();
  }

  @Override
  public List<Users> findAll(UserSearchForm userSearchForm) {
    return this.sqlsessiontemplate.getMapper(UsersMapper.class).findAll(userSearchForm);
  }

  /**
   * 従業員IDとパスワードからユーザーを探す.
   *
   * @return Users
   */
  @Override
  public Users findByUserId(String userId) {
    return this.sqlsessiontemplate.getMapper(UsersMapper.class).findByUserId(userId);
  }

  /**
   * ユーザーを登録する.
   */
  @Override
  public void save(Users user) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).save(user);
  }

  /**
   * ユーザーを更新する.
   */
  @Override
  public void update(Users user) {
    this.sqlsessiontemplate.getMapper(UsersMapper.class).update(user);
  }
}
