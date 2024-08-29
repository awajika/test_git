package org.example.service;

import java.util.List;
import org.example.domain.Users;
import org.example.form.UserSearchForm;
import org.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UsersService実装クラス.
 */
@Service
public class UsersServiceImpl implements UsersService {

  @Autowired
  UsersRepository usersRepository;

  @Override
  public int selectUsersCount(UserSearchForm userSearchForm) {
    return usersRepository.selectUsersCount(userSearchForm);
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
    return usersRepository.findUsers(userSearchForm);
  }

  /**
   * 従業員IDからログインユーザーの認証を行う.
   *
   * @param userId 社員番号
   * @return Users型の1件のデータ
   */
  @Override
  public Users findByUserId(String userId) {
    return usersRepository.findByUserId(userId);
  }

  /**
   * 従業員IDを元に編集するユーザーを探す.
   *
   * @param userId 社員番号
   * @return Users型の1件のデータ
   */
  @Override
  public Users editUserByUserId(String userId) {
    return usersRepository.findByUserId(userId);
  }

  /**
   * ユーザーを登録する.
   *
   * @param user Users
   */
  @Override
  public void save(Users user) {
    usersRepository.save(user);
  }

  /**
   * ユーザーを編集する.
   *
   * @param user Users
   */
  @Override
  public void update(Users user) {
    usersRepository.update(user);
  }

  /**
   * ユーザーを論理削除する.
   *
   * @param userId 社員番号
   */
  @Override
  public void delete(String[] userId) {
    usersRepository.delete(userId);
  }

  @Override
  public void test(String id) {
    usersRepository.test(id);
  }

  /**
   * csvファイルから取得したユーザーを登録、または論理削除する.
   *
   * @param userList csvファイルから取得したユーザーレコードが入っているlist
   */
  @Override
  public void saveFromCsvFile(List<Users> userList) {
    usersRepository.saveFromCsvFile(userList);
  }
}
