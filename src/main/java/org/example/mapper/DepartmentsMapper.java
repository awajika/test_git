package org.example.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.Departments;
import org.example.form.DepartmentForm;

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
   * 所属IDもしくは所属名から一致する所属IDを探す.
   *
   * @return Departments
   */
  Departments findByDepartmentId(@Param("departmentForm")DepartmentForm departmentForm);

}
