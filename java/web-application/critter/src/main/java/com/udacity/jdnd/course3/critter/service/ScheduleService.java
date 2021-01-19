package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.utility.DTOUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.utility.DTOUtility.toScheduleDTO;

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
        return toScheduleDTO(scheduleSaved);
    }


    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAllSchedules();
        return schedules.stream().map(DTOUtility::toScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        List<Schedule> schedules = petRepository.getScheduleByPetId(petId);
        return schedules.stream().map(DTOUtility::toScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        List<Schedule> schedules = employeeRepository.getScheduleByEmployeeId(employeeId);
        return schedules.stream().map(DTOUtility::toScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        List<Schedule> schedules = customerRepository.findCustomerScheduleById(customerId);
        return schedules.stream().map(DTOUtility::toScheduleDTO).collect(Collectors.toList());
    }
}
