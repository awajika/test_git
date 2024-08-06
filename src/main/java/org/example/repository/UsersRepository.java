package org.example.repository;

import org.example.domain.Users;

public interface UsersRepository {

    /**
     * 従業員IDとパスワードからユーザーを探す
     * @param user_id
     * @param password
     * @return
     */
    Users findByUserIdAndPassword(String user_id, String password);
}
