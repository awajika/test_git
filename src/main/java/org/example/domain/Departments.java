package org.example.domain;

import lombok.Data;

/**
 * departmentsテーブルのdomain.
 */
@Data
public class Departments {
  // 所属ID
  private Integer departmentId;
  //　所属名
  private String name;
}
