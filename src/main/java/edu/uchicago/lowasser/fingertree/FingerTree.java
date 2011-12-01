package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;

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

  public abstract FingerTree<E, T> cons(T t);

  public abstract FingerTree<E, T> snoc(T t);

  @SuppressWarnings("rawtypes")
  private static final FingerTree EMPTY = new FingerTree() {
    public Object index(int i) {
      throw new IndexOutOfBoundsException();
    }

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
  };

  private static final class Single<E, T extends Container<E>> extends FingerTree<E, T> {
    private final T value;

    private Single(T value) {
      this.value = checkNotNull(value);
    }

    public E index(int i) {
      return value.index(i);
    }

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
}
