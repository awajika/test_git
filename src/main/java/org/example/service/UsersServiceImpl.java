package org.example.service;

import org.example.domain.Users;
import org.example.form.UserSearchForm;
import org.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    UsersRepository usersRepository;

    @Override
    public int selectUsersCount(UserSearchForm userSearchForm) {
        return usersRepository.selectUsersCount(userSearchForm);
    }

    /**
     * 従業員データをpageableで設定した件数分取得する
     * もしuserSearchForm(検索条件)に値が入っていた場合、その条件に沿ったデータを取得する
     * @return Page <Users>型
     */
    @Override
    public List<Users> findUsers(UserSearchForm userSearchForm) {
        return usersRepository.findUsers(userSearchForm);
    }

    /**
     * 従業員IDからユーザーを探す
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
