package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {
    @Autowired
    PetRepository petRepository;
    @Autowired
    CustomerRepository customerRepository;

    public PetDTO savePet(PetDTO petDTO) {
        Customer c = null;
        if (petDTO.getOwnerId() != null) {
            c = customerRepository.findCustomerById(petDTO.getOwnerId());
        }
        Pet pet = new Pet(petDTO.getType(), petDTO.getName(), c, petDTO.getBirthDate(), petDTO.getNotes());
        return toPetDTO(petRepository.savePet(pet));
    }

    public PetDTO getPet(long petId) {
        Pet pet = petRepository.getPetById(petId);
        return toPetDTO(pet);
    }

    public List<PetDTO> getPets(){
        List<Pet> pets = petRepository.getAllPets();
        return pets.stream().map(this::toPetDTO).collect(Collectors.toList());
    }

    public List<PetDTO> getPetsByOwner(long ownerId) {
        Customer owner = customerRepository.findCustomerById(ownerId);
        if (owner == null) {
            return null;
        }
        return owner.getPets().stream().map(this::toPetDTO).collect(Collectors.toList());
    }

    private Pet toPet(PetDTO petDTO) {
        Customer c = customerRepository.findCustomerById(petDTO.getOwnerId());
        return new Pet(petDTO.getType(), petDTO.getName(), c, petDTO.getBirthDate(), petDTO.getNotes());
    }

    private PetDTO toPetDTO(Pet pet) {
        if (pet == null) {
            return new PetDTO();
        }
        Customer owner = null;
        if (pet.getOwner() != null) {
            owner = customerRepository.findCustomerById(pet.getOwner().getUser_id());
        }
        return new PetDTO(pet.getPet_id(), pet.getType(), pet.getName(), owner == null? null : owner.getUser_id(), pet.getBirthDate(), pet.getNotes());
    }
}
