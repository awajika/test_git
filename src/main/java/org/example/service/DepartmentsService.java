package org.example.service;

import org.example.domain.Departments;
import java.util.List;

public interface DepartmentsService {

    /**
     * 所属IDと所属名を全件取得する
     * @return List<Departments>
     */
    List<Departments> findAll();
}
