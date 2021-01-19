package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class ScheduleRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Schedule saveSchedule(List<Long> employeeIds, List<Long> petIds, Set<EmployeeSkill> skillSet, LocalDate date) {
        /*if (employeeIds.size() != petIds.size()) {
            return null;
        }*/
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<Employee> employeeList = new ArrayList<>();
        List<Pet> petList = new ArrayList<>();
        for (Long id : employeeIds) {
            //employee managed
            Employee e = entityManager.find(Employee.class, id);
            if (e == null) {
                System.out.println(String.format("Employee with id {0} not found", id));
            }
            /*
            if (!e.getDaysAvailable().contains(dayOfWeek)) {
                System.out.println("Employee not available");
                return null;
            }
            if (!e.getSkills().containsAll(skillSet)) {
                System.out.println("Employee not skilled");
                return null;
            }*/
            e.getDaysAvailable().remove(dayOfWeek);
            e = entityManager.merge(e);
            employeeList.add(e);
        }
        for (Long id : petIds) {
            //pet managed
            Pet pet = entityManager.find(Pet.class, id);
            if (pet == null) {
                System.out.println(String.format("Pet with id {0} not found", id));
            }
            petList.add(pet);
        }
        Schedule schedule = new Schedule(employeeList, petList, date, skillSet);
        Schedule scheduleSaved = entityManager.merge(schedule);

        for (Employee e : employeeList) {
            e.getSchedules().add(scheduleSaved);
        }

        for (Pet p: petList) {
            p.getSchedules().add(scheduleSaved);
        }

        return scheduleSaved;
    }

    public List<Schedule> findAllSchedules() {
        TypedQuery<Schedule> query = entityManager.createNamedQuery("Employee.findAllSchedules", Schedule.class);
        return query.getResultList();
    }
}
