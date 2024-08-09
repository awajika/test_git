package org.example.form;

import lombok.Data;

@Data
public class UserSearchForm {

    private String keyword;
    private Integer departmentId;
    private Integer role;
    private String idSort;
    private String nameSort;
    private int page;
    private int size;

}
