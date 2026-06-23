package com.example.aisocket.week1.benchmark.wrapper;

import java.io.IOException;
import java.util.Random;

public class WrapperEmbeddingMemoryTest {

    private static final int VECTOR_COUNT = 100_000;
    private static final int DIMENSION = 768;

    private static Double[][] embeddings;

    public static void main(String[] args) throws IOException {
        System.out.println("Double[][] wrapper 임베딩 메모리 테스트");
        System.out.println("VECTOR_COUNT = " + VECTOR_COUNT);
        System.out.println("DIMENSION = " + DIMENSION);

        waitForVisualVm("Before allocation");

        embeddings = createWrapperEmbeddings();

        waitForVisualVm("After allocation");

        System.out.println("Done. embeddings length = " + embeddings.length);
    }

    private static Double[][] createWrapperEmbeddings() {
        Random random = new Random(42);
        Double[][] result = new Double[VECTOR_COUNT][];

        for (int i = 0; i < VECTOR_COUNT; i++) {
            Double[] vector = new Double[DIMENSION];

            for (int j = 0; j < DIMENSION; j++) {
                vector[j] = random.nextDouble();
            }

            result[i] = vector;
        }

        return result;
    }

    private static void waitForVisualVm(String phase) throws IOException {
        System.out.println();
        System.out.println("[" + phase + "]");
        System.out.println("VisualVM에서 힙 상태를 확인한 뒤 Enter를 누르세요.");
        System.in.read();
    }
}