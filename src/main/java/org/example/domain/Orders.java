package org.example.domain;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Orderテーブルのdomain.
 */
@Data
public class Orders {
  // 管理id
  private int id;
  // 商品コード
  private String itemCode;
  // 数量
  private int count;
  // 購入者
  private String userId;
  // 購入日
  private LocalDateTime createDate;
  // 更新者
  private String updateUser;
  // 更新日
  private LocalDateTime updateDate;
  // 削除フラグ
  private boolean isDelete;
}
