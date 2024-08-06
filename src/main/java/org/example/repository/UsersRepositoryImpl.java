package org.example.repository;

import org.example.domain.Users;
import org.example.mapper.UsersMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsersRepositoryImpl implements UsersRepository{

    @Autowired
    SqlSessionTemplate sqlsessiontemplate;

    /**
     * 従業員IDとパスワードからユーザーを探す
     * @param user_id
     * @param password
     * @return
     */
    @Override
    public Users findByUserIdAndPassword(String user_id, String password) {
        return this.sqlsessiontemplate.getMapper(UsersMapper.class).findByUserIdAndPassword(user_id, password);
    }
}
