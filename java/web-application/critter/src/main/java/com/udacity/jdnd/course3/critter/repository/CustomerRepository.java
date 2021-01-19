package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class CustomerRepository {
    @PersistenceContext
    EntityManager entityManager;

    public Customer save(Customer customer) {
        return entityManager.merge(customer);
    }

    public List<Customer> getAllCustomer() {
        TypedQuery<Customer> query = entityManager.createQuery("select c from Customer c", Customer.class);
        return query.getResultList();
    }

    public Customer findCustomerById(Long customerId) {
        return entityManager.find(Customer.class, customerId);
    }

    public List<Schedule> findCustomerScheduleById(Long customerId) {
        TypedQuery<Schedule> query = entityManager.createQuery("select ps from Pet p join p.schedules ps where p.owner.user_id = :customerId", Schedule.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }
}