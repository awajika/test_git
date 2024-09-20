package org.example.repository;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.example.domain.Items;

/**
 * itemsテーブルのRepository.
 */
public interface ItemsRepository {

  /**
   * 現在登録されている商品マスタを全て取得する.
   *
   * @return 現在登録されている商品マスタ
   */
  List<Items> findAll();

  /**
   * 商品コードから商品を探す.
   *
   * @param itemCode 商品コード
   * @return 商品
   */
  Items findByItemCode(String itemCode);

  /**
   * CSVファイルから取得した商品マスタを登録、または更新する.
   *
   * @param itemList CSVファイルから取得した商品マスタ
   */
  void saveFromCsvItemMaster(@Param("itemList") List<Items> itemList);
}
