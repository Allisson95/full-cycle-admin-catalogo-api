package com.github.allisson95.codeflix.infrastructure.genre.persistence;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GenreCategoryID implements Serializable {

    @Column(name = "genre_id", nullable = false)
    private String genreId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    public GenreCategoryID() {
        super();
    }

    private GenreCategoryID(
            final String genreId,
            final String categoryId) {
        this.genreId = Objects.requireNonNull(genreId, "'genreId' should not be null");
        this.categoryId = Objects.requireNonNull(categoryId, "'categoryId' should not be null");
    }

    public static GenreCategoryID from(final String aGenreId, final String aCategoryId) {
        return new GenreCategoryID(aGenreId, aCategoryId);
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGenreId(), getCategoryId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GenreCategoryID other = (GenreCategoryID) obj;
        return Objects.equals(getGenreId(), other.getGenreId())
                && Objects.equals(getCategoryId(), other.getCategoryId());
    }

}
