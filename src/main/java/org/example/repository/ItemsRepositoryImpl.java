package org.example.repository;

import org.example.domain.Items;
import org.example.mapper.ItemsMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ItemsRepository実装クラス.
 */
@Repository
public class ItemsRepositoryImpl implements ItemsRepository {

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * 商品コードから商品を探す.
   *
   * @param itemCode 商品コード
   * @return 商品
   */
  @Override
  public Items findByItemCode(String itemCode) {
    return this.sqlSessionTemplate.getMapper(ItemsMapper.class).findByItemCode(itemCode);
  }
}
