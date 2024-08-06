package org.example.domain;

import lombok.Data;

@Data
public class Users {

    // 従業員ID
    private String user_id;
    // パスワード
    private String password;
    // 氏名
    private String name;
    // 氏名（カナ）
    private String name_kana;
    // 所属
    private int department_id;
    // 権限
    private int role;
    // 作成者ID
    private String create_user;
    // 作成日
    private String create_date;
    // 最終更新者ID
    private String update_user;
    // 最終更新日
    private String update_date;
    // 削除フラグ
    private boolean is_delete;
}
