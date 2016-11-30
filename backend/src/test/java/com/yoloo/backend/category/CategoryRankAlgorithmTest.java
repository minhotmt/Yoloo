package com.yoloo.backend.category;

import com.yoloo.backend.category.rank.CategoryRankAlgorithm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Random;

@RunWith(JUnit4.class)
public class CategoryRankAlgorithmTest {

    @Test
    public void testRank() throws Exception {
        for (int i = 0; i < 5; i++) {
            double rank = CategoryRankAlgorithm.from(i + new Random().nextInt(10)).getRank();

            Thread.sleep(1500);
            System.out.println("Rank: " + rank);
        }
    }
}