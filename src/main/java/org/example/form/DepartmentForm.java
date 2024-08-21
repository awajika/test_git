package org.example.form;

import lombok.Getter;
import lombok.Setter;

/**
 * DepartmentMapperへ値を送るためのフォーム.
 */
@Getter
@Setter
public class DepartmentForm {

  // 所属ID
  private Integer departmentId;
  // 所属名
  private String name;
}
