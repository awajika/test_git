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
     * @return Users型の1件のデータ
     */
    @Override
    public Users findByUserIdAndPassword(String userId) {
        return this.sqlsessiontemplate.getMapper(UsersMapper.class).findByUserIdAndPassword(userId);
    }

    /**
     * ユーザーを登録する
     */
    @Override
    public void save(Users user) {
        this.sqlsessiontemplate.getMapper(UsersMapper.class).save(user);
    }
}
