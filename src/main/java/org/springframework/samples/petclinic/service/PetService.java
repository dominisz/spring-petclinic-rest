package org.springframework.samples.petclinic.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;

import java.util.Collection;

public interface PetService {

    Pet findPetById(int id) throws DataAccessException;
    Collection<Pet> findAllPets() throws DataAccessException;
    void savePet(Pet pet) throws DataAccessException;
    void deletePet(Pet pet) throws DataAccessException;

}
