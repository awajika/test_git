package org.example.repository;

import java.util.List;
import org.example.domain.Departments;

/**
 * departmentsテーブルに関連するメソッドを持つRepository.
 */
public interface DepartmentsRepository {

  /**
   * 所属IDと所属名を全件取得する.
   *
   * @return List型のDepartments
   */
  List<Departments> findAll();

  /**
   * 所属IDから所属を探す.
   *
   * @return Departments
   */
  Departments findByDepartmentId(int departmentId);
}
