package org.example.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.Items;

/**
 * itemsテーブルのMapper.
 */
@Mapper
public interface ItemsMapper {

  /**
   * 商品コードから商品を探す.
   *
   * @param itemCode 商品コード
   * @return 商品コード
   */
  Items findByItemCode(String itemCode);

  /**
   * CSVファイルから取得した商品マスタを登録、または更新する.
   *
   * @param itemList CSVファイルから取得した商品マスタ
   */
  void saveFromCsvItemMaster(@Param("itemList") List<Items> itemList);
}
