package org.example.repository;

import org.example.domain.Users;

import java.util.List;

public interface UsersRepository {

    /**
     * 登録されているユーザーを昇順で全件取得する
     * @return List <Users>
     */
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
