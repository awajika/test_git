package org.example.service;

import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    UsersRepository usersRepository;

    @Override
    public List<Users> findAll() {
        return usersRepository.findAll();
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
     * @return UserForm型の1件のデータ
     */
    @Override
    public UserForm editUserByUserId(String userId) {
        Users user = usersRepository.findByUserId(userId);

        UserForm userForm = new UserForm();
        userForm.setUserId(user.getUserId());
        userForm.setName(user.getName());
        userForm.setNameKana(user.getNameKana());
        userForm.setDepartmentId(user.getDepartmentId());
        userForm.setRole(user.getRole());

        return userForm;
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
