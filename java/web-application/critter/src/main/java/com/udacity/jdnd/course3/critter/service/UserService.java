package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.utility.DTOUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static com.udacity.jdnd.course3.critter.utility.DTOUtility.*;
import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PetRepository petRepository;

    public CustomerDTO saveCustomer(CustomerDTO customerDTO){
        Customer customerSaved = customerRepository.save(toCustomer(customerDTO));
        return toCustomerDTO(customerSaved);
    }

    public CustomerDTO getCustomer(long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId);
        return toCustomerDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = customerRepository.getAllCustomer();
        return customers.stream().map(DTOUtility::toCustomerDTO).collect(toList());
    }

    public CustomerDTO getOwnerByPet(long petId){
        Pet pet = petRepository.getPetById(petId);
        if (pet == null) {
            throw new PetNotFoundException();
        }
        return toCustomerDTO(pet.getOwner());
    }

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        Employee employeeSaved = employeeRepository.saveEmployee(toEmployee(employeeDTO));
        return toEmployeeDTO(employeeSaved);
    }

    public EmployeeDTO getEmployee(long employeeId) {
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        return toEmployeeDTO(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.getAllEmployees();
        return employees.stream().map(DTOUtility::toEmployeeDTO).collect(toList());
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        employeeRepository.setEmployeeAvailabilityById(daysAvailable, employeeId);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeRequestDTO) {
        List<Employee> employees = employeeRepository.findEmployeeByDateAndSkill(employeeRequestDTO.getDate(), employeeRequestDTO.getSkills());
        return employees.stream().map(DTOUtility::toEmployeeDTO).collect(toList());
    }
}
