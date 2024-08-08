package org.example.service;

import org.example.domain.Users;
import org.example.form.UserForm;

public interface UsersService {

    /**
     * 従業員IDからログインユーザーの認証を行う
     * @return Users型の1件のデータ
     */
    Users findByUserId(String userId);

    /**
     * 従業員IDを元に編集するユーザーを探す
     * @return UserForm型の1件のデータ
     */
    UserForm editUserByUserId(String userId);

    /**
     * ユーザーを登録する
     */
    void save (UserForm userForm);

    /**
     * ユーザーを編集する
     */
    void update (UserForm userForm);
}
