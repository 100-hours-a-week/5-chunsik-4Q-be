package org.chunsik.pq.gallery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGallerySortException extends RuntimeException {

    public InvalidGallerySortException(String sort) {
        super("Invalid sort value: " + sort);
    }
}
