package org.springframework.samples.petclinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OwnerNotFoundException extends RuntimeException {

    public OwnerNotFoundException(int id) {
        super("Owner with id " + id + " not found");
    }

}
