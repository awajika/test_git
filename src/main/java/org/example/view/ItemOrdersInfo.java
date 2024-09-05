package org.example.view;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * item/list.htmlへ表示するデータのエンティティ.
 */
@Data
public class ItemOrdersInfo {
  // 管理id
  private int id;
  // 商品コード
  private String itemCode;
  // 商品名
  private String itemName;
  // 数量
  private int count;
  // 単価
  private int price;
  // 合計金額
  private int totalPrice;
  // 購入日
  private LocalDateTime createDate;
  // 氏名
  private String name;
}
