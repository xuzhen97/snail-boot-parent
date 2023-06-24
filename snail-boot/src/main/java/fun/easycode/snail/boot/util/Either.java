package fun.easycode.snail.boot.util;

import java.util.Optional;
import java.util.function.Function;

public class Either<E extends Exception, R> {
    private final E error;
    private final R result;

    private Either(E error, R result) {
        this.error = error;
        this.result = result;
    }

    public static <E extends Exception, R> Either<E, R> error(E error) {
        return new Either<>(error, null);
    }

    public static <E extends Exception, R> Either<E, R> result(R result) {
        return new Either<>(null, result);
    }

    public Optional<E> getError() {
        return Optional.ofNullable(error);
    }

    public Optional<R> getResult() {
        return Optional.ofNullable(result);
    }

    public boolean isError() {
        return error != null;
    }

    public boolean isResult() {
        return result != null;
    }

    public <T> Optional<T> mapError(Function<E, T> mapper) {
        if (isError()) {
            return Optional.of(mapper.apply(error));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapResult(Function<R, T> mapper) {
        if (isResult()) {
            return Optional.of(mapper.apply(result));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        if (isError()) {
            return "Error: " + error.toString();
        }
        return "Result: " + result.toString();
    }

    /**
     * 将一个可能抛出异常的方法转换为Either
     * @param function 可能抛出异常的方法
     * @return Either
     * @param <T> 参数类型
     * @param <R> 返回值类型
     */
    public static <T,R> Function<T, Either<Exception, R>> wrap(Function<T,R> function){
        return t -> {
            try {
                return Either.result(function.apply(t));
            } catch (Exception e) {
                return Either.error(e);
            }
        };
    }
}
