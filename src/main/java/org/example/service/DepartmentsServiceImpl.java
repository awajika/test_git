package org.example.service;

import java.util.List;
import org.example.domain.Departments;
import org.example.form.DepartmentForm;
import org.example.repository.DepartmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DepartmentService実装クラス.
 */
@Service
public class DepartmentsServiceImpl implements DepartmentsService {

  @Autowired
  DepartmentsRepository departmentsRepository;

  /**
   * 所属IDと所属名を全件取得する.
   *
   * @return ListのDepartments
   */
  @Override
  public List<Departments> findAll() {
    return departmentsRepository.findAll();
  }

  /**
   * 登録されている所属かチェックする.
   *
   * @return 登録されていない場合true、されている場合falseを返す
   */
  @Override
  public boolean checkDepartment(DepartmentForm departmentForm) {
    return departmentsRepository.findByDepartmentId(departmentForm) == null;
  }

  /**
   * 所属名から該当する所属IDを取得する.
   *
   * @return departmentId
   */
  @Override
  public Integer getDepartmentId(DepartmentForm departmentForm) {
    return departmentsRepository.findByDepartmentId(departmentForm).getDepartmentId();
  }
}
