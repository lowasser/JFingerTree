package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.ObjectArrays;

abstract class FingerTree<E, T extends Container<E>> implements Container<E> {

  @SuppressWarnings("unchecked")
  public static final <E, T extends Container<E>> FingerTree<E, T> empty() {
    return EMPTY;
  }

  public static final <E, T extends Container<E>> FingerTree<E, T> single(T single) {
    return new Single<E, T>(single);
  }

  public static final <E, T extends Container<E>> FingerTree<E, T> deep(
      Digit<E, T> pre,
      FingerTree<E, Node<E, T>> mid,
      Digit<E, T> suf) {
    return new Deep<E, T>(pre, mid, suf);
  }

  private static <E, T extends Container<E>> FingerTree<E, T> small(T[] contents) {
    FingerTree<E, Node<E, T>> emptyDeep = empty();
    switch (contents.length) {
      case 0:
        return empty();
      case 1:
        return single(contents[0]);
      case 2:
        return deep(Digit.of(contents[0]), emptyDeep, Digit.of(contents[1]));
      case 3:
        return deep(Digit.of(contents[0], contents[1]), emptyDeep, Digit.of(contents[2]));
      case 4:
        return deep(
            Digit.of(contents[0], contents[1]),
            emptyDeep,
            Digit.of(contents[2], contents[3]));
      case 5:
        return deep(
            Digit.of(contents[0], contents[1], contents[2]),
            emptyDeep,
            Digit.of(contents[3], contents[4]));
      default:
        throw new AssertionError();
    }
  }

  public abstract Optional<T> first();

  public abstract Optional<T> last();

  public Optional<FingerTree<E, T>> tail() {
    Optional<View<T, FingerTree<E, T>>> viewL = viewL();
    if (viewL.isPresent()) {
      return Optional.of(viewL.get().getRemainder());
    } else {
      return Optional.absent();
    }
  }

  public Optional<FingerTree<E, T>> init() {
    Optional<View<T, FingerTree<E, T>>> viewR = viewR();
    if (viewR.isPresent()) {
      return Optional.of(viewR.get().getRemainder());
    } else {
      return Optional.absent();
    }
  }

  public abstract Optional<View<T, FingerTree<E, T>>> viewL();

  public abstract Optional<View<T, FingerTree<E, T>>> viewR();

  FingerTree<E, T> consAll(T[] ts) {
    FingerTree<E, T> tree = this;
    for (int i = ts.length - 1; i >= 0; i--) {
      tree = tree.cons(ts[i]);
    }
    return tree;
  }

  FingerTree<E, T> snocAll(T[] ts) {
    FingerTree<E, T> tree = this;
    for (T t : ts) {
      tree = tree.snoc(t);
    }
    return tree;
  }

  public abstract FingerTree<E, T> cons(T t);

  public abstract FingerTree<E, T> snoc(T t);

  abstract FingerTree<E, T> appendTree(T[] m, FingerTree<E, T> other);

