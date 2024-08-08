package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.domain.Departments;
import java.util.List;

@Mapper
public interface DepartmentsMapper {

    /**
     * 所属IDと所属名を全件取得する
     * @return List<Departments>
     */
    List<Departments> findAll();

    /**
     * 所属IDから所属を1件取得する
     * @return Departments
     */
    Departments findByDepartmentId(int departmentId);
}
