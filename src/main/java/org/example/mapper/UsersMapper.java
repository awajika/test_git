package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.Users;
import org.example.form.UserSearchForm;
import java.util.List;

@Mapper
public interface UsersMapper {

    int selectUsersCount();

    List<Users> findAll(@Param("userSearchForm") UserSearchForm userSearchForm);

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