  @SuppressWarnings("rawtypes")
  private static final FingerTree EMPTY = new FingerTree() {
    @Override
    public Object index(int i) {
      throw new IndexOutOfBoundsException();
    }

    @Override
    public int length() {
      return 0;
    }

    @Override
    public Optional viewL() {
      return Optional.absent();
    }

    @Override
    public Optional first() {
      return Optional.absent();
    }

    @Override
    public Optional last() {
      return Optional.absent();
    }

    @Override
    public Optional viewR() {
      return Optional.absent();
    }

    @Override
    public Optional tail() {
      return Optional.absent();
    }

    @Override
    public Optional init() {
      return Optional.absent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public FingerTree cons(Container t) {
      return FingerTree.single(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FingerTree snoc(Container t) {
      return FingerTree.single(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    FingerTree appendTree(Container[] m, FingerTree other) {
      return other.consAll(m);
    }

    @Override
    public Iterator iterator() {
      return Iterators.emptyIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    FingerTree consAll(Container[] ts) {
      return small(ts);
    }

    @SuppressWarnings("unchecked")
    @Override
    FingerTree snocAll(Container[] ts) {
      return small(ts);
    }
  };

  private static final class Single<E, T extends Container<E>> extends FingerTree<E, T> {
    private final T value;

    private Single(T value) {
      this.value = checkNotNull(value);
    }

    @Override
    public E index(int i) {
      return value.index(i);
    }

    @Override
    public int length() {
      return value.length();
    }

    @Override
    public Optional<View<T, FingerTree<E, T>>> viewL() {
      return Optional.of(View.of(value, FingerTree.<E, T> empty()));
    }

    @Override
    public Optional<View<T, FingerTree<E, T>>> viewR() {
      return Optional.of(View.of(value, FingerTree.<E, T> empty()));
    }

    @Override
    public Optional<T> first() {
      return Optional.of(value);
    }

    @Override
    public Optional<T> last() {
      return Optional.of(value);
    }

    @Override
    public Optional<FingerTree<E, T>> tail() {
      return Optional.of(FingerTree.<E, T> empty());
    }

    @Override
    public Optional<FingerTree<E, T>> init() {
      return Optional.of(FingerTree.<E, T> empty());
    }

    @Override
    public FingerTree<E, T> cons(T t) {
      return deep(Digit.of(t), FingerTree.<E, Node<E, T>> empty(), Digit.of(value));
    }

    @Override
    public FingerTree<E, T> snoc(T t) {
      return deep(Digit.of(value), FingerTree.<E, Node<E, T>> empty(), Digit.of(t));
    }

    @Override
    FingerTree<E, T> appendTree(T[] m, FingerTree<E, T> other) {
      return other.consAll(m).cons(value);
    }

    @Override
    public Iterator<E> iterator() {
      return value.iterator();
    }

    @Override
    FingerTree<E, T> consAll(T[] ts) {
      if (ts.length == 0) {
        return this;
      }
      return small(ObjectArrays.concat(ts, value));
    }

    @Override
    FingerTree<E, T> snocAll(T[] ts) {
      if (ts.length == 0) {
        return this;
      }
      return small(ObjectArrays.concat(value, ts));
    }
  }

  private static final class Deep<E, T extends Container<E>> extends FingerTree<E, T> {
    private final Digit<E, T> pre;
    private final FingerTree<E, Node<E, T>> mid;
    private final Digit<E, T> suf;
    private final int length;

    private Deep(Digit<E, T> pre, FingerTree<E, Node<E, T>> mid, Digit<E, T> suf, int length) {
      this.pre = checkNotNull(pre);
      this.mid = checkNotNull(mid);
      this.suf = checkNotNull(suf);
      this.length = length;
    }

    private Deep(Digit<E, T> pre, FingerTree<E, Node<E, T>> mid, Digit<E, T> suf) {
      this(pre, mid, suf, pre.length() + mid.length() + suf.length());
    }

    @Override
    public E index(int i) {
      int prlen = pre.length();
      if (i < prlen) {
        return pre.index(i);
      } else {
        i -= prlen;
      }
      int midlen = length - prlen - suf.length();
      return (i < midlen) ? mid.index(i) : suf.index(i - midlen);
    }

    @Override
    public int length() {
      return length;
    }

    @Override
    public Optional<View<T, FingerTree<E, T>>> viewL() {
      View<T, Optional<Digit<E, T>>> preViewL = pre.viewL();
      T end = preViewL.getEnd();
      return Optional.of(View.of(end, deepL(preViewL.getRemainder(), mid, suf)));
    }

    @Override
    public Optional<View<T, FingerTree<E, T>>> viewR() {
      View<T, Optional<Digit<E, T>>> sufViewR = suf.viewR();
      T end = sufViewR.getEnd();
      return Optional.of(View.of(end, deepR(pre, mid, sufViewR.getRemainder())));
    }

    @Override
    public Optional<T> first() {
      return Optional.of(pre.first());
    }

    @Override
    public Optional<T> last() {
      return Optional.of(suf.last());
    }

    @Override
    public FingerTree<E, T> cons(T t) {
      View<Digit<E, T>, Optional<Node<E, T>>> preCons = pre.cons(t);
      Optional<Node<E, T>> remainder = preCons.getRemainder();
      FingerTree<E, Node<E, T>> newMid = remainder.isPresent() ? mid.cons(remainder.get()) : mid;
      return new Deep<E, T>(preCons.getEnd(), newMid, suf, length + t.length());
    }

    @Override
    public FingerTree<E, T> snoc(T t) {
      View<Digit<E, T>, Optional<Node<E, T>>> sufSnoc = suf.snoc(t);
      Optional<Node<E, T>> remainder = sufSnoc.getRemainder();
      FingerTree<E, Node<E, T>> newMid = remainder.isPresent() ? mid.snoc(remainder.get()) : mid;
      return new Deep<E, T>(pre, newMid, sufSnoc.getEnd());
    }

    @Override
    FingerTree<E, T> consAll(T[] ts) {
      if (ts.length == 0) {
        return this;
      }
      View<Digit<E, T>, Node<E, T>[]> preCons = pre.cons(new Digit<E, T>(ts));
      return deep(preCons.getEnd(), mid.consAll(preCons.getRemainder()), suf);
    }

    @Override
    FingerTree<E, T> snocAll(T[] ts) {
      if (ts.length == 0) {
        return this;
      }
      View<Digit<E, T>, Node<E, T>[]> sufSnoc = suf.snoc(new Digit<E, T>(ts));
      return deep(pre, mid.snocAll(sufSnoc.getRemainder()), sufSnoc.getEnd());
    }

    @Override
    FingerTree<E, T> appendTree(T[] m, FingerTree<E, T> other) {
      if (other == EMPTY) {
        return snocAll(m);
      } else if (other instanceof Single) {
        Single<E, T> s = (Single<E, T>) other;
        return snocAll(m).snoc(s.value);
      }
      Deep<E, T> deep = (Deep<E, T>) other;
      return deep(this.pre, addDigits(this.mid, this.suf, m, deep.pre, deep.mid), deep.suf);
    }

    @Override
    public Iterator<E> iterator() {
      return Iterables.concat(pre, mid, suf).iterator();
    }
  }

  private static <E, T extends Container<E>> FingerTree<E, T> deepL(
      Optional<Digit<E, T>> pre,
      FingerTree<E, Node<E, T>> mid,
      Digit<E, T> suf) {
    if (pre.isPresent()) {
      return deep(pre.get(), mid, suf);
    }
    Optional<View<Node<E, T>, FingerTree<E, Node<E, T>>>> midView = mid.viewL();
    if (midView.isPresent()) {
      View<Node<E, T>, FingerTree<E, Node<E, T>>> theMidView = midView.get();
      return deep(theMidView.getEnd().asDigit(), theMidView.getRemainder(), suf);
    } else {
      return suf.asFingerTree();
    }
  }

  private static <E, T extends Container<E>> FingerTree<E, T> deepR(
      Digit<E, T> pre,
      FingerTree<E, Node<E, T>> mid,
      Optional<Digit<E, T>> suf) {
    if (suf.isPresent()) {
      return deep(pre, mid, suf.get());
    }
    Optional<View<Node<E, T>, FingerTree<E, Node<E, T>>>> midView = mid.viewR();
    if (midView.isPresent()) {
      View<Node<E, T>, FingerTree<E, Node<E, T>>> theMidView = midView.get();
      return deep(pre, theMidView.getRemainder(), theMidView.getEnd().asDigit());
    } else {
      return pre.asFingerTree();
    }
  }

  @SuppressWarnings("unchecked")
  private static <E, T extends Container<E>> FingerTree<E, Node<E, T>> addDigits(
      FingerTree<E, Node<E, T>> m1,
      Digit<E, T> d1,
      T[] mid,
      Digit<E, T> d2,
      FingerTree<E, Node<E, T>> m2) {

    T[] tmp = (T[]) new Container[12];
    int p = 0;
    System.arraycopy(d1.contents, 0, tmp, p, d1.size());
    p += d1.size();
    System.arraycopy(mid, 0, tmp, p, mid.length);
    p += mid.length;
    System.arraycopy(d2.contents, 0, tmp, p, d2.size());
    p += d2.size();

    // Each of d1 and d2 are at least 1, so p >= 2.
    Node<E, T>[] nodes;
    switch (p % 3) {
      case 0:
        nodes = new Node[p / 3];
        for (int i = 0, j = 0; j < nodes.length; i += 3, j++) {
          nodes[j] = Node.of(tmp[i], tmp[i + 1], tmp[i + 2]);
        }
        break;
      case 1:
        nodes = new Node[(p - 4) / 3 + 2];
        nodes[0] = Node.of(tmp[0], tmp[1]);
        nodes[nodes.length - 1] = Node.of(tmp[p - 2], tmp[p - 1]);
        for (int i = 2, j = 1; j < nodes.length - 1; i += 3, j++) {
          nodes[j] = Node.of(tmp[i], tmp[i + 1], tmp[i + 2]);
        }
        break;
      case 2:
        nodes = new Node[p / 3 + 1];
        nodes[0] = Node.of(tmp[0], tmp[1]);
        for (int i = 2, j = 1; j < nodes.length; i += 3, j++) {
          nodes[j] = Node.of(tmp[i], tmp[i + 1], tmp[i + 2]);
        }
        break;
      default:
        throw new AssertionError();
    }
    return m1.appendTree(nodes, m2);
  }
}
