package org.example.controller;

import org.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @Autowired
    UsersService usersService;

    @RequestMapping("/top")
    public String top() {

        // login.html表示
        return "login/login";
    }

//    @RequestMapping("/sign_in")
//    public String sign() {
//        return "redirect:/person/list";
//    }

    @RequestMapping("/person")
    public String person() {
        return "redirect:/person/list?page=0";
    }
}
