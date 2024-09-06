package org.example.service;

import org.example.domain.Items;

/**
 * itemsテーブルのService.
 */
public interface ItemsService {

  /**
   * 商品コードから商品を探す.
   *
   * @param itemCode 商品コード
   * @return 商品
   */
  Items findByItemCode(String itemCode);
}
