package org.example.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.example.constant.Role;
import org.example.domain.ItemOrders;
import org.example.domain.Items;
import org.example.domain.Orders;
import org.example.form.CsvItemMasterForm;
import org.example.form.ItemForm;
import org.example.form.ItemOrdersForm;
import org.example.form.ItemSearchForm;
import org.example.service.ItemOrdersService;
import org.example.service.ItemsService;
import org.example.service.OrdersService;
import org.example.strategy.CustomMappingStrategy;
import org.example.util.CsvUtil;
import org.example.util.SecuritySession;
import org.example.view.ItemOrdersInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * 購入商品に関する処理を行うcontroller.
 */
@Controller
@SessionAttributes(types = ItemSearchForm.class)
public class ItemController {

  @Autowired
  ItemsService itemsService;

  @Autowired
  OrdersService ordersService;

  @Autowired
  ItemOrdersService itemOrdersService;

  @Autowired
  MessageSource messageSource;

  @Autowired
  SecuritySession securitySession;

  /**
   * 購入商品一覧画面へ遷移する.
   *
   * @param messageFlag 購入商品登録・編集が成功したときのメッセージフラグ
   * @param model viewへ変数を渡す
   * @return item/list.html
   */
  @RequestMapping(path = "/item/list", method = RequestMethod.GET)
  public String forwardList(@ModelAttribute("successMessage") String messageFlag,
                            Model model) {

    /*
    セッションからユーザー情報を取得
    未ログインはログイン画面へ遷移
    */

    // 一覧表示のためformを新たに生成
    ItemSearchForm itemSearchForm = new ItemSearchForm();

    // 初期ソートは商品コードの昇順のためisSortにasc、その他は空白を設定
    itemSearchForm.setIdSort("asc");
    itemSearchForm.setPriceSort("");
    itemSearchForm.setTotalSort("");
    itemSearchForm.setCreateSort("");

    ItemOrdersForm form = mapItemOrdersForm(itemSearchForm);

    // pageableの設定
    Pageable pageable = PageRequest.of(0,
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    form.setPage(0);

    form.setSize(Integer.parseInt(
        messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    Page<ItemOrdersInfo> itemOrderList = mapItemOrderInfo(pageable, form);

    // 成功メッセージがあったときの処理
    String message = getSuccessMessage(messageFlag);

    model.addAttribute("itemSearchForm", itemSearchForm);
    model.addAttribute("itemOrderList", itemOrderList);
    model.addAttribute("roleList", Role.values());
    model.addAttribute("page", 0);
    model.addAttribute("successMessage", message);
    model.addAttribute("userRole", securitySession.getRole());

    return "item/list";
  }

  /**
   * 購入商品検索機能.
   *
   * @param page 現在いるページ
   * @param itemSearchForm 検索form
   * @param model viewへ変数を渡す
   * @return item/list.html
   */
  @RequestMapping(path = "/item/search", method = RequestMethod.GET)
  public String search(@RequestParam("page") int page,
                       ItemSearchForm itemSearchForm, Model model) {

    /*
    セッションからユーザー情報を取得
    未ログインはログイン画面へ遷移
    */

    /*
    フリーワードおよび購入日時で検索した際の表示を商品コードの昇順にするための設定を行う
    また、ページも1ページ目から始まるように設定する
     */
    if (StringUtils.isEmpty(itemSearchForm.getIdSort())
        && StringUtils.isEmpty(itemSearchForm.getPriceSort())
        && StringUtils.isEmpty(itemSearchForm.getTotalSort())
        && StringUtils.isEmpty(itemSearchForm.getCreateSort())) {

      itemSearchForm.setIdSort("asc");
      itemSearchForm.setPriceSort("");
      itemSearchForm.setTotalSort("");
      itemSearchForm.setCreateSort("");
      page = 0;
    }

    ItemOrdersForm form = mapItemOrdersForm(itemSearchForm);

    // pageableの設定
    Pageable pageable = PageRequest.of(page,
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    form.setPage(page * Integer.parseInt(
        messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    form.setSize(Integer.parseInt(
        messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    Page<ItemOrdersInfo> itemOrderList = mapItemOrderInfo(pageable, form);

    model.addAttribute("itemSearchForm", itemSearchForm);
    model.addAttribute("itemOrderList", itemOrderList);
    model.addAttribute("roleList", Role.values());
    model.addAttribute("page", page);
    model.addAttribute("userRole", securitySession.getRole());

    return "item/list";
  }

  /**
   * 購入商品の登録formへ遷移する.
   *
   * @param model viewへ変数を渡す
   * @return entry.html
   */
  @RequestMapping(path = "/item/form", method = RequestMethod.GET)
  public String forwardEntry(Model model) {

    /*
    セッションからユーザー情報を取得
    未ログインはログイン画面へ遷移
     */

    ItemForm itemForm = new ItemForm();

    // 購入商品登録のリクエストのため判断フラグにtrueをセット
    itemForm.setIsRegister(true);
    model.addAttribute("itemForm", itemForm);

    return "item/entry";
  }

  /**
   * 購入商品の編集formへ遷移する.
   *
   * @param id 管理id
   * @param model viewへ変数を渡す
   * @return entry.html
   */
  @RequestMapping(path = "/item/form/{id}", method = RequestMethod.GET)
  public String forwardEntry(@PathVariable int id, Model model) {

    /*
    セッションからユーザー情報を取得
    未ログインはログイン画面へ遷移
     */

    Orders order = ordersService.findById(id);
    ItemForm itemForm = mapItemForm(order);

    // 購入商品編集のリクエストのため判断フラグにfalseをセット
    itemForm.setIsRegister(false);
    model.addAttribute("itemForm", itemForm);

    return "item/entry";
  }

  /**
   * 購入商品登録・編集formに入力された値をチェックし、結果に応じたページへ遷移する.
   *
   * @param itemForm 購入商品登録・編集form
   * @param bindingResult formのvalidationチェック
   * @param model viewへ変数を渡す
   * @return success: item/entry_confirm, error: item/entry.html
   */
  @RequestMapping(path = "/item/form", method = RequestMethod.POST)
  public String forwardEntryConfirm(@Validated ItemForm itemForm,
                                    BindingResult bindingResult, Model model) {

    /*
    セッションからユーザー情報を取得
    未ログインはログイン画面へ遷移
     */

    // validationチェック
    List<FieldError> errorList = checkItemValidation(itemForm, bindingResult);
    if (!errorList.isEmpty()) {
      errorList.forEach(bindingResult::addError);
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("itemForm", itemForm);
      return "item/entry";
    }

    mapItemForm(itemForm);
    model.addAttribute("itemForm", itemForm);

    return "item/entry_confirm";
  }

  /**
   * データベースへ購入商品の登録または更新を行う.
   *
   * @param itemForm 購入商品登録・編集form
   * @param model viewへ変数を渡す
   * @param redirectAttributes redirect先へ変数を渡す
   * @return redirect:item/list
   */
  @RequestMapping(path = "/item/confirm", method = RequestMethod.POST)
  public String redirectItemList(ItemForm itemForm, Model model,
                                 RedirectAttributes redirectAttributes) {

    /*
    セッションからユーザー情報を取得
    未ログインはログイン画面へ遷移
     */

    // entry_confirm.htmlで戻るボタンが押下されたとき
    if (itemForm.getBackFlg() == 1) {
      model.addAttribute("itemForm", itemForm);
      return "item/entry";
    }

    Orders order = mapOrders(itemForm);

    if (itemForm.getIsRegister()) {
      ordersService.save(order);
      redirectAttributes.addFlashAttribute("successMessage",
          messageSource.getMessage("register", null, Locale.getDefault()));
    } else {
      ordersService.edit(order);
      redirectAttributes.addFlashAttribute("successMessage",
          messageSource.getMessage("edit", null, Locale.getDefault()));
    }

    return "redirect:/item/list";
  }

  /**
   * 商品マスタ一括登録機能.
   *
   * @param file アップロードされたCSVファイル
   * @return success: ステータスコード200, messageFlag　　fail: ステータスコード400, エラーメッセージ
   */
  @RequestMapping(path = "/item/upload", method = RequestMethod.POST)
  public ResponseEntity<Object> updateItems(@RequestParam(value = "file", required = false)
                                              MultipartFile file) {

    /*
    セッション情報から権限をチェックする
    セッションから作成者のuser_idを取得する
    userForm.setAuthor(作成者のuser_id)
     */

    // エラーメッセージをリクエストヘッダのmessageにセットするMapを用意
    HashMap<String, List<String>> error = new HashMap<>();

    // ファイルのvalidationチェック
    // 商品マスタのフォーマットでフォーマットチェックを行うよう判別フラグをセットする
    String flag = messageSource.getMessage("flag.itemMaster", null, Locale.getDefault());
    CsvUtil csvUtil = new CsvUtil(file, flag, messageSource);
    List<String> errorList = csvUtil.checkCsvFileValidation();
    if (!errorList.isEmpty()) {
      error.put("message", errorList);
      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    try (InputStream inputStream = file.getInputStream();
         CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {

      // CsvItemMasterFormに詰め替える
      List<CsvItemMasterForm> itemMasters = readCsvFile(csvReader);

      // validationチェック
      List<String> errorMessageList = checkItemValidation(itemMasters);
      errorList.addAll(errorMessageList);

      // validationチェックでerrorがあった際の処理
      if (!errorList.isEmpty()) {
        error.put("message", errorList);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      }

      List<Items> itemList = mapItems(itemMasters);
      itemsService.saveFromCsvItemMaster(itemList);

      // successMessageをリクエストヘッダのmessageにセット
      HashMap<String, String> success = new HashMap<>();
      success.put("message", messageSource.getMessage("upload", null, Locale.getDefault()));

      return new ResponseEntity<>(success, HttpStatus.OK);

    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 商品マスタダウンロード機能.
   *
   * @param response レスポンス
   */
  @RequestMapping(path = "/item/download", method = RequestMethod.POST)
  public void downloadItemMaster(HttpServletResponse response) {

    // DBから商品マスタを取得
    List<CsvItemMasterForm> itemMasterList = getItemMaster();

    // 空のファイルを作成
    String filePath = messageSource.getMessage("filePath", null, Locale.getDefault());
    File file = new File(filePath);

    // DBから取ってきたレコードを空ファイルへ書き込む
    writeCsvFile(itemMasterList, file);

    try (OutputStream outputStream = response.getOutputStream()) {

      // CSVファイルをbyte[]に変換する
      byte[] downloadFile =  Files.readAllBytes(Path.of(filePath));

      // クライアントにファイルをダウンロードさせるための処理
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename=itemMaster.csv");
      response.setContentLength(downloadFile.length);

      outputStream.write(downloadFile);
      outputStream.flush();

      // 不要になったファイルを削除
      Files.deleteIfExists(file.toPath());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 商品登録・編集画面で入力された値のvalidationチェックを行う.
   *
   * @param itemForm 商品登録・編集画面で入力されたform
   * @param bindingResult bindingResult
   * @return FieldError型 エラーメッセージ
   */
  private List<FieldError> checkItemValidation(ItemForm itemForm, BindingResult bindingResult) {

    List<FieldError> error = new ArrayList<>();

    // 商品マスタに登録されている商品コードかチェック
    if (!StringUtils.isEmpty(itemForm.getItemCode())) {
      Items item = itemsService.findByItemCode(itemForm.getItemCode());
      if (item == null) {
        error.add(new FieldError(bindingResult.getObjectName(), "itemCode",
            messageSource.getMessage("errMsg.invalidItemCode", null, Locale.getDefault())));
      }
    }
    return error;
  }

  /**
   * CSVファイルから読み込んだ商品マスタのvalidationチェックを行う.
   *
   * @param itemMasters CSVファイルから読み込んだ商品マスタ
   * @return String型のlist エラーメッセージ
   */
  private List<String> checkItemValidation(List<CsvItemMasterForm> itemMasters) {

    List<String> errorList = new ArrayList<>();
    int count = 0;

    for (CsvItemMasterForm form : itemMasters) {

      // 現在の行
      count++;

      /* 商品コード */
      String itemCode = messageSource.getMessage("itemCode", null, Locale.getDefault());
      String wordCount = messageSource.getMessage("limitOneTwenty", null, Locale.getDefault());
      String maxDigit = messageSource.getMessage("maxDigit", null, Locale.getDefault());

      // 未入力チェック
      if (StringUtils.isEmpty(form.getItemCode())) {
        errorList.add(messageSource.getMessage("NotBlank.csvDataForm",
            new String[]{String.valueOf(count), itemCode}, Locale.getDefault()));
      }

      // 桁数チェック
      if (Integer.parseInt(maxDigit) < form.getItemCode().length()) {
        errorList.add(messageSource.getMessage("Size.csvDataForm",
            new String[]{String.valueOf(count), itemCode, wordCount}, Locale.getDefault()));
      }

      // 重複チェック
      /*
       * 商品コードだけのリストを生成する
       * 現在チェックしている行と商品コードだけのリストを比較する
       * このとき、商品コードだけのリストの中には現在チェックしている行の商品コードも含まれているため、
       * 必ずduplicateItemCodeは1以上となる
       *
       * duplicateItemCode=1    重複なし
       * duplicateItemCode=2以上 重複あり
       */
      long duplicateItemCode = itemMasters.stream()
          .map(CsvItemMasterForm::getItemCode)
          .filter(code -> form.getItemCode().equals(code))
          .count();

      if (duplicateItemCode != 1) {
        errorList.add(messageSource.getMessage("errMsg.duplicate",
            new String[]{String.valueOf(count), itemCode}, Locale.getDefault()));
      }

      /* 商品名 */
      String itemName = messageSource.getMessage("itemName", null, Locale.getDefault());
      wordCount = messageSource.getMessage("limitOneTwoHundred", null, Locale.getDefault());
      maxDigit = messageSource.getMessage("maxDigit.itemName", null, Locale.getDefault());

      // 未入力チェック
      if (StringUtils.isEmpty(form.getItemName())) {
        errorList.add(messageSource.getMessage("NotBlank.csvDataForm",
            new String[]{String.valueOf(count), itemName}, Locale.getDefault()));
      }

      // 桁数チェック
      if (Integer.parseInt(maxDigit) < form.getItemName().length()) {
        errorList.add(messageSource.getMessage("Size.csvDataForm",
            new String[]{String.valueOf(count), itemName, wordCount}, Locale.getDefault()));
      }

      /* 単価 */
      String price = messageSource.getMessage("price", null, Locale.getDefault());

      // 未入力チェックSize.csvDateForm
      if (StringUtils.isEmpty(form.getPrice())) {
        errorList.add(messageSource.getMessage("NotBlank.csvDataForm",
            new String[]{String.valueOf(count), price}, Locale.getDefault()));
      }
      // 文字種チェック
      if (!form.getPrice().matches("^[0-9]+$")) {
        errorList.add(messageSource.getMessage("Pattern.csvItemMasterForm.price",
            new String[]{String.valueOf(count), price}, Locale.getDefault()));
      }
    }
    return errorList;
  }

  /**
   * OrdersからItemFormへ詰め替える.
   *
   * @param order 購入商品情報
   * @return ItemForm
   */
  private ItemForm mapItemForm(Orders order) {
    ItemForm form = new ItemForm();

    form.setId(order.getId());
    form.setItemCode(order.getItemCode());
    form.setCount(String.valueOf(order.getCount()));

    return form;
  }

  /**
   * formに入力された値から商品名、単価、合計を取得し、ItemFormにセットする.
   *
   * @param form ItemForm
   */
  private void mapItemForm(ItemForm form) {
    Items item = itemsService.findByItemCode(form.getItemCode());

    form.setItemCode(item.getItemCode());
    form.setItemName(item.getItemName());
    form.setPrice(item.getPrice());
    form.setTotalPrice(Integer.parseInt(form.getCount()) * item.getPrice());
  }

  /**
   * ItemFormからOrdersに詰め替える.
   *
   * @param form ItemForm
   * @return Orders
   */
  private Orders mapOrders(ItemForm form) {
    Orders order = new Orders();

    order.setId(form.getId());
    order.setItemCode(form.getItemCode());
    order.setCount(Integer.parseInt(form.getCount()));
    order.setUserId(securitySession.getUserId());
    order.setCreateDate(LocalDateTime.now());
    order.setUpdateUser(securitySession.getUserId());
    order.setUpdateDate(LocalDateTime.now());

    return order;
  }

  /**
   * ItemOrdersからItemOrdersInfoへ詰め替える.
   *
   * @param pageable ページネーションの設定
   * @param form ItemSearchForm
   * @return Page型のItemOrdersInfo
   */
  Page<ItemOrdersInfo> mapItemOrderInfo(Pageable pageable, ItemOrdersForm form) {
    List<ItemOrders> itemOrdersList = itemOrdersService.findOrders(form);

    List<ItemOrdersInfo> itemOrdersInfoList = new ArrayList<>();
    for (ItemOrders order : itemOrdersList) {
      ItemOrdersInfo info = new ItemOrdersInfo();
      info.setId(order.getId());
      info.setItemCode(order.getItemCode());
      info.setItemName(order.getItemName());
      info.setCount(order.getCount());
      info.setPrice(order.getPrice());
      info.setTotalPrice(order.getPrice() * order.getCount());
      info.setCreateDate(order.getCreateDate());
      info.setName(order.getName());
      itemOrdersInfoList.add(info);
    }

    // リストの総数
    int count = itemOrdersService.selectOrdersCount(form);

    return new PageImpl<>(itemOrdersInfoList, pageable, count);
  }

  /**
   * ItemSearchFormからItemOrdersFormへ詰め替える.
   *
   * @param itemSearchForm 検索form
   * @return ItemOrdersForm
   */
  private ItemOrdersForm mapItemOrdersForm(ItemSearchForm itemSearchForm) {
    ItemOrdersForm form = new ItemOrdersForm();

    if (itemSearchForm.getKeyword() != null) {
      form.setKeywords(changeAryKeywords(itemSearchForm));
    }

    if (!StringUtils.isEmpty(itemSearchForm.getStartAt())) {
      form.setStartAt(Date.valueOf(itemSearchForm.getStartAt()));
    }

    if (!StringUtils.isEmpty(itemSearchForm.getEndAt())) {
      form.setEndAt(Date.valueOf(itemSearchForm.getEndAt()));
    }

    form.setIdSort(itemSearchForm.getIdSort());
    form.setPriceSort(itemSearchForm.getPriceSort());
    form.setTotalSort(itemSearchForm.getTotalSort());
    form.setCreateSort(itemSearchForm.getCreateSort());

    return form;
  }

  /**
   * CSVファイルを読み込みCsvItemMasterFormへ詰め替える.
   *
   * @param csvReader csvReader
   * @return ListのCsvItemMasterForm
   */
  private List<CsvItemMasterForm> readCsvFile(CSVReader csvReader)
      throws CsvException, IOException {
    // ヘッダー行を飛ばす処理
    csvReader.readNext();

    // CsvItemMasterFormへ詰め替える
    CsvToBean<CsvItemMasterForm> csvToBean =
        new CsvToBeanBuilder<CsvItemMasterForm>(csvReader)
            .withType(CsvItemMasterForm.class).build();

    return csvToBean.parse();
  }

  private List<Items> mapItems(List<CsvItemMasterForm> form) {
    List<Items> itemList = new ArrayList<>();
    form.forEach(itemMaster -> {
      Items item = new Items();
      item.setItemCode(itemMaster.getItemCode());
      item.setItemName(itemMaster.getItemName());
      item.setPrice(Integer.parseInt(itemMaster.getPrice()));
      itemList.add(item);
    });
    return itemList;
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
    } else if ("upload".equals(messageFlag)) {
      message = messageSource.getMessage("upload.successMessage", null, Locale.getDefault());
    }

    return message;
  }

  /**
   * DBから商品マスタを取得し、ListのCsvItemMasterFormに詰め替える.
   *
   * @return listのCsvItemMasterForm　商品マスタ
   */
  private List<CsvItemMasterForm> getItemMaster() {
    List<Items> itemList = itemsService.findAll();

    List<CsvItemMasterForm> itemMasterList = new ArrayList<>();
    itemList.forEach(data -> {
      CsvItemMasterForm form = new CsvItemMasterForm();
      form.setItemCode(data.getItemCode());
      form.setItemName(data.getItemName());
      form.setPrice(String.valueOf(data.getPrice()));
      itemMasterList.add(form);
    });

    return itemMasterList;
  }

  /**
   * フリーワードが複数入力されている場合、半角または全角で区切る.
   */
  private String[] changeAryKeywords(ItemSearchForm form) {
    String[] keyword;

    // フリーワードが複数あるか確認
    if (form.getKeyword().matches(".*[\\s|　+].*")) {
      keyword = form.getKeyword().split("\\s|　+");
    } else {
      keyword = new String[]{form.getKeyword()};
    }
    return keyword;
  }

  /**
   * DBから取得した商品マスタをCSVファイルへ書き込む.
   *
   * @param itemMasterList DBから取得した商品マスタ
   * @param file 空のファイル
   */
  private void writeCsvFile(List<CsvItemMasterForm> itemMasterList, File file) {
    try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {

      // CsvBindByNameとCsvBindByPositionを紐づける処理
      CustomMappingStrategy<CsvItemMasterForm> mappingStrategy = new CustomMappingStrategy<>();
      mappingStrategy.setType(CsvItemMasterForm.class);

      // CSVファイルへ書き込み
      StatefulBeanToCsv<CsvItemMasterForm> beanToCsv
          = new StatefulBeanToCsvBuilder<CsvItemMasterForm>(csvWriter)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withApplyQuotesToAll(false)
          .withMappingStrategy(mappingStrategy)
          .build();
      beanToCsv.write(itemMasterList);
    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
      throw new RuntimeException(e);
    }
  }
}
