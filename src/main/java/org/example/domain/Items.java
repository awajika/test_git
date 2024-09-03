package org.example.domain;

import lombok.Data;

/**
 * itemsテーブルのdomain.
 */
@Data
public class Items {
  // 商品コード
  private String itemCode;
  // 商品名
  private String itemName;
  // 単価
  private int price;
}
