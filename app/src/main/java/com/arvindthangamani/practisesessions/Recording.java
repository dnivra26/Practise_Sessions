package com.arvindthangamani.practisesessions;

public class Recording {
    Long startTimestamp;
    Long durationInMs;
    String name;
    String filePath;

    public Recording(Long startTimestamp, Long durationInMs, String name, String filePath) {
        this.startTimestamp = startTimestamp;
        this.durationInMs = durationInMs;
        this.name = name;
        this.filePath = filePath;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(Long durationInMs) {
        this.durationInMs = durationInMs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
