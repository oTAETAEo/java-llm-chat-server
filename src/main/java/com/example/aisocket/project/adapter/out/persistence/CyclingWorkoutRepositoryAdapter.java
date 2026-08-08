package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.QCyclingWorkout;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CyclingWorkoutRepositoryAdapter implements CyclingWorkoutRepository {

    private static final QCyclingWorkout cyclingWorkout = QCyclingWorkout.cyclingWorkout;

    private final CyclingWorkoutJpaRepository repository;

    private final JPAQueryFactory queryFactory;

    @Override
    public Long save(Member member, CyclingWorkout workout, AthleteTier tier) {
        CyclingWorkout savedWorkout = repository.save(workout);

        return savedWorkout.getId();
    }

    @Override
    public Optional<CyclingWorkout> findByIdAndMemberId(Long workoutId, Long memberId) {
        return repository.findByIdAndMemberId(workoutId, memberId);
    }

    @Override
    public Optional<CyclingWorkout> findDuplicate(Long memberId, CyclingWorkout workout) {
        return repository.findFirstByMemberIdAndStartedAtAndEndedAtAndDistanceAndMovingTime(
                memberId,
                workout.getStartedAt(),
                workout.getEndedAt(),
                workout.getDistance(),
                workout.getMovingTime()
        );
    }

    @Override
    public List<CyclingWorkout> findDashboardWorkouts(Long memberId, LocalDateTime from, LocalDateTime toExclusive) {
        return queryFactory
                .selectFrom(cyclingWorkout)
                .where(
                        memberIdEq(memberId),
                        startedAtGoe(from),
                        startedAtLt(toExclusive)
                )
                .orderBy(cyclingWorkout.startedAt.desc(), cyclingWorkout.id.desc())
                .fetch();
    }

    @Override
    public List<CyclingWorkout> findDashboardHistories(
            Long memberId,
            LocalDateTime from,
            LocalDateTime toExclusive,
            LocalDateTime cursorStartedAt,
            Long cursorWorkoutId,
            int size
    ) {
        return queryFactory
                .selectFrom(cyclingWorkout)
                .where(
                        memberIdEq(memberId),
                        startedAtGoe(from),
                        startedAtLt(toExclusive),
                        beforeCursor(cursorStartedAt, cursorWorkoutId)
                )
                .orderBy(cyclingWorkout.startedAt.desc(), cyclingWorkout.id.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return cyclingWorkout.member.id.eq(memberId);
    }

    private BooleanExpression startedAtGoe(LocalDateTime from) {
        return from == null ? null : cyclingWorkout.startedAt.goe(from);
    }

    private BooleanExpression startedAtLt(LocalDateTime toExclusive) {
        return toExclusive == null ? null : cyclingWorkout.startedAt.lt(toExclusive);
    }

    private BooleanExpression beforeCursor(LocalDateTime cursorStartedAt, Long cursorWorkoutId) {
        if (cursorStartedAt == null || cursorWorkoutId == null) {
            return null;
        }
        return cyclingWorkout.startedAt.lt(cursorStartedAt)
                .or(cyclingWorkout.startedAt.eq(cursorStartedAt)
                        .and(cyclingWorkout.id.lt(cursorWorkoutId)));
    }
}
