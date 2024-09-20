package org.example.service;

import java.util.List;
import org.example.domain.ItemOrders;
import org.example.form.ItemOrdersForm;
import org.example.repository.ItemOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ItemOrdersService実装クラス.
 */
@Service
public class ItemOrdersServiceImpl implements ItemOrdersService {

  @Autowired
  ItemOrdersRepository itemOrdersRepository;

  /**
   * 削除フラグが立っていない購入商品レコードの件数を取得する.
   * もしitemSearchForm(検索条件)に値が入っていた場合、その条件に沿ったレコードの件数を取得する
   *
   * @param form 検索条件
   * @return int
   */
  @Override
  public int selectOrdersCount(ItemOrdersForm form) {
    return itemOrdersRepository.selectOrdersCount(form);
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
    return itemOrdersRepository.findOrders(form);
  }
}
