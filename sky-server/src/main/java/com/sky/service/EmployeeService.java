package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employee
     * @return
     */
    void save(EmployeeDTO employee);

    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    PageResult PageSelect(EmployeePageQueryDTO pageQueryDTO);

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    void UseAndBan(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Employee SelectById(Long id);

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    void updateInfo(EmployeeDTO employeeDTO);
}
