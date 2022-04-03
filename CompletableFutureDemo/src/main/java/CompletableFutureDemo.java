import cn.hutool.json.JSONUtil;
import dto.AsyncQueryDTO;
import dto.DemoResultDTO;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CompletableFutureDemo {

    private static final int COMPLETABLE_FUTURE_A_TYPE = 1;

    private static final int COMPLETABLE_FUTURE_B_TYPE = 2;

    private static final int COMPLETABLE_FUTURE_C_TYPE = 3;

    private static final int COMPLETABLE_FUTURE_D_TYPE = 4;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        System.out.println(JSONUtil.toJsonStr(getAsyncDemoResult()));
    }

    public static DemoResultDTO getAsyncDemoResult() {
        CompletableFuture<AsyncQueryDTO<String>> completableFutureA = getResultA();
        CompletableFuture<AsyncQueryDTO<String>> completableFutureB = getResultB();
        CompletableFuture<AsyncQueryDTO<String>> completableFutureC = getResultC();
        CompletableFuture<AsyncQueryDTO<Integer>> completableFutureD = getResultD();
        Stream<CompletableFuture> completableFutureStream = Stream.of(completableFutureA, completableFutureB,
                completableFutureC, completableFutureD);
        CompletableFuture[] completableFutures = completableFutureStream.toArray(CompletableFuture[]::new);
        //等待所有线程执行完毕
        CompletableFuture.allOf(completableFutures).join();
        //设置返回值
        DemoResultDTO demoResultDTO = new DemoResultDTO();
        setAsyncValue(demoResultDTO, completableFutures);

        return demoResultDTO;
    }

    /**
     * @description A业务结果返回 查询db or 接口 返回类型自定义
     * @author zcyang
     * @date 2022/04/03 18:50
     */
    private static CompletableFuture<AsyncQueryDTO<String>> getResultA() {
        return CompletableFuture.supplyAsync(() -> {
            return getAsyncResult(COMPLETABLE_FUTURE_A_TYPE, "A");
        }, threadPoolExecutor).exceptionally(exception -> {
            return getAsyncResult(COMPLETABLE_FUTURE_A_TYPE, null);
        });
    }

    /**
     * @description B业务结果返回 查询db or 接口 返回类型自定义
     * @author zcyang
     * @date 2022/04/03 18:50
     */
    private static CompletableFuture<AsyncQueryDTO<String>> getResultB() {
        return CompletableFuture.supplyAsync(() -> {
            return getAsyncResult(COMPLETABLE_FUTURE_B_TYPE, "B");
        }, threadPoolExecutor).exceptionally(exception -> {
            return getAsyncResult(COMPLETABLE_FUTURE_B_TYPE, null);
        });
    }

    /**
     * @description C业务结果返回 查询db or 接口 返回类型自定义
     * @author zcyang
     * @date 2022/04/03 18:50
     */
    private static CompletableFuture<AsyncQueryDTO<String>> getResultC() {
        return CompletableFuture.supplyAsync(() -> {
            return getAsyncResult(COMPLETABLE_FUTURE_C_TYPE, "C");
        }, threadPoolExecutor).exceptionally(exception -> {
            return getAsyncResult(COMPLETABLE_FUTURE_C_TYPE, null);
        });
    }

    /**
     * @description D业务结果返回 查询db or 接口 返回类型自定义
     * @author zcyang
     * @date 2022/04/03 18:50
     */
    private static CompletableFuture<AsyncQueryDTO<Integer>> getResultD() {
        return CompletableFuture.supplyAsync(() -> {
            return getAsyncResult(COMPLETABLE_FUTURE_D_TYPE, 0);
        }, threadPoolExecutor).exceptionally(exception -> {
            return getAsyncResult(COMPLETABLE_FUTURE_D_TYPE, null);
        });
    }


    /***
     * @description 创建async返回实体
     * @author zcyang
     * @date 2022/03/15 14:40
     */
    private static <U> AsyncQueryDTO<U> getAsyncResult(int type, U data) {
        return new AsyncQueryDTO(data, type);
    }

    /***
     * @description 设置Async结果
     * @author zcyang
     * @date 2022/03/15 14:40
     */
    private static void setAsyncValue(DemoResultDTO demoResultDTO, CompletableFuture[] completableFutures) {
        try {
            for (CompletableFuture future : completableFutures) {
                AsyncQueryDTO asyncQueryDTO = (AsyncQueryDTO) future.get();
                if (Objects.isNull(asyncQueryDTO) || Objects.isNull(asyncQueryDTO.getData())) {
                    continue;
                }
                switch (asyncQueryDTO.getDataType()) {
                    case COMPLETABLE_FUTURE_A_TYPE:
                        demoResultDTO.setResultA((String) asyncQueryDTO.getData());
                        break;
                    case COMPLETABLE_FUTURE_B_TYPE:
                        demoResultDTO.setResultB((String) asyncQueryDTO.getData());
                        break;
                    case COMPLETABLE_FUTURE_C_TYPE:
                        demoResultDTO.setResultC((String) asyncQueryDTO.getData());
                        break;
                    case COMPLETABLE_FUTURE_D_TYPE:
                        demoResultDTO.setResultD((Integer) asyncQueryDTO.getData());
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
