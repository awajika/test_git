package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.domain.Users;
@Mapper
public interface UsersMapper {

    /**
     * ユーザーIDとパスワードからユーザーを探す
     * @return Users型の1件のデータ
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
