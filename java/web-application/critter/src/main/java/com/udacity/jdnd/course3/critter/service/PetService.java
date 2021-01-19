package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.utility.DTOUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.utility.DTOUtility.toPetDTO;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private CustomerRepository customerRepository;

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
        return pets.stream().map(DTOUtility::toPetDTO).collect(Collectors.toList());
    }

    public List<PetDTO> getPetsByOwner(long ownerId) {
        Customer owner = customerRepository.findCustomerById(ownerId);
        if (owner == null) {
            return null;
        }
        return owner.getPets().stream().map(DTOUtility::toPetDTO).collect(Collectors.toList());
    }
}
