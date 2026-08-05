package com.example.aisocket.project.application.internal.fit;

import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.WorkOutType;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.RecordMesgListener;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;
import com.garmin.fit.Sport;
import com.garmin.fit.SportMesg;
import com.garmin.fit.SportMesgListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GarminFitFileParser implements FitFileParser {

    private static final long MAX_FIT_FILE_SIZE = 10L * 1024L * 1024L;
    private static final int MAX_PREVIEW_SAMPLES = 1200;
    private static final String FIT_EXTENSION = ".fit";
    private static final byte[] FIT_SIGNATURE = {'.', 'F', 'I', 'T'};

    @Override
    public FitParseResult parse(MultipartFile file) {
        validateFile(file);

        byte[] bytes = readBytes(file);
        validateFitSignature(bytes);

        FitSummaryCollector collector = new FitSummaryCollector();
        MesgBroadcaster broadcaster = new MesgBroadcaster();
        broadcaster.addListener((SportMesgListener) collector::onMesg);
        broadcaster.addListener((SessionMesgListener) collector::onMesg);
        broadcaster.addListener((RecordMesgListener) collector::onMesg);

        try {
            broadcaster.run(new ByteArrayInputStream(bytes));
        } catch (RuntimeException exception) {
            throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
        }

        return collector.toResult();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ProjectException(WorkoutErrorCode.FIT_FILE_EMPTY);
        }
        if (file.getSize() > MAX_FIT_FILE_SIZE) {
            throw new ProjectException(WorkoutErrorCode.FIT_FILE_TOO_LARGE);
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(FIT_EXTENSION)) {
            throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
        }
    }

    private void validateFitSignature(byte[] bytes) {
        if (bytes.length < 12) {
            throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
        }
        for (int index = 0; index < FIT_SIGNATURE.length; index++) {
            if (bytes[8 + index] != FIT_SIGNATURE[index]) {
                throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
            }
        }
    }

    private static final class FitSummaryCollector {

        private Sport sport;
        private SessionMesg session;
        private Instant firstRecordTime;
        private Instant lastRecordTime;
        private Float maxRecordDistance;
        private Float maxRecordAltitude;
        private Float maxRecordSpeed;
        private Integer maxRecordPower;
        private final List<RecordMesg> records = new ArrayList<>();

        void onMesg(SportMesg message) {
            if (message.getSport() != null) {
                sport = message.getSport();
            }
        }

        void onMesg(SessionMesg message) {
            session = message;
            if (message.getSport() != null) {
                sport = message.getSport();
            }
        }

        void onMesg(RecordMesg message) {
            records.add(message);
            if (message.getTimestamp() != null) {
                Instant timestamp = message.getTimestamp().getInstant();
                if (firstRecordTime == null || timestamp.isBefore(firstRecordTime)) {
                    firstRecordTime = timestamp;
                }
                if (lastRecordTime == null || timestamp.isAfter(lastRecordTime)) {
                    lastRecordTime = timestamp;
                }
            }
            maxRecordDistance = max(maxRecordDistance, message.getDistance());
            maxRecordAltitude = max(maxRecordAltitude, message.getAltitude());
            maxRecordSpeed = max(maxRecordSpeed, message.getSpeed());
            maxRecordPower = max(maxRecordPower, message.getPower());
        }

        FitParseResult toResult() {
            if (session == null) {
                throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
            }

            WorkOutType workOutType = toWorkOutType(sport);
            LocalDateTime startedAt = toLocalDateTime(session.getStartTime() == null ? firstRecordTime : session.getStartTime().getInstant());
            Integer movingTime = toSeconds(firstNonNull(session.getTotalMovingTime(), session.getTotalTimerTime(), session.getTotalElapsedTime()));
            LocalDateTime endedAt = startedAt == null ? null : startedAt.plusSeconds(movingTime == null ? elapsedRecordSeconds() : movingTime);

            Double distance = metersToKilometers(firstNonNull(session.getTotalDistance(), maxRecordDistance));
            Double avgPace = workOutType == WorkOutType.RUNNING && distance != null && movingTime != null && distance > 0
                    ? movingTime / distance
                    : null;

            return new FitParseResult(
                    workOutType,
                    startedAt,
                    endedAt,
                    distance,
                    toDouble(session.getTotalAscent()),
                    toDouble(firstNonNull(session.getEnhancedMaxAltitude(), session.getMaxAltitude(), maxRecordAltitude)),
                    movingTime,
                    toDouble(session.getTotalCalories()),
                    toDouble(firstNonNull(session.getAvgCadence(), session.getAvgRunningCadence())),
                    toDouble(firstNonNull(session.getMaxCadence(), session.getMaxRunningCadence())),
                    toDouble(session.getMaxHeartRate()),
                    toDouble(session.getAvgHeartRate()),
                    workOutType == WorkOutType.CYCLING ? metersPerSecondToKilometersPerHour(firstNonNull(session.getEnhancedAvgSpeed(), session.getAvgSpeed())) : null,
                    workOutType == WorkOutType.CYCLING ? metersPerSecondToKilometersPerHour(firstNonNull(session.getEnhancedMaxSpeed(), session.getMaxSpeed(), maxRecordSpeed)) : null,
                    workOutType == WorkOutType.CYCLING ? toDouble(session.getAvgPower()) : null,
                    workOutType == WorkOutType.CYCLING ? toDouble(firstNonNull(session.getMaxPower(), maxRecordPower)) : null,
                    null,
                    avgPace,
                    null,
                    workOutType == WorkOutType.RUNNING ? toInteger(session.getTotalStrides()) : null,
                    toSamples()
            );
        }

        private WorkOutType toWorkOutType(Sport fitSport) {
            if (fitSport == Sport.RUNNING) {
                return WorkOutType.RUNNING;
            }
            if (fitSport == Sport.CYCLING) {
                return WorkOutType.CYCLING;
            }
            throw new ProjectException(WorkoutErrorCode.UNSUPPORTED_FIT_SPORT);
        }

        private int elapsedRecordSeconds() {
            if (firstRecordTime == null || lastRecordTime == null) {
                throw new ProjectException(WorkoutErrorCode.INVALID_FIT_FILE);
            }
            return Math.max(1, (int) (lastRecordTime.getEpochSecond() - firstRecordTime.getEpochSecond()));
        }

        private List<FitParseResult.FitSensorSample> toSamples() {
            if (records.isEmpty()) {
                return List.of();
            }

            int stride = Math.max(1, (int) Math.ceil(records.size() / (double) MAX_PREVIEW_SAMPLES));
            List<FitParseResult.FitSensorSample> samples = new ArrayList<>();

            for (int index = 0; index < records.size(); index += stride) {
                samples.add(toSample(records.get(index)));
            }

            FitParseResult.FitSensorSample lastSample = toSample(records.get(records.size() - 1));
            if (samples.isEmpty() || !samples.get(samples.size() - 1).equals(lastSample)) {
                samples.add(lastSample);
            }

            return List.copyOf(samples);
        }

        private FitParseResult.FitSensorSample toSample(RecordMesg record) {
            return new FitParseResult.FitSensorSample(
                    elapsedSeconds(record),
                    metersToKilometers(record.getDistance()),
                    semicirclesToDegrees(record.getPositionLat()),
                    semicirclesToDegrees(record.getPositionLong()),
                    toDouble(firstNonNull(record.getEnhancedAltitude(), record.getAltitude())),
                    toInteger(record.getHeartRate()),
                    toCadence(record),
                    metersPerSecondToKilometersPerHour(firstNonNull(record.getEnhancedSpeed(), record.getSpeed())),
                    record.getPower()
            );
        }

        private Integer toCadence(RecordMesg record) {
            if (record.getCadence256() != null) {
                return Math.round(record.getCadence256());
            }
            return toInteger(record.getCadence());
        }

        private Integer elapsedSeconds(RecordMesg record) {
            if (firstRecordTime == null || record.getTimestamp() == null) {
                return null;
            }
            long seconds = record.getTimestamp().getInstant().getEpochSecond() - firstRecordTime.getEpochSecond();
            return Math.max(0, Math.toIntExact(seconds));
        }

        private static LocalDateTime toLocalDateTime(Instant instant) {
            return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        }

        private static Integer toSeconds(Float seconds) {
            return seconds == null ? null : Math.max(1, Math.round(seconds));
        }

        private static Double metersToKilometers(Float meters) {
            return meters == null ? null : meters / 1000.0;
        }

        private static Double metersPerSecondToKilometersPerHour(Float speed) {
            return speed == null ? null : speed.doubleValue() * 3.6;
        }

        private static Double semicirclesToDegrees(Integer semicircles) {
            return semicircles == null ? null : semicircles * (180.0 / 2147483648.0);
        }

        private static Double toDouble(Number number) {
            return number == null ? null : number.doubleValue();
        }

        private static Integer toInteger(Long value) {
            return value == null ? null : Math.toIntExact(value);
        }

        private static Integer toInteger(Short value) {
            return value == null ? null : value.intValue();
        }

        private static Integer toInteger(Float value) {
            return value == null ? null : Math.round(value);
        }

        @SafeVarargs
        private static <T> T firstNonNull(T... values) {
            for (T value : values) {
                if (value != null) {
                    return value;
                }
            }
            return null;
        }

        private static Float max(Float current, Float candidate) {
            if (candidate == null) {
                return current;
            }
            return current == null ? candidate : Math.max(current, candidate);
        }

        private static Integer max(Integer current, Integer candidate) {
            if (candidate == null) {
                return current;
            }
            return current == null ? candidate : Math.max(current, candidate);
        }
    }
}
