/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service.clinicService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.PetTypeService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> Base class for {@link ClinicService} integration tests. </p> <p> Subclasses should specify Spring context
 * configuration using {@link ContextConfiguration @ContextConfiguration} annotation </p> <p>
 * AbstractclinicServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * AbstractClinicServiceTests#clinicService petService}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public abstract class AbstractClinicServiceTests {

    @Autowired
    protected ClinicService clinicService;

    @Autowired
    protected OwnerService ownerService;

    @Autowired
    protected PetTypeService petTypeService;

    @Autowired
    protected PetService petService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFindOwnersByLastName() {
        Collection<Owner> owners = this.ownerService.findOwnerByLastName("Davis");
        assertThat(owners.size()).isEqualTo(2);

        owners = this.ownerService.findOwnerByLastName("Daviss");
        assertThat(owners.isEmpty()).isTrue();
    }

    @Test
    public void shouldFindSingleOwnerWithPet() {
        Owner owner = this.ownerService.findOwnerById(1).get();
        assertThat(owner.getLastName()).startsWith("Franklin");
        assertThat(owner.getPets().size()).isEqualTo(1);
        assertThat(owner.getPets().get(0).getType()).isNotNull();
        assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
    }

    @Test
    @Transactional
    public void shouldInsertOwner() {
        Collection<Owner> owners = this.ownerService.findOwnerByLastName("Schultz");
        int found = owners.size();

        Owner owner = new Owner();
        owner.setFirstName("Sam");
        owner.setLastName("Schultz");
        owner.setAddress("4, Evans Street");
        owner.setCity("Wollongong");
        owner.setTelephone("4444444444");
        this.ownerService.saveOwner(owner);
        assertThat(owner.getId().longValue()).isNotEqualTo(0);

        owners = this.ownerService.findOwnerByLastName("Schultz");
        assertThat(owners.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdateOwner() {
        Owner owner = this.ownerService.findOwnerById(1).get();
        String oldLastName = owner.getLastName();
        String newLastName = oldLastName + "X";

        owner.setLastName(newLastName);
        this.ownerService.saveOwner(owner);

        // retrieving new name from database
        owner = this.ownerService.findOwnerById(1).get();
        assertThat(owner.getLastName()).isEqualTo(newLastName);
    }

    @Test
    public void shouldFindPetWithCorrectId() {
        Pet pet7 = this.petService.findPetById(7).get();
        assertThat(pet7.getName()).startsWith("Samantha");
        assertThat(pet7.getOwner().getFirstName()).isEqualTo("Jean");

    }

//    @Test
//    public void shouldFindAllPetTypes() {
//        Collection<PetType> petTypes = this.petService.findPetTypes();
//
//        PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
//        assertThat(petType1.getName()).isEqualTo("cat");
//        PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
//        assertThat(petType4.getName()).isEqualTo("snake");
//    }

    @Test
    @Transactional
    public void shouldInsertPetIntoDatabaseAndGenerateId() {
        Owner owner6 = this.ownerService.findOwnerById(6).get();
        int found = owner6.getPets().size();

        Pet pet = new Pet();
        pet.setName("bowser");
        Collection<PetType> types = this.petTypeService.findPetTypes();
        pet.setType(EntityUtils.getById(types, PetType.class, 2));
        pet.setBirthDate(new Date());
        owner6.addPet(pet);
        assertThat(owner6.getPets().size()).isEqualTo(found + 1);

        this.petService.savePet(pet);
        this.ownerService.saveOwner(owner6);

        owner6 = this.ownerService.findOwnerById(6).get();
        assertThat(owner6.getPets().size()).isEqualTo(found + 1);
        // checks that id has been generated
        assertThat(pet.getId()).isNotNull();
    }

    @Test
    @Transactional
    public void shouldUpdatePetName() throws Exception {
        Pet pet7 = this.petService.findPetById(7).get();
        String oldName = pet7.getName();

        String newName = oldName + "X";
        pet7.setName(newName);
        this.petService.savePet(pet7);

        pet7 = this.petService.findPetById(7).get();
        assertThat(pet7.getName()).isEqualTo(newName);
    }


    @Test
    @Transactional
    public void shouldAddNewVisitForPet() {
        Pet pet7 = this.petService.findPetById(7).get();
        int found = pet7.getVisits().size();
        Visit visit = new Visit();
        pet7.addVisit(visit);
        visit.setDescription("test");
        this.clinicService.saveVisit(visit);
        this.petService.savePet(pet7);

        pet7 = this.petService.findPetById(7).get();
        assertThat(pet7.getVisits().size()).isEqualTo(found + 1);
        assertThat(visit.getId()).isNotNull();
    }

    @Test
       public void shouldFindVisitsByPetId() throws Exception {
        Collection<Visit> visits = this.clinicService.findVisitsByPetId(7);
        assertThat(visits.size()).isEqualTo(2);
        Visit[] visitArr = visits.toArray(new Visit[visits.size()]);
        assertThat(visitArr[0].getPet()).isNotNull();
        assertThat(visitArr[0].getDate()).isNotNull();
        assertThat(visitArr[0].getPet().getId()).isEqualTo(7);
    }

    @Test
    public void shouldFindAllPets(){
        Collection<Pet> pets = this.petService.findAllPets();
        Pet pet1 = EntityUtils.getById(pets, Pet.class, 1);
        assertThat(pet1.getName()).isEqualTo("Leo");
        Pet pet3 = EntityUtils.getById(pets, Pet.class, 3);
        assertThat(pet3.getName()).isEqualTo("Rosy");
    }

    @Test
    @Transactional
    public void shouldDeletePet(){
        Pet pet = this.petService.findPetById(1).get();
        this.petService.deletePet(pet);
        try {
        pet = this.petService.findPetById(1).get();
		} catch (Exception e) {
			pet = null;
		}
        assertThat(pet).isNull();
    }

    @Test
    public void shouldFindVisitDyId(){
    	Visit visit = this.clinicService.findVisitById(1);
    	assertThat(visit.getId()).isEqualTo(1);
    	assertThat(visit.getPet().getName()).isEqualTo("Samantha");
    }

    @Test
    public void shouldFindAllVisits(){
        Collection<Visit> visits = this.clinicService.findAllVisits();
        Visit visit1 = EntityUtils.getById(visits, Visit.class, 1);
        assertThat(visit1.getPet().getName()).isEqualTo("Samantha");
        Visit visit3 = EntityUtils.getById(visits, Visit.class, 3);
        assertThat(visit3.getPet().getName()).isEqualTo("Max");
    }

    @Test
    @Transactional
    public void shouldInsertVisit() {
        Collection<Visit> visits = this.clinicService.findAllVisits();
        int found = visits.size();

        Pet pet = this.petService.findPetById(1).get();

        Visit visit = new Visit();
        visit.setPet(pet);
        visit.setDate(new Date());
        visit.setDescription("new visit");


        this.clinicService.saveVisit(visit);
        assertThat(visit.getId().longValue()).isNotEqualTo(0);

        visits = this.clinicService.findAllVisits();
        assertThat(visits.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdateVisit(){
    	Visit visit = this.clinicService.findVisitById(1);
    	String oldDesc = visit.getDescription();
        String newDesc = oldDesc + "X";
        visit.setDescription(newDesc);
        this.clinicService.saveVisit(visit);
        visit = this.clinicService.findVisitById(1);
        assertThat(visit.getDescription()).isEqualTo(newDesc);
    }

    @Test
    @Transactional
    public void shouldDeleteVisit(){
    	Visit visit = this.clinicService.findVisitById(1);
        this.clinicService.deleteVisit(visit);
        try {
        	visit = this.clinicService.findVisitById(1);
		} catch (Exception e) {
			visit = null;
		}
        assertThat(visit).isNull();
    }


    @Test
    public void shouldFindAllOwners(){
        Collection<Owner> owners = this.ownerService.findAllOwners();
        Owner owner1 = EntityUtils.getById(owners, Owner.class, 1);
        assertThat(owner1.getFirstName()).isEqualTo("George");
        Owner owner3 = EntityUtils.getById(owners, Owner.class, 3);
        assertThat(owner3.getFirstName()).isEqualTo("Eduardo");
    }

    @Test
    @Transactional
    public void shouldDeleteOwner(){
    	Owner owner = this.ownerService.findOwnerById(1).get();
        this.ownerService.deleteOwner(owner);
        try {
        	owner = this.ownerService.findOwnerById(1).get();
		} catch (Exception e) {
			owner = null;
		}
        assertThat(owner).isNull();
    }

    @Test
    public void shouldFindSpecialtyById(){
    	Specialty specialty = this.clinicService.findSpecialtyById(1);
    	assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    public void shouldFindAllSpecialtys(){
        Collection<Specialty> specialties = this.clinicService.findAllSpecialties();
        Specialty specialty1 = EntityUtils.getById(specialties, Specialty.class, 1);
        assertThat(specialty1.getName()).isEqualTo("radiology");
        Specialty specialty3 = EntityUtils.getById(specialties, Specialty.class, 3);
        assertThat(specialty3.getName()).isEqualTo("dentistry");
    }

    @Test
    @Transactional
    public void shouldInsertSpecialty() {
        Collection<Specialty> specialties = this.clinicService.findAllSpecialties();
        int found = specialties.size();

        Specialty specialty = new Specialty();
        specialty.setName("dermatologist");

        this.clinicService.saveSpecialty(specialty);
        assertThat(specialty.getId().longValue()).isNotEqualTo(0);

        specialties = this.clinicService.findAllSpecialties();
        assertThat(specialties.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdateSpecialty(){
    	Specialty specialty = this.clinicService.findSpecialtyById(1);
    	String oldLastName = specialty.getName();
        String newLastName = oldLastName + "X";
        specialty.setName(newLastName);
        this.clinicService.saveSpecialty(specialty);
        specialty = this.clinicService.findSpecialtyById(1);
        assertThat(specialty.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    public void shouldDeleteSpecialty(){
    	Specialty specialty = this.clinicService.findSpecialtyById(1);
        this.clinicService.deleteSpecialty(specialty);
        try {
        	specialty = this.clinicService.findSpecialtyById(1);
		} catch (Exception e) {
			specialty = null;
		}
        assertThat(specialty).isNull();
    }



}
