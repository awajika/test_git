package org.example.controller;

import org.example.constant.Role;
import org.example.domain.Departments;
import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.form.UserSearchForm;
import org.example.service.DepartmentsService;
import org.example.service.UsersService;
import org.example.view.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@SessionAttributes(types = UserSearchForm.class)
public class UserController {

    @Autowired
    UsersService usersService;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(path = "/person/list", method = RequestMethod.GET)
    public String forwardList(@ModelAttribute("successMessage")String message,
                              @RequestParam("page") int page,
                              UserSearchForm userSearchForm, Model model) {

        /*
        セッションにログインユーザー情報が入っているか確認
        入っていなければログイン画面へリダイレクト
         */

        // 最大表示件数の設定
        int maxPageSize = 5;

        // pageableの設定
        Pageable pageable = PageRequest.of(page, maxPageSize);
        userSearchForm.setPage(page * maxPageSize);
        userSearchForm.setSize(maxPageSize);

        // 初期ソートの設定
        if (userSearchForm.getIdSort() == null && userSearchForm.getNameSort() == null) {
            userSearchForm.setIdSort("asc");
        }

        // 前回のフリーワードが残っている可能性があるので、nullを入れて初期化
        userSearchForm.setAryKeywords(null);

        // 検索キーワードを半角、または全角区切りの配列にする
        if (userSearchForm.getKeyword() != null && !userSearchForm.getKeyword().isEmpty()) {
            changeAryKeywords(userSearchForm);
        }

        Page<UserInfo> userList = mapUserInfo(pageable, userSearchForm);
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

        Users user = usersService.editUserByUserId(userId);
        UserForm userForm = mapUserForm(user);

        // ユーザー編集のリクエストのため判断フラグにfalseをセット
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
        FieldError error = checkPersonValidation(userForm, bindingResult);
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
        if (userForm.getBackFlg() != 0) {
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
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("user.save.successMessage", null, Locale.JAPAN));
        } else {
            usersService.update(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("user.update.successMessage", null, Locale.JAPAN));
        }

        return "redirect:/person/list?page=0";
    }

    @RequestMapping(path = "/person/update", method = RequestMethod.POST)
    public String batchUpdate(@RequestParam("file")MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            /*
            セッション情報から権限をチェックする
            セッションから作成者のuser_idを取得する
            userForm.setAuthor(作成者のuser_id)
             */

            String line;

            // csvファイルの行がなくなるまで繰り返す
            while ((line = br.readLine()) != null) {
                String[] csvSplit = line.split(",");

                UserForm userForm = new UserForm();
                userForm.setUserId(csvSplit[0]);
                userForm.setName(csvSplit[1]);
                userForm.setNameKana(csvSplit[2]);
                userForm.setPassword(csvSplit[3]);
                userForm.setPasswordConfirm(csvSplit[4]);
                userForm.setDepartmentId(Role.getRoleCode(csvSplit[5]));
                userForm.setRole(Role.getRoleCode(csvSplit[6]));

                Users user = mapUsers(userForm);

                usersService.save(user);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/person/list?page=0";
    }

    /**
     * personのvalidationチェックを行う
     * @return FieldError型　エラーメッセージ
     */
    public FieldError checkPersonValidation(UserForm form, BindingResult bindingResult) {

        FieldError error = null;

        // 既に登録済の社員番号かチェック(登録時のみ)
        if (form.getIsRegister()) {
            Users user = usersService.findByUserId(form.getUserId());
            if (user != null) {
                error = new FieldError(bindingResult.getObjectName(), "userId",
                        messageSource.getMessage("errMsg.duplicate.userId", null, Locale.JAPAN));
            }
        }

        // パスワードとパスワード（再入力）一致チェック
        if (form.getPassword() != null && form.getPasswordConfirm() != null) {
            if (!(form.getPassword().equals(form.getPasswordConfirm()))) {
                error = new FieldError(bindingResult.getObjectName(), "password",
                        messageSource.getMessage("errMsg.mismatch.password", null, Locale.JAPAN));
            }
        }

        // マスタにない所属が入力されていないかチェック
        if (form.getDepartmentId() != null) {
            if (departmentsService.checkDepartment(form.getDepartmentId())) {
                error = new FieldError(bindingResult.getObjectName(), "departmentId",
                        messageSource.getMessage("errMsg.invalidValue", null, Locale.JAPAN));
            }
        }

        // マスタにない権限が入力されていないかチェック
        if (form.getRole() != null) {
            if (Role.getRole(form.getRole()) == null) {
                error = new FieldError(bindingResult.getObjectName(), "role",
                        messageSource.getMessage("errMsg.invalidValue", null, Locale.JAPAN));
            }
        }
        return error;
    }

    /**
     * UserFormからUsersに詰め替える
     * @return Users
     */
    private Users mapUsers(UserForm form) {
        Users user = new Users();

        user.setUserId(form.getUserId());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setName(form.getName());
        user.setNameKana(form.getNameKana());
        user.setDepartmentId(form.getDepartmentId());
        user.setRole(form.getRole());
        user.setCreateUser("0001");
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateUser("0001");
        user.setUpdateDate(LocalDateTime.now());

        return user;
    }

    /**
     * UsersからUserFormに詰め替える
     * @return UserForm
     */
    private UserForm mapUserForm(Users user) {
        UserForm userForm = new UserForm();

        userForm.setUserId(user.getUserId());
        userForm.setName(user.getName());
        userForm.setNameKana(user.getNameKana());
        userForm.setDepartmentId(user.getDepartmentId());
        userForm.setRole(user.getRole());

        return userForm;
    }

    /**
     * UsersからUserInfoに詰め替える
     * @return Page型のUserInfo
     */
    private Page<UserInfo> mapUserInfo(Pageable pageable, UserSearchForm form) {
        List<Users> userList = usersService.findUsers(form);

        List<UserInfo> userInfoList = new ArrayList<>();
        for(Users user : userList) {
            UserInfo info = new UserInfo();
            info.setUserId(user.getUserId());
            info.setName(user.getName());
            info.setNameKana(user.getNameKana());
            info.setDepartmentId(user.getDepartmentId());
            info.setRole(user.getRole());
            userInfoList.add(info);
        }

        // リストの総数
        int count = usersService.selectUsersCount(form);

        return new PageImpl<>(userInfoList, pageable, count);
    }

    /**
     * フリーワードが複数入力されている場合、半角または全角で区切る
     */
    private void changeAryKeywords(UserSearchForm form) {
        String[] keyword;

        // フリーワードが複数あるか確認
        if (form.getKeyword().matches(".*[\\s|　+].*")) {
            keyword = form.getKeyword().split("\\s|　+");
        } else {
            keyword = new String[]{form.getKeyword()};
        }
        form.setAryKeywords(keyword);
    }
}
