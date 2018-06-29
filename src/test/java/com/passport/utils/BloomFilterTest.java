package com.passport.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.util.HashSet;
import java.util.Random;

/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
public class BloomFilterTest {

  private static Random gen = new Random();

  public static void main(String[] args) {
    int error = 0;
    HashSet<Integer> hashSet = new HashSet<>();
    int sizeOfNumberSet = 300000000;
    BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), sizeOfNumberSet);

    for (int i=0;i< sizeOfNumberSet;i++){
      int number = gen.nextInt();
      if(bloomFilter.mightContain(number) != hashSet.contains(number)){
        error++;
      }
      bloomFilter.put(number);
      hashSet.add(number);
    }
    System.out.println("Error count: " + error + ", error rate = " + String.format("%f", (float)error/(float) sizeOfNumberSet));
  }

}
