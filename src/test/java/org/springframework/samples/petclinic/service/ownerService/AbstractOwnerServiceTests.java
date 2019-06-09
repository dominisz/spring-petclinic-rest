package org.springframework.samples.petclinic.service.ownerService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractOwnerServiceTests {

    @Autowired
    protected OwnerService ownerService;

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
        Owner owner = this.ownerService.findOwnerById(1);
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
        Owner owner = this.ownerService.findOwnerById(1);
        String oldLastName = owner.getLastName();
        String newLastName = oldLastName + "X";

        owner.setLastName(newLastName);
        this.ownerService.saveOwner(owner);

        // retrieving new name from database
        owner = this.ownerService.findOwnerById(1);
        assertThat(owner.getLastName()).isEqualTo(newLastName);
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
        Owner owner = this.ownerService.findOwnerById(1);
        this.ownerService.deleteOwner(owner);
        try {
            owner = this.ownerService.findOwnerById(1);
        } catch (Exception e) {
            owner = null;
        }
        assertThat(owner).isNull();
    }
}
