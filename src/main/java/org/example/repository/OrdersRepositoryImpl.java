package org.example.repository;

import org.example.domain.Orders;
import org.example.mapper.OrdersMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * OrdersRepository実装クラス.
 */
@Repository
public class OrdersRepositoryImpl implements OrdersRepository {

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * 管理idから購入商品を探す.
   *
   * @param id 管理id
   * @return 購入商品
   */
  @Override
  public Orders findById(int id) {
    return this.sqlSessionTemplate.getMapper(OrdersMapper.class).findById(id);
  }

  /**
   * 購入商品を登録する.
   *
   * @param order 登録する予定の商品データ
   */
  @Override
  public void save(Orders order) {
    this.sqlSessionTemplate.getMapper(OrdersMapper.class).save(order);
  }

  /**
   * 購入商品を編集する.
   *
   * @param order 編集する予定の商品データ
   */
  @Override
  public void edit(Orders order) {
    this.sqlSessionTemplate.getMapper(OrdersMapper.class).edit(order);
  }
}
