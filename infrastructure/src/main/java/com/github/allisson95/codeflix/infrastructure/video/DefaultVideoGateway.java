package com.github.allisson95.codeflix.infrastructure.video;

import static com.github.allisson95.codeflix.domain.utils.CollectionUtils.mapTo;
import static com.github.allisson95.codeflix.domain.utils.CollectionUtils.nullIfEmpty;
import static com.github.allisson95.codeflix.infrastructure.utils.SqlUtils.like;
import static com.github.allisson95.codeflix.infrastructure.utils.SqlUtils.upper;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoPreview;
import com.github.allisson95.codeflix.domain.video.VideoSearchQuery;
import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.github.allisson95.codeflix.infrastructure.services.EventService;
import com.github.allisson95.codeflix.infrastructure.video.persistence.VideoJpaEntity;
import com.github.allisson95.codeflix.infrastructure.video.persistence.VideoRepository;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final VideoRepository videoRepository;
    private final EventService eventService;

    public DefaultVideoGateway(
            final VideoRepository videoRepository,
            @VideoCreatedQueue final EventService eventService) {
        this.videoRepository = Objects.requireNonNull(videoRepository);
        this.eventService = Objects.requireNonNull(eventService);
    }

    @Transactional
    @Override
    public Video create(final Video aVideo) {
        return save(aVideo);
    }

    @Override
    public void deleteById(final VideoID anId) {
        final var aVideoId = anId.getValue();
        if (this.videoRepository.existsById(aVideoId)) {
            this.videoRepository.deleteById(aVideoId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Video> findById(VideoID anId) {
        return this.videoRepository.findById(anId.getValue())
                .map(VideoJpaEntity::toAggregate);
    }

    @Override
    public Pagination<VideoPreview> findAll(VideoSearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort()));

        final var actualPage = this.videoRepository.findAll(
                upper(like(aQuery.terms())),
                nullIfEmpty(mapTo(aQuery.castMembers(), Identifier::getValue)),
                nullIfEmpty(mapTo(aQuery.categories(), Identifier::getValue)),
                nullIfEmpty(mapTo(aQuery.genres(), Identifier::getValue)),
                page);

        return new Pagination<>(
                actualPage.getNumber(),
                actualPage.getSize(),
                actualPage.getTotalElements(),
                actualPage.toList());
    }

    @Transactional
    @Override
    public Video update(Video aVideo) {
        return save(aVideo);
    }

    private Video save(final Video aVideo) {
        final var aggregate = this.videoRepository.save(VideoJpaEntity.from(aVideo))
                .toAggregate();

        aggregate.publishDomainEvents(this.eventService::send);

        return aggregate;
    }

}
