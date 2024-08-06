package org.example.service;

import org.example.domain.Users;
import org.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    UsersRepository usersRepository;

    /**
     * 従業員IDとパスワードからログインユーザーの認証を行う
     * @param userId
     * @param password
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public Users loginUserByUserIdAndPassword(String userId, String password) throws UsernameNotFoundException {
        Users user = usersRepository.findByUserIdAndPassword(userId, password);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
