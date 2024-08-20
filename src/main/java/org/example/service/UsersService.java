package org.example.service;

import org.example.domain.Users;
import org.example.form.UserSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersService {

    Page<Users> findAll(Pageable pageable, UserSearchForm userSearchForm);

    /**
     * 従業員IDからログインユーザーの認証を行う
     * @return Users型の1件のデータ
     */
    Users findByUserId(String userId);

    /**
     * 従業員IDを元に編集するユーザーを探す
     * @return UserForm型の1件のデータ
     */
    Users editUserByUserId(String userId);

    /**
     * ユーザーを登録する
     */
    void save(Users user);

    /**
     * ユーザーを編集する
     */
    void update(Users user);
}
