package org.example.repository;

import org.example.domain.Departments;
import java.util.List;

public interface DepartmentsRepository {

    /**
     * 所属IDと所属名を全件取得する
     * @return List<Departments>
     */
    List<Departments> findAll();

    /**
     * 所属IDから所属を探す
     * @return Departments
     */
    Departments findByDepartmentId(int departmentId);
}
