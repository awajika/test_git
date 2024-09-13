package org.example.form;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

/**
 * CSVファイルで登録する商品マスタのform.
 */
@Data
public class CsvItemMasterForm {
  // 商品コード
  @CsvBindByName(column = "商品コード", required = true)
  @CsvBindByPosition(position = 0)
  private String itemCode;
  // 商品名
  @CsvBindByName(column = "商品名", required = true)
  @CsvBindByPosition(position = 1)
  private String itemName;
  // 単価
  @CsvBindByName(column = "単価", required = true)
  @CsvBindByPosition(position = 2)
  private String price;
}
