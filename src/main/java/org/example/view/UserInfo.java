package org.example.view;

import lombok.Data;

@Data
public class UserInfo {
    // 社員番号
    private String userId;
    // 氏名
    private String name;
    // 氏名（カナ）
    private String nameKana;
    // 所属
    private Integer departmentId;
    // 権限
    private Integer role;
}
