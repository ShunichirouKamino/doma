/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.it.auto;

import static junit.framework.Assert.*;

import java.sql.Time;
import java.util.List;

import org.junit.runner.RunWith;
import org.seasar.doma.it.dao.DepartmentDao;
import org.seasar.doma.it.dao.DepartmentDaoImpl;
import org.seasar.doma.it.dao.FunctionDao;
import org.seasar.doma.it.dao.FunctionDaoImpl;
import org.seasar.doma.it.entity.Department;
import org.seasar.doma.it.entity.Employee;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.Prerequisite;

@RunWith(Seasar2.class)
@Prerequisite("#ENV not in {'hsqldb', 'h2'}")
public class AutoFunctionTest {

    public void testNoParam() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        Integer result = dao.func_none_param();
        assertEquals(new Integer(10), result);
    }

    public void testOneParam() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        Integer result = dao.func_simpletype_param(new Integer(10));
        assertEquals(new Integer(20), result);
    }

    public void testOneParam_time() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        Time result = dao.func_simpletype_time_param(Time.valueOf("12:34:56"));
        assertEquals(Time.valueOf("12:34:56"), result);
    }

    public void testTwoParams() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        Integer result = dao.func_dto_param(new Integer(10), new Integer(20));
        assertEquals(new Integer(30), result);
    }

    public void testTwoParams_time() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        Time result = dao.func_dto_time_param(Time.valueOf("12:34:56"),
                new Integer(20));
        assertEquals(Time.valueOf("12:34:56"), result);
    }

    @Prerequisite("#ENV not in {'mysql'}")
    public void testResultSet() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        List<Employee> result = dao.func_resultset(new Integer(1));
        assertEquals(13, result.size());
    }

    @Prerequisite("#ENV not in {'mysql'}")
    public void testResultSetAndUpdate() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        List<Employee> result = dao.func_resultset_update(new Integer(1));
        assertEquals(13, result.size());
        DepartmentDao departmentDao = new DepartmentDaoImpl();
        Department department = departmentDao.selectById(new Integer(1));
        assertEquals("HOGE", department.getDepartmentName());
    }

    @Prerequisite("#ENV not in {'mysql'}")
    public void testResultSetAndUpdate2() throws Exception {
        FunctionDao dao = new FunctionDaoImpl();
        List<Employee> result = dao.func_resultset_update2(new Integer(1));
        assertEquals(13, result.size());
        DepartmentDao departmentDao = new DepartmentDaoImpl();
        Department department = departmentDao.selectById(new Integer(1));
        assertEquals("HOGE", department.getDepartmentName());
    }

}
