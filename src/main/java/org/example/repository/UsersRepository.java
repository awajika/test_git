package org.example.repository;

import org.example.domain.Users;

public interface UsersRepository {

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
