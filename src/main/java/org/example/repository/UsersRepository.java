package org.example.repository;

import org.example.domain.Users;

import java.util.List;

public interface UsersRepository {

    List<Users> findAll();

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
