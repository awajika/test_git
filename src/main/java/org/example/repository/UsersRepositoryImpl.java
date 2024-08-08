package org.example.repository;

import org.example.domain.Users;
import org.example.mapper.UsersMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsersRepositoryImpl implements UsersRepository{

    @Autowired
    SqlSessionTemplate sqlsessiontemplate;

    /**
     * 登録されているユーザーを昇順で全件取得する
     * @return List <Users>
     */
    @Override
    public List<Users> findAll() {
        return this.sqlsessiontemplate.getMapper(UsersMapper.class).findAll();
    }

    /**
     * 従業員IDとパスワードからユーザーを探す
     * @return Users
     */
    @Override
    public Users findByUserId(String userId) {
        return this.sqlsessiontemplate.getMapper(UsersMapper.class).findByUserId(userId);
    }

    /**
     * ユーザーを登録する
     */
    @Override
    public void save(Users user) {
        this.sqlsessiontemplate.getMapper(UsersMapper.class).save(user);
    }

    /**
     * ユーザーを更新する
     */
    @Override
    public void update(Users user) {
        this.sqlsessiontemplate.getMapper(UsersMapper.class).update(user);
    }
}
