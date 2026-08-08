package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.QRunningWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RunningWorkoutRepositoryAdapter implements RunningWorkoutRepository {

    private static final QRunningWorkout runningWorkout = QRunningWorkout.runningWorkout;

    private final RunningWorkoutJpaRepository repository;

    private final JPAQueryFactory queryFactory;

    @Override
    public Long save(Member member, RunningWorkout workout, AthleteTier tier) {
        RunningWorkout savedWorkout = repository.save(workout);

        return savedWorkout.getId();
    }

    @Override
    public Optional<RunningWorkout> findByIdAndMemberId(Long workoutId, Long memberId) {
        return repository.findByIdAndMemberId(workoutId, memberId);
    }

    @Override
    public Optional<RunningWorkout> findDuplicate(Long memberId, RunningWorkout workout) {
        return repository.findFirstByMemberIdAndStartedAtAndEndedAtAndDistanceAndMovingTime(
                memberId,
                workout.getStartedAt(),
                workout.getEndedAt(),
                workout.getDistance(),
                workout.getMovingTime()
        );
    }

    @Override
    public List<RunningWorkout> findDashboardWorkouts(Long memberId, LocalDateTime from, LocalDateTime toExclusive) {
        return queryFactory
                .selectFrom(runningWorkout)
                .where(
                        memberIdEq(memberId),
                        startedAtGoe(from),
                        startedAtLt(toExclusive)
                )
                .orderBy(runningWorkout.startedAt.desc(), runningWorkout.id.desc())
                .fetch();
    }

    @Override
    public List<RunningWorkout> findDashboardHistories(
            Long memberId,
            LocalDateTime from,
            LocalDateTime toExclusive,
            LocalDateTime cursorStartedAt,
            Long cursorWorkoutId,
            int size
    ) {
        return queryFactory
                .selectFrom(runningWorkout)
                .where(
                        memberIdEq(memberId),
                        startedAtGoe(from),
                        startedAtLt(toExclusive),
                        beforeCursor(cursorStartedAt, cursorWorkoutId)
                )
                .orderBy(runningWorkout.startedAt.desc(), runningWorkout.id.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return runningWorkout.member.id.eq(memberId);
    }

    private BooleanExpression startedAtGoe(LocalDateTime from) {
        return from == null ? null : runningWorkout.startedAt.goe(from);
    }

    private BooleanExpression startedAtLt(LocalDateTime toExclusive) {
        return toExclusive == null ? null : runningWorkout.startedAt.lt(toExclusive);
    }

    private BooleanExpression beforeCursor(LocalDateTime cursorStartedAt, Long cursorWorkoutId) {
        if (cursorStartedAt == null || cursorWorkoutId == null) {
            return null;
        }
        return runningWorkout.startedAt.lt(cursorStartedAt)
                .or(runningWorkout.startedAt.eq(cursorStartedAt)
                        .and(runningWorkout.id.lt(cursorWorkoutId)));
    }
}
