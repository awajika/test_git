package org.example.repository;

import org.example.domain.Users;
import org.example.form.UserSearchForm;

import java.util.List;

public interface UsersRepository {

    int selectUsersCount();

    List<Users> findAll(UserSearchForm userSearchForm);

    /**
     * 従業員IDとパスワードからユーザーを探す
     * @return Users
     */
    Users findByUserId(String userId);

    /**
     * ユーザーを登録する
     */
    void save (Users user);

    /**
     * ユーザーを更新する
     */
    void update(Users user);
}
