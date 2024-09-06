package org.example.repository;

import java.util.List;
import org.example.domain.Departments;
import org.example.form.DepartmentForm;
import org.example.mapper.DepartmentsMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DepartmentsRepository実装クラス.
 */
@Repository
public class DepartmentsRepositoryImpl implements DepartmentsRepository {

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * 所属IDと所属名を全件取得する.
   *
   * @return ListのDepartments
   */
  @Override
  public List<Departments> findAll() {
    return this.sqlSessionTemplate.getMapper(DepartmentsMapper.class).findAll();
  }

  /**
   * 所属IDもしくは所属名から一致する所属IDを探す.
   *
   * @param departmentForm DepartmentForm
   * @return Departments
   */
  @Override
  public Departments findByDepartmentId(DepartmentForm departmentForm) {
    return this.sqlSessionTemplate.getMapper(DepartmentsMapper.class)
        .findByDepartmentId(departmentForm);
  }
}
