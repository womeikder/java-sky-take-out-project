package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 将前端传递的数据进行加密后与数据库中密码进行匹配
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {

        Employee employee = new Employee();
        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        // 设置员工状态
        // 调用常量规定的状态，方便管理
        employee.setStatus(StatusConstant.ENABLE);
        // 设置默认密码，md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        /*// 设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        // 设置修改时间
        employee.setUpdateTime(LocalDateTime.now());

        // 调用本地线程中的方法获取用户id
        Long currentId = BaseContext.getCurrentId();
        // 创建人
        employee.setCreateUser(currentId);
        // 修改人
        employee.setUpdateUser(currentId);*/

        employeeMapper.insert(employee);


    }

    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult PageSelect(EmployeePageQueryDTO pageQueryDTO) {
        // 通过mybatis提供的插件实现limit功能
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        // 通过插件实现的分页功能都会被封装为一个page对象，底层就是一个list集合
        Page<Employee> paged = employeeMapper.PageSelect(pageQueryDTO);
        // 获取查询后返回的结果
        long total = paged.getTotal();
        List<Employee> result = paged.getResult();
        // 新建对象传递参数返回
        return new PageResult(total,result);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void UseAndBan(Integer status, Long id) {
        // 通过构造器创建对象
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee SelectById(Long id) {
        Employee employee = employeeMapper.SelectById(id);
        employee.setPassword(null);
        return employee;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    @Override
    public void updateInfo(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 对象拷贝、获取本地线程的用户
        BeanUtils.copyProperties(employeeDTO,employee);

/*        Long currentId = BaseContext.getCurrentId();
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(currentId);*/

        employeeMapper.update(employee);
    }
}
