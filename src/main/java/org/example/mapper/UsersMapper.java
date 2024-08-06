package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.domain.Users;
@Mapper
public interface UsersMapper {

    /**
     * ユーザーIDとパスワードからユーザーを探す
     * @param user_id
     * @param password
     * @return
     */
    Users findByUserIdAndPassword(String user_id, String password);
}
