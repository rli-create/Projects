package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class EmployeeRepository {

    @PersistenceContext
    EntityManager entityManager;

    public Employee saveEmployee(Employee employee) { return entityManager.merge(employee); }

    public Employee findEmployeeById(Long id) { return entityManager.find(Employee.class, id); }

    public void setEmployeeAvailabilityById(Set<DayOfWeek> availableDays, Long employeeId) {
        Employee e = entityManager.find(Employee.class, employeeId);
        e.setDaysAvailable(availableDays);
        entityManager.merge(e);
    }

    public List<Employee> findEmployeeByDateAndSkill(LocalDate date, Set<EmployeeSkill> employeeSkills) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        TypedQuery<Long> query = entityManager.createQuery("select e.user_id from Employee e join e.skills es where :day member of e.daysAvailable and es in :skills group by e.user_id having count(es) = :skillsCount", Long.class);
        query.setParameter("day", dayOfWeek);
        query.setParameter("skills", employeeSkills);
        query.setParameter("skillsCount", (long)employeeSkills.size());
        List<Long> userIds = query.getResultList();
        if (userIds.isEmpty()) {
            userIds = null;
        }
        TypedQuery<Employee> userQuery = entityManager.createNamedQuery("Employee.findEmployeesByIds", Employee.class);
        userQuery.setParameter("userIds", userIds);
        return userQuery.getResultList();
    }

    public List<Schedule> getScheduleByEmployeeId(Long employeeId) {
        TypedQuery<Schedule> query = entityManager.createQuery("select es from Employee e join e.schedules es where e.user_id = :employeeId", Schedule.class);
        query.setParameter("employeeId", employeeId);
        return query.getResultList();
    }

    public List<Employee> getAllEmployees() {
        TypedQuery<Employee> query = entityManager.createQuery("select e from Employee e", Employee.class);
        return query.getResultList();
    }
}
