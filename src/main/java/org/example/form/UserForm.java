package org.example.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserForm {

    // 従業員登録の有無
    private Boolean isRegister;
    // 戻るボタン押下を判断するためのフラグ
    private int backFlg;
    // 従業員ID
    @NotBlank(message = "社員番号を入力してください")
    @Size(max = 20, message = "20文字以内で入力してください")
    private String userId;
    // 氏名
    @NotBlank(message = "氏名を入力してください")
    @Size(max = 20, message = "20文字以内で入力してください")
    private String name;
    // 氏名（カナ）
    @NotBlank(message = "氏名（カナ）を入力してください")
    @Size(max = 20, message = "20文字以内で入力してください")
    @Pattern(regexp = "^[ァ-ンヴー]*$", message = "パスワードは全角カナで入力してください")
    private String nameKana;
    // パスワード
    @NotBlank(message = "パスワードを入力してください")
    @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "パスワードは英数字の組み合わせで入力してください")
    @Size(min = 8, max = 20, message = "8文字～20文字以内で入力してください")
    private String password;
    // パスワード（再入力）
    @NotBlank(message = "パスワード（再入力）を入力してください")
    @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "パスワードは英数字の組み合わせで入力してください")
    @Size(min = 8, max = 20, message = "8文字～20文字以内で入力してください")
    private String passwordConfirm;
    // 所属
    @NotNull(message = "所属を入力してください")
    private Integer departmentId;
    // 権限
    @NotNull(message = "権限を入力してください")
    private Integer role;
    // 作成者
    private String author;
}
