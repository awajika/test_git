package org.example.form;

import lombok.Data;

@Data
public class UserSearchForm {

    // フリーワード
    private String keyword;
    // 配列にしたフリーワード
    private String[] aryKeywords;
    // 所属ID
    private Integer departmentId;
    // 権限
    private Integer role;
    // 社員番号ソート
    private String idSort;
    // 氏名ソート
    private String nameSort;
    // ユーザーが現在いるページ
    private int page;
    // データの最大表示件数
    private int size;
    // 現在のURL
    private String currentUrl;

}
