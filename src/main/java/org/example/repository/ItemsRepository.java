package org.example.repository;

import org.example.domain.Items;

/**
 * itemsテーブルのRepository.
 */
public interface ItemsRepository {

  /**
   * 商品コードから商品を探す.
   *
   * @param itemCode 商品コード
   * @return 商品
   */
  Items findByItemCode(String itemCode);
}
