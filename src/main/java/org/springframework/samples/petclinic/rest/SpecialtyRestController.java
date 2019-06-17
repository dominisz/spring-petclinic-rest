/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.samples.petclinic.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.service.SpecialtyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Vitaliy Fedoriv
 *
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/specialties")
public class SpecialtyRestController {

	@Autowired
	private SpecialtyService specialtyService;

    @PreAuthorize( "hasRole(@roles.VET_ADMIN)" )
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Specialty>> getAllSpecialtys(){

    Collection<Specialty> specialties = specialtyService.findAllSpecialties();

    return Optional.ofNullable(specialties)
            .map(specs -> new ResponseEntity<>(specs, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

    @PreAuthorize( "hasRole(@roles.VET_ADMIN)" )
	@RequestMapping(value = "/{specialtyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> getSpecialty(@PathVariable("specialtyId") int specialtyId){
        return specialtyService.findSpecialtyById(specialtyId)
            .map(specialty -> new ResponseEntity<>(specialty, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize( "hasRole(@roles.VET_ADMIN)" )
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Specialty> addSpecialty(@RequestBody @Valid Specialty specialty, BindingResult bindingResult, UriComponentsBuilder ucBuilder){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
        if (validate(specialty, bindingResult, errors, headers))
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        specialtyService.saveSpecialty(specialty);
		headers.setLocation(ucBuilder.path("/api/specialtys/{id}").buildAndExpand(specialty.getId()).toUri());
		return new ResponseEntity<>(specialty, headers, HttpStatus.CREATED);
	}

    private boolean validate(@Valid @RequestBody Specialty specialty, BindingResult bindingResult, BindingErrorsResponse errors, HttpHeaders headers) {
        if (bindingResult.hasErrors() || (specialty == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return true;
        }
        return false;
    }

    @PreAuthorize( "hasRole(@roles.VET_ADMIN)" )
	@RequestMapping(value = "/{specialtyId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public HttpEntity<?> updateSpecialty(@PathVariable("specialtyId") int specialtyId, @RequestBody @Valid Specialty specialty, BindingResult bindingResult){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
        if (validate(specialty, bindingResult, errors, headers))
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
    return Optional.ofNullable(specialtyService.updateSpeciality(specialtyId, specialty))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
	}

    @PreAuthorize( "hasRole(@roles.VET_ADMIN)" )
	@RequestMapping(value = "/{specialtyId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@Transactional
	public ResponseEntity<Void> deleteSpecialty(@PathVariable("specialtyId") int specialtyId){
        try {
            specialtyService.deleteSpecialty(specialtyId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
	}

}
