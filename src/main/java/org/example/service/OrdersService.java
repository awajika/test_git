package org.example.service;

import org.example.domain.Orders;

/**
 * OrdersテーブルのService.
 */
public interface OrdersService {

  /**
   * 管理idから購入商品を探す.
   *
   * @param id 管理id
   * @return 購入商品
   */
  Orders findById(int id);

  /**
   * 購入商品を登録する.
   *
   * @param order 登録する予定の商品データ
   */
  void save(Orders order);

  /**
   * 購入商品を編集する.
   *
   * @param order 編集する予定の商品データ
   */
  void edit(Orders order);
}