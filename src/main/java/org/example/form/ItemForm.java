package org.example.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 購入する商品を登録・編集するform.
 */
@Data
public class ItemForm {
  // 商品登録の有無
  private Boolean isRegister;
  // 戻るボタン押下の判断フラグ
  private Integer backFlg;
  // 管理id
  private int id;
  // 商品コード
  @NotBlank
  @Size(max = 20)
  private String itemCode;
  // 商品名
  private String itemName;
  // 数量
  @NotBlank
  @Pattern(regexp = "^[0-9]+$")
  private String count;
  // 単価
  private int price;
  // 合計
  private int totalPrice;
  // 購入者
  private String userId;
  // 購入日
  private LocalDateTime createDate;
  // 更新者
  private String updateUser;
  // 更新日
  private LocalDateTime updateDate;
}
