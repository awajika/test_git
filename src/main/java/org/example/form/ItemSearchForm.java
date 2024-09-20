package org.example.form;

import lombok.Data;

/**
 * 購入商品検索form.
 */
@Data
public class ItemSearchForm {
  // フリーワード
  private String keyword;
  // 開始日
  private String startAt;
  // 終了日
  private String endAt;
  // 商品コードソート
  private String idSort;
  // 単価ソート
  private String priceSort;
  // 合計金額ソート
  private String totalSort;
  // 購入日時ソート
  private String createSort;
}
