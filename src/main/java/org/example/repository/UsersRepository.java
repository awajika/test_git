package org.example.repository;

import org.example.domain.Users;

public interface UsersRepository {

    /**
     * 従業員IDとパスワードからユーザーを探す
     * @return Users型の1件のデータ
     */
    Users findByUserIdAndPassword(String userId);

    /**
     * ユーザーを登録する
     */
    void save (Users user);
}
