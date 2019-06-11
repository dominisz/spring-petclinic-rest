package org.springframework.samples.petclinic.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;

import java.util.Collection;
import java.util.Optional;

public interface OwnerService {

    Optional<Owner> findOwnerById(int id) throws DataAccessException;
    Collection<Owner> findAllOwners() throws DataAccessException;
    void saveOwner(Owner owner) throws DataAccessException;
    void deleteOwner(int ownerId) throws DataAccessException;
    Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;
    Owner updateOwner(int ownerId, Owner owner);

}
