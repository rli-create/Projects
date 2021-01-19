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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        //System.out.println("Customer Id: " + customerSaved.getUser_id());
        return toCustomerDTO(customerSaved);
    }

    public CustomerDTO getCustomer(long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId);
        return toCustomerDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = customerRepository.getAllCustomer();
        return customers.stream().map(this::toCustomerDTO).collect(toList());
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
        /*
        if (employee == null) {
            throw new EntityNotFoundException(String.format("Employee with id: {0} not found", employeeId));
        }*/
        return toEmployeeDTO(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.getAllEmployees();
        return employees.stream().map(this::toEmployeeDTO).collect(toList());
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        employeeRepository.setEmployeeAvailabilityById(daysAvailable, employeeId);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeRequestDTO) {
        List<Employee> employees = employeeRepository.findEmployeeByDateAndSkill(employeeRequestDTO.getDate(), employeeRequestDTO.getSkills());
        return employees.stream().map(this::toEmployeeDTO).collect(toList());
    }

    private Customer toCustomer(CustomerDTO customerDTO) {
        List<Pet> pets = new ArrayList<>();
        List<Long> petIds = customerDTO.getPetIds();
        if (petIds != null) {
            for (Long petId : petIds) {
                Pet pet = petRepository.getPetById(petId);
                if (pet == null) {
                    pet = new Pet();
                    pet.setPet_id(petId);
                }
                pets.add(pet);
            }
        }
        return new Customer(customerDTO.getId(), customerDTO.getName(), customerDTO.getPhoneNumber(), customerDTO.getNotes(), pets);
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        if (customer == null) {
            return new CustomerDTO();
        }
        List<Long> petIds = customer.getPets().stream().map(Pet::getPet_id).collect(toList());
        return new CustomerDTO(customer.getUser_id(), customer.getName(), customer.getPhoneNumber(), customer.getNotes(), petIds);
    }

    private Employee toEmployee(EmployeeDTO employeeDTO) {
        return new Employee(employeeDTO.getId(), employeeDTO.getName(), employeeDTO.getSkills(), employeeDTO.getDaysAvailable());
    }

    private EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) {
            return new EmployeeDTO();
        }
        return new EmployeeDTO(employee.getUser_id(), employee.getName(), employee.getSkills(), employee.getDaysAvailable());
    }
}
