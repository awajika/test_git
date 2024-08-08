package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.domain.Users;

import java.util.List;

@Mapper
public interface UsersMapper {

    /**
     * 登録されているユーザーを昇順で全件取得する
     * @return List <Users>
     */
    List<Users> findAll();

    /**
     * ユーザーIDとパスワードからユーザーを探す
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
