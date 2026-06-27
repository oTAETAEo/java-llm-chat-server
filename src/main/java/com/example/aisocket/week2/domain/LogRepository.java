package com.example.aisocket.week2.domain;

import java.io.IOException;

public interface LogRepository {

    void write(String logMessage) throws IOException;

    void flush() throws IOException;

    void close();

}
