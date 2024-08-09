package org.example.controller;

import org.example.constant.Role;
import org.example.domain.Departments;
import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.form.UserSearchForm;
import org.example.service.DepartmentsService;
import org.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UsersService usersService;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/person/list", method = RequestMethod.GET)
    public String forwardList(@ModelAttribute("successMessage")String message,
                              @RequestParam("page") int page,
                              Model model) {

        /*
        セッションにログインユーザー情報が入っているか確認
        入っていなければログイン画面へリダイレクト
        (ここはSpringSecurityの範疇？)
         */

        // 最大表示件数を設定
        int maxPageSize = 5;

        // pageableの設定
        Pageable pageable = PageRequest.of(page, maxPageSize);

        UserSearchForm userSearchForm = new UserSearchForm();
        userSearchForm.setPage(page * maxPageSize);
        userSearchForm.setSize(maxPageSize);

        Page<Users> userList = usersService.findAll(pageable, userSearchForm);
        List<Departments> departmentList = departmentsService.findAll();

        model.addAttribute("userSearchForm", userSearchForm);
        model.addAttribute("userList", userList);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("roleList", Role.values());
        model.addAttribute("successMessage", message);

        return "person/list";
    }

    @RequestMapping(path = "/person/form", method = RequestMethod.GET)
    public String forwardEntry(Model model) {

        // セッション情報から権限をチェックする

        UserForm userForm = new UserForm();
        userForm.setIsRegister(true);

        List<Departments> departmentList = departmentsService.findAll();

        model.addAttribute("userForm", userForm);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("roleList", Role.values());

        // person/entry.htmlへフォワード
        return "person/entry";
    }

    @RequestMapping(path = "/person/form/{userId}", method = RequestMethod.GET)
    public String forwardEntry(@PathVariable String userId, Model model) {

        /*
        セッション情報から権限をチェックする
        ログインユーザーのID＝パラメータのID
         */

        UserForm userForm = usersService.editUserByUserId(userId);
        userForm.setIsRegister(false);

        List<Departments> departmentList = departmentsService.findAll();

        model.addAttribute("userForm", userForm);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("roleList", Role.values());

        // person/entry.htmlへフォワード
        return "person/entry";
    }

    @RequestMapping(path = "/person/form", method = RequestMethod.POST)
    public String forwardEntryConfirm(@Validated @ModelAttribute UserForm userForm, BindingResult bindingResult, Model model) {

        /*
        セッション情報から権限をチェックする
        ログインユーザーのID＝パラメータのID
         */

        List<Departments> departmentList = departmentsService.findAll();

        // @で対処できないValidationのチェック
        FieldError error = personValidation(userForm, bindingResult);
        if (error != null) { // personValidationでerrorにnullを入れて初期化しているため
            bindingResult.addError(error);
        }

        // validationチェック
        if (bindingResult.hasErrors()) {
            model.addAttribute("userForm", userForm);
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("roleList", Role.values());
            return "person/entry";
        }

        // person/entry_confirmへフォワード
        model.addAttribute("userForm", userForm);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("roleList", Role.values());

        return "person/entry_confirm";
    }

    @RequestMapping(path = "/person/confirm", method = RequestMethod.POST)
    public String save(UserForm userForm, Model model, RedirectAttributes redirectAttributes) {

        /*
        セッション情報から権限をチェックする
        ログインユーザーのID＝パラメータのID
        セッションから作成者のuser_idを取得する
        userForm.setAuthor(作成者のuser_id)
         */

        // entry.htmlで戻るボタンが押下されたとき
        if (userForm.getBackFlg() == 0) {
            List<Departments> departmentList = departmentsService.findAll();

            model.addAttribute("userForm", userForm);
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("roleList", Role.values());
            return "person/entry";
        }

        Users user = mapUsers(userForm);

        // ユーザー登録かユーザー編集かをチェック
        if (userForm.getIsRegister()) {
            usersService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "登録完了しました");
        } else {
            usersService.update(user);
            redirectAttributes.addFlashAttribute("successMessage", "編集完了しました");
        }

        return "redirect:/person/list?page=0";
    }

    /**
     * personのvalidationチェックを行う
     * @return FieldError型　エラーメッセージ
     */
    public FieldError personValidation(UserForm userForm, BindingResult bindingResult) {

        FieldError error = null;

        // 既に登録済の社員番号かチェック(登録時のみ)
        if (userForm.getIsRegister()) {
            Users user = usersService.findByUserId(userForm.getUserId());
            if (user != null) {
                error = new FieldError(bindingResult.getObjectName(), "userId",
                        "この社員番号は既に使用されています。別の社員番号を入力してください");
            }
        }

        // パスワードとパスワード（再入力）一致チェック
        if (userForm.getPassword() != null && userForm.getPasswordConfirm() != null) {
            if (!(userForm.getPassword().equals(userForm.getPasswordConfirm()))) {
                error = new FieldError(bindingResult.getObjectName(), "password",
                        "パスワードが一致しません");
            }
        }

        // マスタにない所属が入力されていないかチェック
        if (userForm.getDepartmentId() != null) {
            if (departmentsService.checkDepartment(userForm.getDepartmentId())) {
                error = new FieldError(bindingResult.getObjectName(), "departmentId",
                        "不正な値が入力されました");
            }
        }

        // マスタにない権限が入力されていないかチェック
        if (userForm.getRole() != null) {
            if (Role.getRole(userForm.getRole()) == null) {
                error = new FieldError(bindingResult.getObjectName(), "role",
                        "不正な値が入力されました");
            }
        }
        return error;
    }

    /**
     * UserFormからUsersに詰め替える
     * @return Users
     */
    private Users mapUsers(UserForm userForm) {
        Users user = new Users();

        user.setUserId(userForm.getUserId());
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setName(userForm.getName());
        user.setNameKana(userForm.getNameKana());
        user.setDepartmentId(userForm.getDepartmentId());
        user.setRole(userForm.getRole());
        user.setCreateUser("0001");
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateUser("0001");
        user.setUpdateDate(LocalDateTime.now());

        return user;
    }
}
