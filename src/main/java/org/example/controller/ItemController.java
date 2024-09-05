package org.example.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.example.constant.Role;
import org.example.domain.ItemOrders;
import org.example.domain.Items;
import org.example.domain.Orders;
import org.example.form.ItemForm;
import org.example.form.ItemSearchForm;
import org.example.service.ItemOrdersService;
import org.example.service.ItemsService;
import org.example.service.OrdersService;
import org.example.view.ItemOrdersInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

/**
 * 購入商品に関する処理を行うcontroller.
 */
@Controller
public class ItemController {

  @Autowired
  ItemsService itemsService;

  @Autowired
  OrdersService ordersService;

  @Autowired
  ItemOrdersService itemOrdersService;

  @Autowired
  MessageSource messageSource;

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

    // pageableの設定
    Pageable pageable = PageRequest.of(0,
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    itemSearchForm.setPage(0);

    itemSearchForm.setSize(
        Integer.parseInt(messageSource.getMessage("maxPageSize", null, Locale.getDefault())));

    Page<ItemOrdersInfo> itemOrderList = mapItemOrderInfo(pageable, itemSearchForm);

    // 成功メッセージがあったときの処理
    String message = getSuccessMessage(messageFlag);

    model.addAttribute("itemSearchForm", itemSearchForm);
    model.addAttribute("itemOrderList", itemOrderList);
    model.addAttribute("roleList", Role.values());
    model.addAttribute("successMessage", message);

    return "item/list";
  }

  /**
   * 購入商品の登録formへ遷移する.
   *
   * @param model viewへ変数を渡す
   * @return entry.html
   */
  @RequestMapping(path = "/item/entry", method = RequestMethod.GET)
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
  @RequestMapping(path = "/item/entry/{id}", method = RequestMethod.GET)
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
  @RequestMapping(path = "/item/entry", method = RequestMethod.POST)
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
  @RequestMapping(path = "/item/entry_confirm", method = RequestMethod.POST)
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
    order.setUserId("0001");
    order.setCreateDate(LocalDateTime.now());
    order.setUpdateUser("0001");
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
  Page<ItemOrdersInfo> mapItemOrderInfo(Pageable pageable, ItemSearchForm form) {
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
    }

    return message;
  }
}
