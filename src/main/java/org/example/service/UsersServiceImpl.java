package org.example.service;

import org.example.domain.Users;
import org.example.form.UserSearchForm;
import org.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    UsersRepository usersRepository;

    @Override
    public Page<Users> findAll(Pageable pageable, UserSearchForm userSearchForm) {
        List<Users> userList = usersRepository.findAll(userSearchForm);

        // リストの総数
        int count = usersRepository.selectUsersCount();

        return new PageImpl<>(userList, pageable, count);
    }

    /**
     * 従業員IDからログインユーザーの認証を行う
     * @return Users型の1件のデータ
     */
    @Override
    public Users findByUserId(String userId) {
       return usersRepository.findByUserId(userId);
    }

    /**
     * 従業員IDを元に編集するユーザーを探す
     * @return Users型の1件のデータ
     */
    @Override
    public Users editUserByUserId(String userId) {
        return usersRepository.findByUserId(userId);
    }

    /**
     * ユーザーを登録する
     */
    @Override
    public void save(Users user) {
        usersRepository.save(user);
    }

    /**
     * ユーザーを編集する
     */
    @Override
    public void update(Users user) {
        usersRepository.update(user);
    }
}
