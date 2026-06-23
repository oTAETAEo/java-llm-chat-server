package com.example.aisocket.week1.benchmark.primitive;

import java.io.IOException;
import java.util.Random;

public class PrimitiveEmbeddingMemoryTest {

    private static final int VECTOR_COUNT = 100_000;
    private static final int DIMENSION = 768;

    private static double[][] embeddings;

    public static void main(String[] args) throws IOException {
        System.out.println("double[][] primitive 임베딩 메모리 테스트");
        System.out.println("VECTOR_COUNT = " + VECTOR_COUNT);
        System.out.println("DIMENSION = " + DIMENSION);

        waitForVisualVm("Before allocation");

        embeddings = createPrimitiveEmbeddings();

        waitForVisualVm("After allocation");

        System.out.println("Done. embeddings length = " + embeddings.length);
    }

    private static double[][] createPrimitiveEmbeddings() {
        Random random = new Random(42);
        double[][] result = new double[VECTOR_COUNT][];

        for (int i = 0; i < VECTOR_COUNT; i++) {
            double[] vector = new double[DIMENSION];

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