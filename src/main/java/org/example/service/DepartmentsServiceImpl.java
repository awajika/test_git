package org.example.service;

import org.example.domain.Departments;
import org.example.repository.DepartmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentsServiceImpl implements DepartmentsService{

    @Autowired
    DepartmentsRepository departmentsRepository;

    /**
     * 所属IDと所属名を全件取得する
     * @return List<Departments>
     */
    @Override
    public List<Departments> findAll() {
        return departmentsRepository.findAll();
    }

    /**
     * 登録されている所属かチェックする
     * @return 登録されていない場合true、されている場合falseを返す
     */
    @Override
    public boolean checkDepartment(int departmentId) {
        return departmentsRepository.findByDepartmentId(departmentId) == null;
    }
}
