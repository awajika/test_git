package org.example.service;

import org.example.domain.Orders;
import org.example.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrdersService実装クラス.
 */
@Service
public class OrdersServiceImpl implements OrdersService {

  @Autowired
  OrdersRepository ordersRepository;

  /**
   * 管理idから購入商品を探す.
   *
   * @param id 管理id
   * @return 購入商品
   */
  @Override
  public Orders findById(int id) {
    return ordersRepository.findById(id);
  }

  /**
   * 購入商品を登録する.
   *
   * @param order 登録する予定の商品データ
   */
  @Override
  public void save(Orders order) {
    ordersRepository.save(order);
  }

  /**
   * 購入商品を編集する.
   *
   * @param order 編集する予定の商品データ
   */
  @Override
  public void edit(Orders order) {
    ordersRepository.edit(order);
  }
}
