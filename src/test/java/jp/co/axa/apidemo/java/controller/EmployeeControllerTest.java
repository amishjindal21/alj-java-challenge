package jp.co.axa.apidemo.java.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import jp.co.axa.apidemo.services.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void testSaveEmployee() throws Exception {

        Employee employee = new Employee(1L, "Amish Jindal", 2000000, "Fintech");

        mvc.perform(post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employee)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void testGetEmployees()
            throws Exception {

        Employee employee = new Employee(1L, "Amish Jindal", 2000000, "Fintech");

        List<Employee> allEmployees = Arrays.asList(employee);

        when(employeeService.retrieveEmployees()).thenReturn(allEmployees);

        mvc.perform(get("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(employee.getName())));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void testGetEmployeeById() throws Exception {
        Employee employee = new Employee(1L, "Amish Jindal", 2000000, "Fintech");

        when(employeeService.getEmployee(1L)).thenReturn(employee);

        mvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Amish Jindal")))
                .andExpect(jsonPath("$.salary", is(2000000)))
                .andExpect(jsonPath("$.department", is("Fintech")));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void testUpdateEmployeeIfExist() throws Exception {

        Employee employee = new Employee("Amish Jindal", 4000000, "Payment dept");
        employee.setId(1L);

        mvc.perform(put("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employee)))
                .andExpect(status().isOk());

        when(employeeService.getEmployee(1L)).thenReturn(employee);

        assertEquals(employee.getSalary(), Integer.valueOf(4000000));
        assertEquals(employee.getDepartment(), "Payment dept");
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void testUpdateEmployeeIfNotExist() throws Exception {

        Employee employee = new Employee("Amish Jindal", 4000000, "Payment dept");
        employee.setId(2L);

        mvc.perform(put("/api/v1/employees/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employee)))
                .andExpect(status().isOk());

        assertNull(employeeRepository.getOne(2L));

    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void testDeleteEmployeeById() throws Exception {

        mvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isOk()).andReturn();

        // Verify that the employee was deleted
        assertNull(employeeService.getEmployee(1L));
    }

}
