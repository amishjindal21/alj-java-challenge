package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Employee;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Cacheable("employeeCache")
    List<Employee> findAll();

    @Cacheable(value = "employeeCache", key = "#id")
    Optional<Employee> findById(Long id);
}
