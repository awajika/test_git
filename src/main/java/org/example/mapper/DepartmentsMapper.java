package org.example.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.example.domain.Departments;

/**
 * departmentsテーブルのmapper.
 */
@Mapper
public interface DepartmentsMapper {

  /**
   * 所属IDと所属名を全件取得する.
   *
   * @return List型のDepartments
   */
  List<Departments> findAll();

  /**
   * 所属IDから所属を1件取得する.
   *
   * @return Departments
   */
  Departments findByDepartmentId(int departmentId);
}
