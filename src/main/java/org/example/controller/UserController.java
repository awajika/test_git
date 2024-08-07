package org.example.controller;

import org.example.constant.Role;
import org.example.domain.Departments;
import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.service.DepartmentsService;
import org.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UsersService usersService;

    @Autowired
    DepartmentsService departmentsService;

    @RequestMapping(path = "/person/form", method = RequestMethod.GET)
    public String forwardEntry(Model model) {

        /*
        セッション情報から権限をチェックする
        もしくは、useIdが変更したいデータのuserIdと一致しているかチェックする
         */

        /*
        パラメータにuserIdがあるかチェックする
        userIdがある→ユーザー編集
        userIdがない→ユーザー登録
         */
        UserForm userForm = new UserForm();

        // 現在ユーザー登録のみ実装済のため、条件分岐が確実に追加となるようあえて""を設定
        String userId = "";

        if (!("".equals(userId))) {
            userForm = usersService.editUserByUserId(userId);
            userForm.setIsRegister(false);
            model.addAttribute("title", "ユーザー編集");
        } else {
            userForm.setIsRegister(true);
            model.addAttribute("title", "ユーザー登録");
        }

        List<Departments> departmentList = departmentsService.findAll();

        model.addAttribute("userForm", userForm);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("roleList", Role.values());

        // person/entry.htmlへフォワード
        return "person/entry";
    }

    @RequestMapping(path = "/person/form", method = RequestMethod.POST)
    public String forwardEntryConfirm(@Validated @ModelAttribute UserForm userForm, BindingResult bindingResult, Model model) {

        // セッション情報から権限をチェックする

        // @で対処可能なValidationのチェック
        if (bindingResult.hasErrors()) {
            List<Departments> departmentList = departmentsService.findAll();
            model.addAttribute("title", "ユーザー新規登録");
            model.addAttribute("userForm", userForm);
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("roleList", Role.values());
            return "person/entry";
        }

        // @で対処できないValidationのチェック
        FieldError error = personValidation(userForm, bindingResult);
        if (error != null) { // personValidationでerrorにnullを入れて初期化しているため
            bindingResult.addError(error);
        }

        if (bindingResult.hasErrors()) {
            List<Departments> departmentList = departmentsService.findAll();
            model.addAttribute("title", "ユーザー新規登録");
            model.addAttribute("userForm", userForm);
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("roleList", Role.values());
            return "person/entry";
        }

        // person/entry_confirmへフォワード
        List<Departments> departmentList = departmentsService.findAll();

        model.addAttribute("title", "ユーザー新規登録");
        model.addAttribute("userForm", userForm);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("roleList", Role.values());

        return "person/entry_confirm";
    }

    @RequestMapping(path = "/person/confirm", method = RequestMethod.POST)
    public String save(UserForm userForm) {
        // セッションから作成者のuser_idを取得する
        // userForm.setAuthor(作成者のuser_id)

        // ユーザー登録かユーザー編集かをチェック

        usersService.save(userForm);

        return "redirect:/person/form";
    }

    /**
     * personのvalidationチェックを行う
     * @return FieldError型　エラーメッセージ
     */
    public FieldError personValidation(UserForm userForm, BindingResult bindingResult) {

        FieldError error = null;
        boolean result = true;

        // 既に登録済の社員番号かチェック
        Users user = usersService.findByUserId(userForm.getUserId());
        if (user != null) {
            error = new FieldError(bindingResult.getObjectName(), "userId",
                    "この社員番号は既に使用されています。別の社員番号を入力してください");
        }

        // パスワードとパスワード（再入力）一致チェック
        if (!(userForm.getPassword().equals(userForm.getPasswordConfirm()))) {
            error = new FieldError(bindingResult.getObjectName(), "password",
                    "パスワードが一致しません");
        }

        // マスタにない所属が入力されていないかチェック
        List<Departments> departmentList = departmentsService.findAll();
        for (Departments department : departmentList) {
            if (department.getDepartmentId().equals(userForm.getDepartmentId())) {
                result = false;
                break;
            }
        }
        if (result) {
            error = new FieldError(bindingResult.getObjectName(), "departmentId",
                    "不正な値が入力されました");
        }

        // マスタにない権限が入力されていないかチェック
        for (Role role : Role.values()) {
            if (role.getRoleCode() == userForm.getRole()) {
                result = false;
                break;
            }
        }
        if (result) {
            error = new FieldError(bindingResult.getObjectName(), "role",
                    "不正な値が入力されました");
        }
        return error;
    }
}
