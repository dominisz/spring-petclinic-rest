package org.springframework.samples.petclinic.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Specialty;

import java.util.Collection;
import java.util.Optional;

public interface SpecialtyService {
    Optional<Specialty> findSpecialtyById(int specialtyId);
    Collection<Specialty> findAllSpecialties() throws DataAccessException;
    void saveSpecialty(Specialty specialty) throws DataAccessException;
    void deleteSpecialty(int specialtyId) throws DataAccessException;

    Specialty updateSpeciality(int specialtyId, Specialty specialty);
}
