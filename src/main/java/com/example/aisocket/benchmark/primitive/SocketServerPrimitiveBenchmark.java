package com.example.aisocket.benchmark.primitive;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketServerPrimitiveBenchmark {

    private static final int PORT = 9991;
    private static final int VECTOR_COUNT = 100_000;
    private static final int DIMENSION = 768;

    public static void main(String[] args) throws IOException {

        // 1. 데이터 미리 채우기
        double[][] primitiveData = new double[VECTOR_COUNT][DIMENSION];
        for (int i = 0; i < VECTOR_COUNT; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                primitiveData[i][j] = i + j;
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Primitive 서버] 포트 " + PORT + " 대기 중...");
            try (Socket client = serverSocket.accept();
                 DataOutputStream out = new DataOutputStream(client.getOutputStream())) {

                System.out.println("[Primitive 서버] 전송 시작");

                ByteBuffer buffer = ByteBuffer.allocate(DIMENSION * 8);
                sendPrimitiveData(out, primitiveData, buffer);

                System.out.println("[Primitive 서버] 전송 완료.");
            }
        }
    }

    private static void sendPrimitiveData(DataOutputStream out, double[][] data, ByteBuffer buffer) throws IOException {
        for (int i = 0; i < VECTOR_COUNT; i++) {
            buffer.clear();
            for (int j = 0; j < DIMENSION; j++) {
                buffer.putDouble(data[i][j]);
            }
            out.write(buffer.array());
        }
        out.flush();
    }
}