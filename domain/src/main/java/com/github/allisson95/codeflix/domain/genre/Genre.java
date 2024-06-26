package com.github.allisson95.codeflix.domain.genre;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.allisson95.codeflix.domain.AggregateRoot;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.utils.InstantUtils;
import com.github.allisson95.codeflix.domain.validation.ValidationHandler;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(
            final GenreID anId,
            final String aName,
            final boolean isActive,
            final List<CategoryID> aCategories,
            final Instant aCreationDate,
            final Instant anUpdateDate,
            final Instant aDeleteDate) {
        super(anId);
        this.name = aName;
        this.active = isActive;
        this.categories = aCategories;
        this.createdAt = Objects.requireNonNull(aCreationDate, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(anUpdateDate, "'updatedAt' should not be null");
        this.deletedAt = aDeleteDate;

        this.selfValidate();
    }

    public static Genre newGenre(final String aName, final boolean isActive) {
        final var anId = GenreID.unique();
        final var now = InstantUtils.now();
        final var aDeletedAt = isActive ? null : now;

        return new Genre(anId, aName, isActive, new ArrayList<>(), now, now, aDeletedAt);
    }

    public static Genre with(
            final GenreID anId,
            final String aName,
            final boolean isActive,
            final List<CategoryID> aCategories,
            final Instant aCreationDate,
            final Instant anUpdateDate,
            final Instant aDeleteDate) {
        return new Genre(anId, aName, isActive, aCategories, aCreationDate, anUpdateDate, aDeleteDate);
    }

    public static Genre with(final Genre aGenre) {
        return with(
                aGenre.getId(),
                aGenre.getName(),
                aGenre.isActive(),
                new ArrayList<>(aGenre.getCategories()),
                aGenre.getCreatedAt(),
                aGenre.getUpdatedAt(),
                aGenre.getDeletedAt());
    }

    public Genre activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Genre deactivate() {
        if (this.getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }

        this.active = false;
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Genre update(final String aName, final boolean isActive, final List<CategoryID> categories) {
        if (isActive) {
            activate();
        } else {
            deactivate();
        }

        if (categories == null) {
            this.categories = new ArrayList<>();
        } else {
            this.categories = new ArrayList<>(categories);
        }

        this.name = aName;
        this.updatedAt = InstantUtils.now();

        this.selfValidate();

        return this;
    }

    public Genre addCategory(final CategoryID aCategoryID) {
        if (aCategoryID == null) {
            return this;
        }

        this.categories.add(aCategoryID);
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Genre addCategories(final List<CategoryID> categories) {
        if (categories == null || categories.isEmpty()) {
            return this;
        }

        this.categories.addAll(categories);
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Genre removeCategory(final CategoryID aCategoryID) {
        if (aCategoryID == null) {
            return this;
        }

        this.categories.remove(aCategoryID);
        this.updatedAt = InstantUtils.now();

        return this;
    }

    @Override
    public void validate(final ValidationHandler aHandler) {
        new GenreValidator(this, aHandler).validate();
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    private void selfValidate() {
        final var notification = Notification.create();

        validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Failed to create Aggregate Genre", notification);
        }
    }

}
