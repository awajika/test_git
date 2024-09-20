package org.example.form;

import java.sql.Date;
import lombok.Data;

/**
 * ItemSearchFormのデータをItemOrdersMapperへ持っていくためのform.
 */
@Data
public class ItemOrdersForm {
  // フリーワード
  private String[] keywords;
  // 開始日
  private Date startAt;
  // 終了日
  private Date endAt;
  // 商品コードソート
  private String idSort;
  // 単価ソート
  private String priceSort;
  // 合計金額ソート
  private String totalSort;
  // 購入日時ソート
  private String createSort;
  // ユーザーが現在いるページ
  private int page;
  // データの最大表示件数
  private int size;
}
