package org.example.domain;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * OrdersテーブルとItemsテーブルを内部結合したテーブルのdomain.
 */
@Data
public class ItemOrders {
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
  // 購入者
  private String name;
  // 購入日
  private LocalDateTime createDate;
}
