package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class PetRepository {
    @PersistenceContext
    EntityManager entityManager;

    public Pet savePet(Pet pet) {
        if (pet.getOwner() == null) {
            return entityManager.merge(pet);
        }
        else {
            Customer owner = pet.getOwner();
            if (owner.hasPet(pet)) {
                return entityManager.merge(pet);
            }
            else {
                pet = entityManager.merge(pet);
                owner.getPets().add(pet);
                entityManager.merge(owner);
                return pet;
            }
        }
    }

    public Pet getPetById(Long petId) {
        return entityManager.find(Pet.class, petId);
    }

    public List<Pet> getAllPets() {
        TypedQuery<Pet> query = entityManager.createNamedQuery("Employee.findAllPets", Pet.class);
        return query.getResultList();
    }

    public List<Schedule> getScheduleByPetId(Long petId) {
        TypedQuery<Schedule> query = entityManager.createQuery("select ps from Pet p join p.schedules ps where p.pet_id = :petId", Schedule.class);
        query.setParameter("petId", petId);
        return query.getResultList();
    }
}
