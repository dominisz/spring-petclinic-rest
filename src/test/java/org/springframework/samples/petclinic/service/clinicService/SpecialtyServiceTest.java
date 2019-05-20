package org.springframework.samples.petclinic.service.clinicService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.service.SpecialtyService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("jdbc, hsqldb")
public class SpecialtyServiceTest {

    @Autowired
    protected SpecialtyService specialtyService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFindSpecialtyById(){
        Specialty specialty = specialtyService.findSpecialtyById(1).get();
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    public void shouldFindAllSpecialtys(){
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        Specialty specialty1 = EntityUtils.getById(specialties, Specialty.class, 1);
        assertThat(specialty1.getName()).isEqualTo("radiology");
        Specialty specialty3 = EntityUtils.getById(specialties, Specialty.class, 3);
        assertThat(specialty3.getName()).isEqualTo("dentistry");
    }

    @Test
    @Transactional
    public void shouldInsertSpecialty() {
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        int found = specialties.size();

        Specialty specialty = new Specialty();
        specialty.setName("dermatologist");

        this.specialtyService.saveSpecialty(specialty);
        assertThat(specialty.getId().longValue()).isNotEqualTo(0);

        specialties = this.specialtyService.findAllSpecialties();
        assertThat(specialties.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdateSpecialty(){
        Specialty specialty = specialtyService.findSpecialtyById(1).get();
        String oldLastName = specialty.getName();
        String newLastName = oldLastName + "X";
        specialty.setName(newLastName);
        this.specialtyService.saveSpecialty(specialty);
        specialty = specialtyService.findSpecialtyById(1).get();
        assertThat(specialty.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    public void shouldDeleteSpecialty(){
        Specialty specialty = specialtyService.findSpecialtyById(1).get();
        specialtyService.deleteSpecialty(specialty.getId());
        try {
            specialty = specialtyService.findSpecialtyById(1).get();
        } catch (Exception e) {
            specialty = null;
        }
        assertThat(specialty).isNull();
    }
}
