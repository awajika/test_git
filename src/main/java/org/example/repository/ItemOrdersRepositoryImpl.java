package org.example.repository;

import java.util.List;
import org.example.domain.ItemOrders;
import org.example.form.ItemOrdersForm;
import org.example.mapper.ItemOrdersMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ItemOrdersRepository実装クラス.
 */
@Repository
public class ItemOrdersRepositoryImpl implements ItemOrdersRepository {

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * 削除フラグが立っていない購入商品レコードの件数を取得する.
   * もしitemSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @param form 検索条件
   * @return int
   */
  @Override
  public int selectOrdersCount(ItemOrdersForm form) {
    return this.sqlSessionTemplate
        .getMapper(ItemOrdersMapper.class).selectOrdersCount(form);
  }

  /**
   * 削除フラグが立っていない購入商品データをpageableで設定した分だけ取得する.
   * もしitemSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードを取得する
   *
   * @param form 検索条件
   * @return List型のItemOrders
   */
  @Override
  public List<ItemOrders> findOrders(ItemOrdersForm form) {
    return this.sqlSessionTemplate.getMapper(ItemOrdersMapper.class).findOrders(form);
  }
}
