package com.github.allisson95.codeflix.infrastructure.video.persistence;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.video.VideoPreview;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {

    @Query("""
            SELECT new com.github.allisson95.codeflix.domain.video.VideoPreview(
                v.id as id,
                v.title as title,
                v.description as description,
                v.createdAt as createdAt,
                v.updatedAt as updatedAt
            )
            FROM Video v
                JOIN v.castMembers members
                JOIN v.categories categories
                JOIN v.genres genres
            WHERE
                ( :terms IS NULL OR UPPER(v.title) LIKE :terms )
                AND
                ( :castMembers IS NULL OR members.id.castMemberId IN :castMembers )
                AND
                ( :categories IS NULL OR categories.id.categoryId IN :categories )
                AND
                ( :genres IS NULL OR genres.id.genreId IN :genres )
            """)
    Page<VideoPreview> findAll(
            @Param("terms") String terms,
            @Param("castMembers") Set<String> castMembers,
            @Param("categories") Set<String> categories,
            @Param("genres") Set<String> genres,
            PageRequest page);
}
