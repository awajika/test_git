package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.domain.Users;
@Mapper
public interface UsersMapper {

    /**
     * ユーザーIDとパスワードからユーザーを探す
     * @return Users型の1件のデータ
     */
    Users findByUserIdAndPassword(String userId);

    /**
     * ユーザーを登録する
     */
    void save (Users user);

    void update(Users user);
}
