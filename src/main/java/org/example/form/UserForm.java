package org.example.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

@Data
public class UserForm {

    @Autowired
    MessageSource messageSource;

    // 従業員登録の有無
    private Boolean isRegister;
    // 戻るボタン押下を判断するためのフラグ
    private int backFlg;
    // 従業員ID
    @NotBlank
    @Size(max = 20)
    private String userId;
    // 氏名
    @NotBlank
    @Size(max = 20)
    private String name;
    // 氏名（カナ）
    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[ァ-ンヴー]*$")
    private String nameKana;
    // パスワード
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]*$")
    @Size(min = 8, max = 20)
    private String password;
    // パスワード（再入力）
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]*$")
    @Size(min = 8, max = 20)
    private String passwordConfirm;
    // 所属
    @NotNull
    private Integer departmentId;
    // 権限
    @NotNull
    private Integer role;
    // 作成者
    private String author;
}
