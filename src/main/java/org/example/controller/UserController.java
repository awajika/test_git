package org.example.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.example.constant.Role;
import org.example.domain.Departments;
import org.example.domain.Users;
import org.example.form.UserForm;
import org.example.form.UserSearchForm;
import org.example.service.DepartmentsService;
import org.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ユーザーに関する処理を行うcontroller.
 */
@Controller
public class UserController {

  @Autowired
  UsersService usersService;

  @Autowired
  DepartmentsService departmentsService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  MessageSource messageSource;

  /**
   * ユーザー一覧表示機能.
   *
   * @param message ユーザー登録・編集が成功したときのメッセージ
   * @param page 現在何ページ目にいるのかを取得
   * @param model viewへ変数を渡す
   *
   * @return list.html
   */
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

  /**
   * ユーザー登録formへ遷移する.
   *
   * @param model viewへ変数を渡す
   * @return entry.html
   */
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

  /**
   * ユーザー編集formへ遷移する.
   *
   * @param userId 社員番号
   * @param model viewへ変数を渡す
   * @return entry.html
   */
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

  /**
   * ユーザー登録・編集formに入力された値をチェックし、結果に応じたページへ遷移する.
   *
   * @param userForm  ユーザー登録・編集のform.
   * @param bindingResult formのvalidationチェック
   * @param model viewへ変数を渡す
   * @return success: entry_confirm.html, fail: entry.html
   */
  @RequestMapping(path = "/person/form", method = RequestMethod.POST)
  public String forwardEntryConfirm(@Validated @ModelAttribute UserForm userForm,
                                    BindingResult bindingResult, Model model) {

    /*
    セッション情報から権限をチェックする
    ログインユーザーのID＝パラメータのID
     */

    List<Departments> departmentList = departmentsService.findAll();

    // @で対処できないValidationのチェック
    List<FieldError> errorList = personValidation(userForm, bindingResult);
    if (!errorList.isEmpty()) {
      errorList.forEach(bindingResult::addError);
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

  /**
   * データベースにユーザーを登録またはユーザーの更新を行う.
   *
   * @param userForm ユーザー登録・編集のform
   * @param model viewへ変数を渡す
   * @param redirectAttributes リダイレクト先へ変数を渡す
   * @return redirect:/person/list?page=0
   */
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
          messageSource.getMessage("user.save.successMessage", null, Locale.getDefault()));
    } else {
      usersService.update(user);
      redirectAttributes.addFlashAttribute("successMessage",
          messageSource.getMessage("user.update.successMessage", null, Locale.getDefault()));
    }

    return "redirect:/person/list?page=0";
  }

  /**
   * personのvalidationチェックを行う.
   *
   * @param userForm エラー
   * @param bindingResult エラー
   * @return FieldError型 エラーメッセージ
   */
  public List<FieldError> personValidation(UserForm userForm, BindingResult bindingResult) {

    List<FieldError> error = new ArrayList<>();

    // 既に登録済の社員番号かチェック(登録時のみ)
    if (userForm.getIsRegister()) {
      Users user = usersService.findByUserId(userForm.getUserId());
      if (user != null) {
        error.add(new FieldError(bindingResult.getObjectName(), "userId",
            messageSource.getMessage("errMsg.duplicate.userId", null, Locale.getDefault())));
      }
    }

    // パスワードとパスワード（再入力）一致チェック
    if (userForm.getPassword() != null && userForm.getPasswordConfirm() != null) {
      if (!(userForm.getPassword().equals(userForm.getPasswordConfirm()))) {
        error.add(new FieldError(bindingResult.getObjectName(), "passwordConfirm",
            messageSource.getMessage("errMsg.mismatch.password", null, Locale.getDefault())));
      }
    }

    // マスタにない所属が入力されていないかチェック
    if (userForm.getDepartmentId() != null) {
      if (departmentsService.checkDepartment(userForm.getDepartmentId())) {
        error.add(new FieldError(bindingResult.getObjectName(), "departmentId",
            messageSource.getMessage("errMsg.invalidValue", null, Locale.getDefault())));
      }
    }

    // マスタにない権限が入力されていないかチェック
    if (userForm.getRole() != null) {
      if (Role.getRole(userForm.getRole()) == null) {
        error.add(new FieldError(bindingResult.getObjectName(), "role",
            messageSource.getMessage("errMsg.invalidValue", null, Locale.getDefault())));
      }
    }
    return error;
  }

  /**
   * UserFormからUsersに詰め替える.
   *
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

  /**
   * UsersからUserFormに詰め替える.
   *
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
}
