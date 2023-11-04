package com.github.allisson95.codeflix.infrastructure.castmember;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;
import com.github.allisson95.codeflix.infrastructure.utils.SpecificationUtils;

@Component
public class CastMemberMySQLGateway implements CastMemberGateway {

    private final CastMemberRepository castMemberRepository;

    public CastMemberMySQLGateway(final CastMemberRepository castMemberRepository) {
        this.castMemberRepository = Objects.requireNonNull(castMemberRepository);
    }

    @Override
    public CastMember create(final CastMember aCastMember) {
        return save(aCastMember);
    }

    @Override
    public void deleteById(final CastMemberID anId) {
        final var anIdValue = anId.getValue();
        if (this.castMemberRepository.existsById(anIdValue)) {
            this.castMemberRepository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<CastMember> findById(final CastMemberID anId) {
        final var anIdValue = anId.getValue();
        return this.castMemberRepository.findById(anIdValue)
                .map(CastMemberJpaEntity::toAggregate);
    }

    @Override
    public Pagination<CastMember> findAll(final SearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort()));

        final var spec = Optional.ofNullable(aQuery.terms())
                .filter(terms -> !terms.isBlank())
                .map(terms -> SpecificationUtils.<CastMemberJpaEntity>like("name", terms))
                .orElse(null);

        final var pageResult = this.castMemberRepository.findAll(Specification.where(spec), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CastMemberJpaEntity::toAggregate).toList());
    }

    @Override
    public CastMember update(final CastMember aCastMember) {
        return save(aCastMember);
    }

    @Override
    public List<CastMemberID> existsByIds(final Iterable<CastMemberID> ids) {
        throw new UnsupportedOperationException("Unimplemented method 'existsByIds'");
    }

    private CastMember save(final CastMember aCastMember) {
        return this.castMemberRepository.save(CastMemberJpaEntity.from(aCastMember)).toAggregate();
    }

}
