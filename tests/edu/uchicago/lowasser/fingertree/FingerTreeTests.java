package edu.uchicago.lowasser.fingertree;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;

public class FingerTreeTests {
  private static final ImmutableList<Elem<Integer>> SAMPLE_ELEMS = ImmutableList.copyOf(Lists
      .transform(
          Ints.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          new Function<Integer, Elem<Integer>>() {
            @Override
            public Elem<Integer> apply(Integer arg0) {
              return Elem.of(arg0);
            }
          }));

  private static final List<FingerTree<Integer, Elem<Integer>>> SAMPLE_TREES;

  static {
    ImmutableList.Builder<FingerTree<Integer, Elem<Integer>>> builder = ImmutableList.builder();
    for (int i = 0; i <= SAMPLE_ELEMS.size(); i++) {

    }
  }
}
