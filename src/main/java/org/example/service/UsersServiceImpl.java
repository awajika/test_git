package org.example.service;

import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 従業員IDからログインユーザーの認証を行う
     * @return Users型の1件のデータ
     */
    @Override
    public Users findByUserId(String userId) {
       return usersRepository.findByUserIdAndPassword(userId);
    }

    /**
     * 従業員IDを元に編集するユーザーを探す
     * @return UserForm型の1件のデータ
     */
    @Override
    public UserForm editUserByUserId(String userId) {
        Users user = usersRepository.findByUserIdAndPassword(userId);

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
    public void save(UserForm userForm) {
        Users user = new Users();

        Calendar cl = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        user.setUserId(userForm.getUserId());
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setName(userForm.getName());
        user.setNameKana(userForm.getNameKana());
        user.setDepartmentId(userForm.getDepartmentId());
        user.setRole(userForm.getRole());
        user.setCreateUser("0001");
        user.setCreateDate(currentTime.format(cl.getTime()));
        user.setUpdateUser("0001");
        user.setUpdateDate(currentTime.format(cl.getTime()));

        usersRepository.save(user);
    }

    /**
     * ユーザーを編集する
     */
    @Override
    public void update(UserForm userForm) {
        Users user = new Users();

        Calendar cl = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        user.setUserId(userForm.getUserId());
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setName(userForm.getName());
        user.setNameKana(userForm.getNameKana());
        user.setDepartmentId(userForm.getDepartmentId());
        user.setRole(userForm.getRole());
        user.setUpdateUser("0001");
        user.setUpdateDate(currentTime.format(cl.getTime()));

        usersRepository.update(user);
    }
}
