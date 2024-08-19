package org.example.service;

import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.form.UserSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersService {

    /**
     * 従業員データをpageableで設定した件数分取得する
     * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
     * @return Page <Users>型
     */
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
    UserForm editUserByUserId(String userId);

    /**
     * ユーザーを登録する
     */
    void save(Users user);

    /**
     * ユーザーを編集する
     */
    void update(Users user);
}
