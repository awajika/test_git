package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
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
}
