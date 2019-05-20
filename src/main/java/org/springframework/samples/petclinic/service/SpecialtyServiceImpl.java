package org.springframework.samples.petclinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private SpecialtyRepository specialtyRepository;

    @Autowired
    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public Optional<Specialty> findSpecialtyById(int specialtyId) {
        return Optional.ofNullable(specialtyRepository.findById(specialtyId));
    }

    @Override
    public Collection<Specialty> findAllSpecialties() throws DataAccessException {
        return specialtyRepository.findAll();
    }

    @Override
    public void saveSpecialty(Specialty specialty) throws DataAccessException {
        specialtyRepository.save(specialty);
    }

    @Override
    public void deleteSpecialty(int specialtyId) throws DataAccessException {
        Specialty foundSpecialty = findSpecialtyById(specialtyId)
            .orElseThrow(EntityNotFoundException::new);
        specialtyRepository.delete(foundSpecialty);
    }

    @Override
    public Specialty updateSpeciality(int specialtyId, Specialty specialty) {
          Specialty foundSpecialty = findSpecialtyById(specialtyId)
              .orElseThrow(EntityNotFoundException::new);
          foundSpecialty.setName(specialty.getName());
          return foundSpecialty;
    }
}
