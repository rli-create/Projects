package com.udacity.jdnd.course3.critter.utility;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class DTOUtility {

    public static Pet toPet(PetDTO petDTO) {
        Customer c = new Customer();
        c.setUser_id(petDTO.getOwnerId());
        return new Pet(petDTO.getType(), petDTO.getName(), c, petDTO.getBirthDate(), petDTO.getNotes());
    }

    public static PetDTO toPetDTO(Pet pet) {
        if (pet == null) {
            return new PetDTO();
        }
        Customer owner = pet.getOwner();
        return new PetDTO(pet.getPet_id(), pet.getType(), pet.getName(), owner == null? null : owner.getUser_id(), pet.getBirthDate(), pet.getNotes());
    }

    public static ScheduleDTO toScheduleDTO(Schedule schedule) {
        if (schedule == null) {
            return new ScheduleDTO();
        }
        List<Long> petIds = schedule.getPets().stream().map(Pet::getPet_id).collect(Collectors.toList());
        List<Long> employeeIds = schedule.getEmployees().stream().map(Employee::getUser_id).collect(Collectors.toList());
        return new ScheduleDTO(schedule.getSchedule_id(), employeeIds, petIds, schedule.getDate(), schedule.getActivities());
    }

    public static Customer toCustomer(CustomerDTO customerDTO) {
        List<Pet> pets = new ArrayList<>();
        List<Long> petIds = customerDTO.getPetIds();
        if (petIds != null) {
            for (Long petId : petIds) {
                /*
                Pet pet = petRepository.getPetById(petId);
                if (pet == null) {
                    pet = new Pet();
                    pet.setPet_id(petId);
                }*/
                Pet pet = new Pet();
                pet.setPet_id(petId);
                pets.add(pet);
            }
        }
        return new Customer(customerDTO.getId(), customerDTO.getName(), customerDTO.getPhoneNumber(), customerDTO.getNotes(), pets);
    }

    public static CustomerDTO toCustomerDTO(Customer customer) {
        if (customer == null) {
            return new CustomerDTO();
        }
        List<Long> petIds = customer.getPets().stream().map(Pet::getPet_id).collect(toList());
        return new CustomerDTO(customer.getUser_id(), customer.getName(), customer.getPhoneNumber(), customer.getNotes(), petIds);
    }

    public static Employee toEmployee(EmployeeDTO employeeDTO) {
        return new Employee(employeeDTO.getId(), employeeDTO.getName(), employeeDTO.getSkills(), employeeDTO.getDaysAvailable());
    }

    public static EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) {
            return new EmployeeDTO();
        }
        return new EmployeeDTO(employee.getUser_id(), employee.getName(), employee.getSkills(), employee.getDaysAvailable());
    }
}
