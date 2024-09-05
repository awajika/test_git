package org.example.repository;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.example.domain.ItemOrders;
import org.example.form.ItemSearchForm;

/**
 * OrdersテーブルとItemsテーブルを内部結合したテーブルのRepository.
 */
public interface ItemOrdersRepository {

  /**
   * 削除フラグが立っていない購入商品レコードの件数を取得する.
   * もしitemSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @param itemSearchForm 検索条件
   * @return int
   */
  int selectOrdersCount(@Param("itemSearchForm") ItemSearchForm itemSearchForm);

  /**
   * 削除フラグが立っていない購入商品データをpageableで設定した分だけ取得する.
   * もしitemSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードを取得する
   *
   * @param itemSearchForm 検索条件
   * @return List型のItemOrders
   */
  List<ItemOrders> findOrders(@Param("itemSearchForm")ItemSearchForm itemSearchForm);
}
