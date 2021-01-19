package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        Schedule scheduleSaved = scheduleRepository.saveSchedule(scheduleDTO.getEmployeeIds(), scheduleDTO.getPetIds(), scheduleDTO.getActivities(), scheduleDTO.getDate());
        /*if (scheduleSaved == null) {
            throw new IllegalArgumentException("Can not create schedule because of conflict (Employee not found/Pet not found)");
        }*/
        return toScheduleDTO(scheduleSaved);
    }


    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAllSchedules();
        return schedules.stream().map(this::toScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        List<Schedule> schedules = petRepository.getScheduleByPetId(petId);
        return schedules.stream().map(this::toScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        List<Schedule> schedules = employeeRepository.getScheduleByEmployeeId(employeeId);
        return schedules.stream().map(this::toScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        List<Schedule> schedules = customerRepository.findCustomerScheduleById(customerId);
        return schedules.stream().map(this::toScheduleDTO).collect(Collectors.toList());
    }

    private ScheduleDTO toScheduleDTO(Schedule schedule) {
        if (schedule == null) {
            return new ScheduleDTO();
        }
        List<Long> petIds = schedule.getPets().stream().map(Pet::getPet_id).collect(Collectors.toList());
        List<Long> employeeIds = schedule.getEmployees().stream().map(Employee::getUser_id).collect(Collectors.toList());
        return new ScheduleDTO(schedule.getSchedule_id(), employeeIds, petIds, schedule.getDate(), schedule.getActivities());
    }
}
