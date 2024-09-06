package org.example.service;

import org.example.domain.Items;
import org.example.repository.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ItemsService実装クラス.
 */
@Service
public class ItemsServiceImpl implements ItemsService {

  @Autowired
  ItemsRepository itemsRepository;

  /**
   * 商品コードから商品を探す.
   *
   * @param itemCode 商品コード
   * @return 商品
   */
  @Override
  public Items findByItemCode(String itemCode) {
    return itemsRepository.findByItemCode(itemCode);
  }
}
