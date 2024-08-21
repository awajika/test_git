package org.example.repository;

import java.util.List;
import org.example.domain.Departments;
import org.example.form.DepartmentForm;

/**
 * departmentsテーブルに関連するメソッドを持つRepository.
 */
public interface DepartmentsRepository {

  /**
   * 所属IDと所属名を全件取得する.
   *
   * @return ListのDepartments
   */
  List<Departments> findAll();

  /**
   * 所属IDもしくは所属名から一致する所属IDを探す.
   *
   * @return Departments
   */
  Departments findByDepartmentId(DepartmentForm departmentForm);
}
