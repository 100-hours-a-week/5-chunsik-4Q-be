package org.chunsik.pq.gallery.model;

import org.chunsik.pq.gallery.exception.InvalidGallerySortException;

public enum GallerySort {
    POPULAR, 
    LATEST;

    public static GallerySort fromValue(String value) {
        try {
            return GallerySort.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidGallerySortException(value);
        }
    }
}
