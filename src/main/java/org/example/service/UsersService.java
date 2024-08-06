package org.example.service;

import org.example.domain.Users;

public interface UsersService {

    /**
     * 従業員IDとパスワードからログインユーザーの認証を行う
     * @param userId
     * @param password
     * @return
     */
    Users loginUserByUserIdAndPassword(String userId, String password);
}
