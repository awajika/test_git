package org.example.form;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

/**
 * CSVファイルで登録する商品マスタのform.
 */
@Data
public class CsvItemMasterForm {
  // 商品コード
  @CsvBindByPosition(position = 0)
  private String itemCode;
  // 商品名
  @CsvBindByPosition(position = 1)
  private String itemName;
  // 単価
  @CsvBindByPosition(position = 2)
  private String price;
}
