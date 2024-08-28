package org.example.form;

import lombok.Data;

/**
 * CSVファイルで登録されるユーザーのフォーム.
 */
@Data
public class CsvUserForm {

  // 従業員ID
  private String userId;
  // 氏名
  private String name;
  // 氏名（カナ）
  private String nameKana;
  // パスワード
  private String password;
  // 所属名
  private String departmentName;
  // 所属ID
  private Integer departmentId;
  // 権限名
  private String label;
  // 権限
  private Integer role;
  // 状態
  private String status;
  // 削除フラグ
  private boolean deleteFlg;
  // 作成者
  private String author;
}
