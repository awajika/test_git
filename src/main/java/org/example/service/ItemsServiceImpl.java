package org.example.service;

import java.util.List;
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
   * 現在登録されている商品マスタを全て取得する.
   *
   * @return 現在登録されている商品マスタ
   */
  @Override
  public List<Items> findAll() {
    return itemsRepository.findAll();
  }

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

  /**
   * CSVファイルから取得した商品マスタを登録、または更新する.
   *
   * @param itemList CSVファイルから取得した商品マスタ
   */
  @Override
  public void saveFromCsvItemMaster(List<Items> itemList) {
    itemsRepository.saveFromCsvItemMaster(itemList);
  }
}
