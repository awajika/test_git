package org.example.service;

import java.util.List;
import org.example.domain.Departments;
import org.example.form.DepartmentForm;

/**
 * departmentsテーブルに関連するメソッドを持つService.
 */
public interface DepartmentsService {

  /**
   * 所属IDと所属名を全件取得する.
   *
   * @return ListのDepartments
   */
  List<Departments> findAll();

  /**
   * 登録されている所属かチェックする.
   *
   * @return 登録されていない場合true、されている場合falseを返す
   */
  boolean checkDepartment(DepartmentForm departmentForm);

  /**
   * 所属名から該当する所属IDを取得する.
   *
   * @return departmentId
   */
  Integer getDepartmentId(DepartmentForm departmentForm);
}
