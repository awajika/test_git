package org.example.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.example.constant.Role;
import org.example.domain.Departments;
import org.example.domain.Users;
import org.example.form.CsvUserForm;
import org.example.form.DepartmentForm;
import org.example.form.UserForm;
import org.example.form.UserSearchForm;
import org.example.service.DepartmentsService;
import org.example.service.UsersService;
import org.example.view.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ユーザーに関する処理を行うcontroller.
 */
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

  // 最大表示件数の設定
  final int maxPageSize = 5;

  private static final long MAX_FILE_SIZE = 1024 * 1024;

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
                            UserSearchForm userSearchForm, Model model) {

    /*
    セッションにログインユーザー情報が入っているか確認
    入っていなければログイン画面へリダイレクト
     */

    // 初期ソートの設定
    if (userSearchForm.getIdSort() == null && userSearchForm.getNameSort() == null) {
      userSearchForm.setIdSort("asc");
    }

    // 前回の配列にしたフリーワードが残っている可能性があるので、nullを入れて初期化
    userSearchForm.setAryKeywords(null);

    // 検索キーワードを半角、または全角区切りの配列にする
    if (userSearchForm.getKeyword() != null && !userSearchForm.getKeyword().isEmpty()) {
      changeAryKeywords(userSearchForm);
    }

    // pageableの設定
    Pageable pageable = PageRequest.of(page, maxPageSize);
    userSearchForm.setPage(page * maxPageSize);
    userSearchForm.setSize(maxPageSize);

    Page<UserInfo> userList = mapUserInfo(pageable, userSearchForm);
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

    // ユーザー編集のリクエストのため判断フラグにfalseをセット
    Users user = usersService.editUserByUserId(userId);
    UserForm userForm = mapUserForm(user);
    userForm.setIsRegister(false);

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

  @RequestMapping(path = "/person/update", method = RequestMethod.POST)
  public String batchUpdate(@RequestParam("file") MultipartFile multipartFile,
                            RedirectAttributes redirectAttributes) {

    try (InputStream inputStream = multipartFile.getInputStream();
         BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

      /*
      セッション情報から権限をチェックする
      セッションから作成者のuser_idを取得する
      userForm.setAuthor(作成者のuser_id)
       */

      String line;
      System.out.println(multipartFile.getSize());

      // csvファイルの行がなくなるまで繰り返す
      while ((line = br.readLine()) != null) {
        String[] csvSplit = line.split(",");

        CsvUserForm csvForm = new CsvUserForm();
        DepartmentForm form = new DepartmentForm();

        if (csvSplit[5] != null) {
          form.setDepartmentId(Integer.parseInt(csvSplit[5]));
        }

        csvForm.setUserId(csvSplit[0]);
        csvForm.setName(csvSplit[1]);
        csvForm.setNameKana(csvSplit[2]);
        csvForm.setPassword(csvSplit[3]);
        csvForm.setPasswordConfirm(csvSplit[4]);
        csvForm.setDepartmentId(departmentsService.getDepartmentId(form));
        csvForm.setRole(findByLabel(csvSplit[6]));

        usersService.save(mapUsers(csvForm));

        redirectAttributes.addFlashAttribute("successMessage",
            messageSource.getMessage("user.save.successMessage", null, Locale.getDefault()));
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
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
  private List<FieldError> personValidation(UserForm userForm, BindingResult bindingResult) {
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
      DepartmentForm form = new DepartmentForm();
      form.setDepartmentId(userForm.getDepartmentId());

      if (departmentsService.checkDepartment(form)) {
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

  private List<FieldError> checkCsvUploadValidation(CsvUserForm form, BindingResult bindingResult) {

    List<FieldError> error = new ArrayList<>();
    String fileName = form.getFile().getName();

    // ファイルサイズのチェック
    if (form.getFile() != null && form.getFile().getSize() > MAX_FILE_SIZE) {
      error.add(new FieldError(bindingResult.getObjectName(), "file",
          messageSource.getMessage("errMsg.tooLargeFile", null, Locale.JAPAN)));
    }

    // CSVファイルかチェック
    if (!"csv".equals(fileName.substring(fileName.lastIndexOf('.')))) {
      error.add(new FieldError(bindingResult.getObjectName(), "file",
          messageSource.getMessage("errMsg.mismatchFile", null, Locale.JAPAN)));
    }

    return error;
  }

  /**
   * UserFormからUsersに詰め替える.
   *
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
   * CsvUserFormからUsersに詰め替える.
   *
   * @return Users
   */
  private Users mapUsers(CsvUserForm form) {
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

  /**
   * UsersからUserInfoに詰め替える.
   *
   * @return Page型のUserInfo
   */
  private Page<UserInfo> mapUserInfo(Pageable pageable, UserSearchForm form) {
    List<Users> userList = usersService.findUsers(form);

    List<UserInfo> userInfoList = new ArrayList<>();
    for (Users user : userList) {
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
   * フリーワードが複数入力されている場合、半角または全角で区切る.
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

  /**
   * あ.
   *
   * @param csvLabel CSVファイルから入力された権限名
   *
   * @return success: 該当のロールコード,  fail: null
   */
  private Integer findByLabel(String csvLabel) {
    if ("管理者".equals(csvLabel)) {
      return Role.ADMIN.getRoleCode();
    } else if ("一般".equals(csvLabel)) {
      return Role.GENERAL.getRoleCode();
    }
    return 2;
  }

}
