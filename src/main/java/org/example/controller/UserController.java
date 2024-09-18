package org.example.controller;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.example.util.SecuritySession;
import org.example.view.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.thymeleaf.util.StringUtils;

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

  @Autowired
  SecuritySession securitySession;

  /**
   * ユーザー一覧表示機能.
   *
   * @param messageFlag ユーザー登録・編集・一括更新が成功したときのメッセージフラグ
   * @param model   viewへ変数を渡す
   * @return list.html
   */
  @RequestMapping(path = "/person/list", method = RequestMethod.GET)
  public String forwardList(@ModelAttribute("successMessage") String messageFlag,
                            Model model) {

    // 一覧表示のためformを新たに生成
    UserSearchForm userSearchForm = new UserSearchForm();

    // 初期ソートの設定
    userSearchForm.setIdSort("asc");
    userSearchForm.setNameSort("");

    // pageableの設定
    Pageable pageable = PageRequest.of(0,
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    userSearchForm.setPage(0);

    userSearchForm.setSize(
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    Page<UserInfo> userList = mapUserInfo(pageable, userSearchForm);
    List<Departments> departmentList = departmentsService.findAll();

    // 成功メッセージがあったときの処理
    String message = getSuccessMessage(messageFlag);

    model.addAttribute("userSearchForm", userSearchForm);
    model.addAttribute("userList", userList);
    model.addAttribute("departmentList", departmentList);
    model.addAttribute("roleList", Role.values());
    model.addAttribute("page", 0);
    model.addAttribute("successMessage", message);
    model.addAttribute("userRole", securitySession.getRole());
    model.addAttribute("loginUserId", securitySession.getUserId());

    return "person/list";
  }

  /**
   * ユーザー検索機能.
   *
   * @param page    現在何ページ目にいるのかを取得
   * @param model   viewへ変数を渡す
   * @return list.html
   */
  @RequestMapping(path = "/person/search", method = RequestMethod.GET)
  public String search(@RequestParam("page") int page,
                       UserSearchForm userSearchForm, Model model) {

    /*
    フリーワード、所属、権限のみで検索された際の初期表示を社員番号の昇順にするため、初期ソートの設定を行う
    また、ページも1ページ目から始まるように設定する
     */
    if (StringUtils.isEmpty(userSearchForm.getIdSort())
        && StringUtils.isEmpty(userSearchForm.getNameSort())) {

      userSearchForm.setIdSort("asc");
      userSearchForm.setNameSort("");
      page = 0;
    }

    // 検索キーワードを半角、または全角区切りの配列にする
    if (!StringUtils.isEmpty(userSearchForm.getKeyword())) {
      changeAryKeywords(userSearchForm);
    } else {
      userSearchForm.setAryKeywords(null);
    }

    // pageableの設定
    Pageable pageable = PageRequest.of(page,
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    userSearchForm.setPage(page * Integer.parseInt(
        messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    userSearchForm.setSize(
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    Page<UserInfo> userList = mapUserInfo(pageable, userSearchForm);
    List<Departments> departmentList = departmentsService.findAll();

    model.addAttribute("userSearchForm", userSearchForm);
    model.addAttribute("userList", userList);
    model.addAttribute("departmentList", departmentList);
    model.addAttribute("roleList", Role.values());
    model.addAttribute("page", page);
    model.addAttribute("userRole", securitySession.getRole());
    model.addAttribute("loginUserId", securitySession.getUserId());

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
    if (!Role.ADMIN.getUserRole().equals(securitySession.getRole())) {
      return "redirect:/person/list";
    }

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
   * @param model  viewへ変数を渡す
   * @return entry.html
   */
  @RequestMapping(path = "/person/form/{userId}", method = RequestMethod.GET)
  public String forwardEntry(@PathVariable String userId, Model model) {

    // セッション情報から権限をチェックする
    if ((!securitySession.getRole().equals(Role.ADMIN.getUserRole()))
        && (!securitySession.getUserId().equals(userId))) {
      return "redirect:/person/list";
    }

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
   * @param userForm      ユーザー登録・編集のform.
   * @param bindingResult formのvalidationチェック
   * @param model         viewへ変数を渡す
   * @return success: entry_confirm.html, fail: entry.html
   */
  @RequestMapping(path = "/person/form", method = RequestMethod.POST)
  public String forwardEntryConfirm(@Validated @ModelAttribute UserForm userForm,
                                    BindingResult bindingResult, Model model) {

    // セッション情報から権限をチェックする
    if ((!securitySession.getRole().equals(Role.ADMIN.getUserRole()))
        && (!securitySession.getUserId().equals(userForm.getUserId()))) {
      return "redirect:/person/list";
    }

    List<Departments> departmentList = departmentsService.findAll();

    // @で対処できないValidationのチェック
    List<FieldError> errorList = checkPersonValidation(userForm, bindingResult);
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
   * @param userForm           ユーザー登録・編集のform
   * @param model              viewへ変数を渡す
   * @param redirectAttributes リダイレクト先へ変数を渡す
   * @return redirect:/person/list
   */
  @RequestMapping(path = "/person/confirm", method = RequestMethod.POST)
  public String save(UserForm userForm, Model model, RedirectAttributes redirectAttributes) {

    /*
    セッション情報から権限をチェックする
    ログインユーザーのID＝パラメータのID
    セッションから作成者のuser_idを取得する
    userForm.setAuthor(作成者のuser_id)
     */
    // セッション情報から権限をチェックする
    if ((!securitySession.getRole().equals(Role.ADMIN.getUserRole()))
        && (!securitySession.getUserId().equals(userForm.getUserId()))) {
      return "redirect:/person/list";
    }

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
          messageSource.getMessage("register", null, Locale.getDefault()));
    } else {
      usersService.update(user);
      redirectAttributes.addFlashAttribute("successMessage",
          messageSource.getMessage("edit", null, Locale.getDefault()));
    }
    return "redirect:/person/list";
  }

  /**
   * csvファイルを読み込んでユーザー一括登録または削除を行う.
   *
   * @param file アップロードされたファイル
   * @return ステータスコード200、validationエラー時は400とエラーメッセージ
   */
  @RequestMapping(path = "/person/update", method = RequestMethod.POST)
  public ResponseEntity<Object> updateUsers(@RequestParam(value = "file", required = false)
                                    MultipartFile file) {
    /*
    セッション情報から権限をチェックする
    セッションから作成者のuser_idを取得する
    userForm.setAuthor(作成者のuser_id)
     */

    // エラーをリクエストヘッダのmessageにセットするMapを用意
    HashMap<String, List<String>> error = new HashMap<>();

    // ファイルのvalidationチェック
    List<String> errorList = checkCsvFileValidation(file);
    if (!errorList.isEmpty()) {
      error.put("message", errorList);
      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // csvファイルを読み込む
    try (InputStream inputStream = file.getInputStream();
         CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {

      CsvToBeanBuilder<CsvUserForm> builder = new CsvToBeanBuilder<>(csvReader);
      builder.withType(CsvUserForm.class);


      // ファイルのフォーマットチェック
      List<String[]> readCsvList = csvReader.readAll();
      errorList = checkCsvFileValidation(readCsvList);

      if (!errorList.isEmpty()) {
        error.put("message", errorList);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      }

      // ヘッダー行を取り除く
      readCsvList.remove(0);

      // validationチェックをエラーなく終えてUsersに詰め替えられたデータを入れるlist
      List<Users> userList = new ArrayList<>();

      // 現在の行数を保持する変数の初期化
      int count = 0;

      // 社員番号の重複チェックをするためにuserIdだけのリストを作成
      List<String> userIdList = new ArrayList<>(
          readCsvList.stream()
              .map(data -> data[0])
              .toList());

      for (String[] line : readCsvList) {

        // 現在の行数
        count++;
        // csvファイルのユーザー情報をCsvUserFormに詰め替える
        CsvUserForm csvUserForm = mapCsvUserForm(line);

        // validationチェック
        List<String> errorMsgList = checkPersonValidation(csvUserForm, count, userIdList);
        errorList.addAll(errorMsgList);

        Users user = mapUsers(csvUserForm);
        userList.add(user);
      }

      // validationチェックでerrorがあった際の処理
      if (!errorList.isEmpty()) {
        error.put("message", errorList);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      }

      usersService.saveFromCsvFile(userList);

      // successMessageをリクエストヘッダのmessageにセットするMapを用意
      HashMap<String, String> success = new HashMap<>();
      success.put("message", messageSource.getMessage("update", null, Locale.getDefault()));

      return new ResponseEntity<>(success, HttpStatus.OK);

    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 選択されたユーザーの一括削除を行う.
   *
   * @param lists 削除するユーザーのuserId
   * @return ステータスコード200
   */
  @RequestMapping(path = "/person/delete", method = RequestMethod.POST)
  public ResponseEntity<Object> deleteUsers(@RequestParam(value = "lists", required = false)
                                    String[] lists) {

    usersService.delete(lists);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * personのvalidationチェックを行う.
   *
   * @param userForm      エラーの確認をしたいuserForm
   * @param bindingResult bindingResult
   * @return FieldError型のlist エラーメッセージ
   */
  private List<FieldError> checkPersonValidation(UserForm userForm, BindingResult bindingResult) {
    List<FieldError> error = new ArrayList<>();

    // 既に登録済の社員番号かチェック(登録時のみ)
    if (userForm.getIsRegister()) {
      Users user = usersService.findByUserId(userForm.getUserId());
      if (user != null) {
        error.add(new FieldError(bindingResult.getObjectName(), "userId",
            messageSource.getMessage("errMsg.duplicate.userForm.userId", null,
                Locale.getDefault())));
      }
    }

    // パスワードとパスワード（再入力）一致チェック
    if (userForm.getPassword() != null && userForm.getPasswordConfirm() != null) {
      if (!(userForm.getPassword().equals(userForm.getPasswordConfirm()))) {
        error.add(new FieldError(bindingResult.getObjectName(), "passwordConfirm",
            messageSource.getMessage("errMsg.mismatch.password", null,
                Locale.getDefault())));
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

  /**
   * CSVファイルのユーザーデータのvalidationチェックを行う.
   *
   * @param form  csvファイルから取得したデータを詰めたform
   * @param count 現在の行数
   * @param list userIdだけのlist 社員番号の重複チェックのために使用
   * @return String型のlist エラーメッセージ
   */
  private List<String> checkPersonValidation(CsvUserForm form, int count, List<String> list) {

    List<String> error = new ArrayList<>();

    /* 社員番号 */
    String wordCount = messageSource.getMessage("limitOneTwenty", null, Locale.getDefault());
    String userId = messageSource.getMessage("userId", null, Locale.getDefault());

    // 最大文字数
    String maxDigit = messageSource.getMessage("maxDigit", null, Locale.getDefault());

    //未入力チェック
    if (StringUtils.isEmpty(form.getUserId())) {
      error.add(messageSource.getMessage("NotBlank.csvUserForm",
          new String[]{String.valueOf(count), userId}, Locale.getDefault()));
    }
    // 桁数チェック
    if (Integer.parseInt(maxDigit) < form.getUserId().length()) {
      error.add(messageSource.getMessage("Size.csvUserForm",
          new String[]{String.valueOf(count), userId, wordCount}, Locale.getDefault()));
    }
    // 社員番号の重複チェック
    List<String> userIdList = new ArrayList<>(list);
    // 現在チェックしている行を取り除く
    userIdList.remove(count - 1);
    if (userIdList.contains(form.getUserId())) {
      error.add(messageSource.getMessage("errMsg.duplicate",
          new String[]{String.valueOf(count), userId}, Locale.getDefault()));
    }

    /* 氏名 */
    String name = messageSource.getMessage("name", null, Locale.getDefault());

    //未入力チェック
    if (StringUtils.isEmpty(form.getName())) {
      error.add(messageSource.getMessage("NotBlank.csvUserForm",
          new String[]{String.valueOf(count), name}, Locale.getDefault()));
    }
    // 桁数チェック
    if (Integer.parseInt(maxDigit) < form.getName().length()) {
      error.add(messageSource.getMessage("Size.csvUserForm",
          new String[]{String.valueOf(count), name, wordCount}, Locale.getDefault()));
    }

    /* 氏名（カナ）*/
    String nameKana = messageSource.getMessage("nameKana", null, Locale.getDefault());

    //未入力チェック
    if (StringUtils.isEmpty(form.getNameKana())) {
      error.add(messageSource.getMessage("NotBlank.csvUserForm",
          new String[]{String.valueOf(count), nameKana}, Locale.getDefault()));
    }
    // 桁数チェック
    if (Integer.parseInt(maxDigit) < form.getNameKana().length()) {
      error.add(messageSource.getMessage("Size.csvUserForm",
          new String[]{String.valueOf(count), nameKana, wordCount}, Locale.getDefault()));
    }
    // 文字種チェック
    if (!form.getNameKana().matches("^[ァ-ンヴー]*$")) {
      error.add(messageSource.getMessage("Pattern.csvUserForm.nameKana",
          new String[]{String.valueOf(count), nameKana}, Locale.getDefault()));
    }

    /* パスワード */
    String password = messageSource.getMessage("password", null, Locale.getDefault());
    wordCount = messageSource.getMessage("limitEightTwenty", null, Locale.getDefault());

    // 最小文字数
    String minDigit = messageSource.getMessage("minDigit", null, Locale.getDefault());

    //未入力チェック
    if (StringUtils.isEmpty(form.getPassword())) {
      error.add(messageSource.getMessage("NotBlank.csvUserForm",
          new String[]{String.valueOf(count), password}, Locale.getDefault()));
    }
    // 桁数チェック(8文字未満)
    if (form.getPassword().length() < Integer.parseInt(minDigit)) {
      error.add(messageSource.getMessage("Size.csvUserForm",
          new String[]{String.valueOf(count), password, wordCount}, Locale.getDefault()));
    }
    // 桁数チェック(20文字以上)
    if (Integer.parseInt(maxDigit) < form.getPassword().length()) {
      error.add(messageSource.getMessage("Size.csvUserForm",
          new String[]{String.valueOf(count), password, wordCount}, Locale.getDefault()));
    }
    // 文字種チェック
    if (!form.getPassword().matches("^[0-9a-zA-Z]*$")) {
      error.add(messageSource.getMessage("Pattern.csvUserForm.password",
          new String[]{String.valueOf(count), password}, Locale.getDefault()));
    }

    /* 所属 */
    String departmentId = messageSource.getMessage("departmentId", null, Locale.getDefault());

    //未入力チェック
    if (StringUtils.isEmpty(form.getDepartmentName())) {
      error.add(messageSource.getMessage("NotBlank.csvUserForm",
          new String[]{String.valueOf(count), departmentId}, Locale.getDefault()));
    }

    // マスタにない所属が入力されていないかチェック
    if (!"".equals(form.getDepartmentName())) {
      DepartmentForm departmentForm = new DepartmentForm();
      departmentForm.setName(form.getDepartmentName());
      if (departmentsService.findDepartmentId(departmentForm) == null) {
        error.add(messageSource.getMessage("errMsg.invalidValueInRow",
            new String[]{String.valueOf(count), departmentId}, Locale.getDefault()));
      } else {
        form.setDepartmentId(departmentsService.findDepartmentId(departmentForm).getDepartmentId());
      }
    }

    /* 権限 */
    String role = messageSource.getMessage("role", null, Locale.getDefault());

    //未入力チェック
    if (StringUtils.isEmpty(form.getLabel())) {
      error.add(messageSource.getMessage("NotBlank.csvUserForm",
          new String[]{String.valueOf(count), role}, Locale.getDefault()));
    }

    // マスタにない権限が入力されていないかチェック
    if (!"".equals(form.getLabel())) {
      if (findByLabel(form.getLabel()) == null) {
        error.add(messageSource.getMessage("errMsg.invalidValueInRow",
            new String[]{String.valueOf(count), role}, Locale.getDefault()));
      } else {
        form.setRole(findByLabel(form.getLabel()));
      }
    }

    /* 状態 */
    String status = messageSource.getMessage("status", null, Locale.getDefault());

    // 削除以外の値が入力されていないかチェック
    if (!"".equals(form.getStatus()) && !"削除".equals(form.getStatus())) {
      error.add(messageSource.getMessage("errMsg.invalidValueInRow",
            new String[]{String.valueOf(count), status}, Locale.getDefault()));
    }

    return error;
  }

  /**
   * csvファイルのフォーマットチェック以外のチェックを行う.
   *
   * @param file csvファイル
   * @return String型のlist エラーメッセージ
   */
  private List<String> checkCsvFileValidation(MultipartFile file) {

    List<String> error = new ArrayList<>();

    // ファイルがアップロードされているかチェック
    if (file == null) {
      error.add(messageSource.getMessage("errMsg.notSelectedFile", null, Locale.getDefault()));

      // ファイルがnullだと下記のチェックをやる必要がないためreturn
      return error;
    }

    // CSVファイルかチェック
    String fileName = file.getOriginalFilename();

    if (fileName != null) {
      String fileExtension = fileName.substring(fileName.lastIndexOf('.'));

      if (!".csv".equals(fileExtension)) {
        error.add(messageSource.getMessage("errMsg.notSelectedFile", null, Locale.getDefault()));
      }
    }

    // ファイルサイズのチェック
    if (file.getSize() > Integer.parseInt(messageSource.getMessage("maxFileSize",
        null, Locale.getDefault()))) {
      error.add(messageSource.getMessage("errMsg.tooLargeFile", null, Locale.getDefault()));
    }
    return error;
  }

  /**
   * csvファイルのフォーマットチェックを行う.
   *
   * @param csvLineList csvファイルを読み込んだlist
   * @return String型のlist エラーメッセージ
   */
  private List<String> checkCsvFileValidation(List<String[]> csvLineList) {

    List<String> error = new ArrayList<>();

    // ヘッダーの文字列
    String header = messageSource.getMessage("csvFile.header", null, Locale.getDefault());

    // 指定のヘッダーが存在するかチェック
    if (!header.equals(Arrays.toString(csvLineList.get(0)))) {
      error.add(messageSource.getMessage("errMsg.mismatchFormat", null, Locale.getDefault()));

      /*
       * 指定のヘッダーがない + カンマも6個ではないファイルがアップロードされたとき
       * 二重にエラーメッセージを表示してしまうので、
       * それを防ぐためのreturn
       */
      return error;
    }

    // カンマが6個あるかチェック
    for (String[] line : csvLineList) {
      if (countComma(Arrays.toString(line)) != 6) {
        error.add(messageSource.getMessage("errMsg.mismatchFormat", null, Locale.getDefault()));
        break;
      }
    }
    return error;
  }

  /**
   * UserFormからUsersに詰め替える.
   *
   * @param form UserForm
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
    user.setCreateUser(securitySession.getUserId());
    user.setCreateDate(LocalDateTime.now());
    user.setUpdateUser(securitySession.getUserId());
    user.setUpdateDate(LocalDateTime.now());

    return user;
  }

  /**
   * CsvUserFormからUsersに詰め替える.
   *
   * @param form CsvUserForm
   * @return Users
   */
  private Users mapUsers(CsvUserForm form) {
    Users user = new Users();

    user.setUserId(form.getUserId());
    user.setPassword(passwordEncoder.encode(form.getPassword()));
    user.setName(form.getName());
    user.setNameKana(form.getNameKana());

    if (form.getDepartmentId() != null) {
      user.setDepartmentId(form.getDepartmentId());
    }

    if (form.getRole() != null) {
      user.setRole(form.getRole());
    }

    user.setCreateUser(securitySession.getUserId());
    user.setCreateDate(LocalDateTime.now());
    user.setUpdateUser(securitySession.getUserId());
    user.setUpdateDate(LocalDateTime.now());
    user.setDelete(form.isDeleteFlg());

    return user;
  }

  /**
   * UsersからUserFormに詰め替える.
   *
   * @param user Users
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
   * @param pageable ページネーションの設定
   * @param form     UserSearchForm
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
   * CSVファイルから読み込んだデータをCsvUserFormに詰め替える.
   *
   * @param line CSVファイルから読み込んだ1レコード
   * @return CsvUserForm
   */
  private CsvUserForm mapCsvUserForm(String[] line) {

    // csvファイルのデータをCsvUserFormにマッピングする
    CsvUserForm csvUserForm = new CsvUserForm();

    csvUserForm.setUserId(line[0]);
    csvUserForm.setName(line[1]);
    csvUserForm.setNameKana(line[2]);
    csvUserForm.setPassword(line[3]);
    csvUserForm.setDepartmentName(line[4]);
    csvUserForm.setLabel((line[5]));
    csvUserForm.setStatus(line[6]);
    csvUserForm.setAuthor(securitySession.getUserId());

    // 状態に削除が入っていたら削除フラグを建てる
    csvUserForm.setDeleteFlg("削除".equals(csvUserForm.getStatus()));

    return csvUserForm;
  }

  /**
   * リクエストパラメータで受け取ったmessageFlagを元に対応したsuccessMessageを取得する.
   * messageFlagの種類はmessage.propertiesを参照
   *
   * @param messageFlag リクエストパラメータで受け取ったmessageFlag
   * @return successMessage
   */
  private String getSuccessMessage(String messageFlag) {

    String message = "";

    if ("register".equals(messageFlag)) {
      message = messageSource.getMessage("register.successMessage", null, Locale.getDefault());
    } else if ("edit".equals(messageFlag)) {
      message = messageSource.getMessage("edit.successMessage", null, Locale.getDefault());
    } else if ("update".equals(messageFlag)) {
      message = messageSource.getMessage("update.successMessage", null, Locale.getDefault());
    } else if ("delete".equals(messageFlag)) {
      message = messageSource.getMessage("delete.successMessage", null, Locale.getDefault());
    }

    return message;
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
   * CSVファイルから入力された権限名から該当するロールコードを取得する.
   *
   * @param label CSVファイルから入力された権限名
   * @return success: 該当のロールコード,  fail: null
   */
  private Integer findByLabel(String label) {
    if ("管理者".equals(label)) {
      return Role.ADMIN.getRoleCode();
    } else if ("一般".equals(label)) {
      return Role.GENERAL.getRoleCode();
    }
    return null;
  }

  /**
   * csvファイルから読み込んだデータに含まれるカンマの数を数える.
   *
   * @param line csvファイルから読み込んだデータ
   * @return カンマの数
   */
  private static long countComma(String line) {
    return line.chars()
        .filter(c -> c == ',')
        .count();
  }
}