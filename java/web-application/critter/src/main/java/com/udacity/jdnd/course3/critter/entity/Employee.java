package com.udacity.jdnd.course3.critter.entity;

import com.udacity.jdnd.course3.critter.user.EmployeeSkill;

import javax.persistence.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NamedQuery(
        name = "Employee.findEmployeesByIds",
        query = "select e from Employee e where e.user_id in :userIds")
@Entity
public class Employee extends User{

    @ElementCollection
    @CollectionTable(name = "employee_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private Set<EmployeeSkill> skills = new HashSet<>();

    @ElementCollection
    private Set<DayOfWeek> daysAvailable = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "Employee_Schedule",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "schedule_id") }
    )
    List<Schedule> schedules = new ArrayList<>();

    public Employee() {}

    public Employee(long userId, String employeeName, Set<EmployeeSkill> skills, Set<DayOfWeek> daysAvailable) {
        this.user_id = userId;
        this.name = employeeName;
        this.skills = skills;
        this.daysAvailable = daysAvailable;
    }

    public Set<EmployeeSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<EmployeeSkill> skills) {
        this.skills = skills;
    }

    public Set<DayOfWeek> getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(Set<DayOfWeek> daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }
    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}