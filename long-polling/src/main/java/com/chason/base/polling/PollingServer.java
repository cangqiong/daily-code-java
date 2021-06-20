package com.chason.base.polling;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 长轮询服务端
 *
 * @author chason
 */
@Slf4j
@RestController
@SpringBootApplication
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class PollingServer {

    // 服务端超时时间
    private static final int CONNECTION_TIME_OUT = 3000;

    @RequestMapping("/listener")
    public void handleLongPolling(HttpServletRequest request, HttpServletResponse response) {
        String dataId = request.getParameter("dataId");
        log.info("enter a dataId:[{}]", dataId);
        AsyncContext asyncContext = request.startAsync(request, response);
        AsyncTask asyncTask = new AsyncTask(asyncContext, true);
        // 维护dataId和异步上下文的关联
        dataIdContext.put(dataId, asyncTask);

        // 定时器启动写入，30s后写入304
        timOutCheck.schedule(() -> {
            if (asyncTask.isTimeOutFlag()) {
                dataIdContext.remove(dataId, asyncTask);
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                log.info("enter a dataId end");
                asyncContext.complete();
            }
        }, CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS);

    }

    @RequestMapping("/pushConfig")
    @SneakyThrows
    public String pushConfig(String dataId, String configInfo) {
        log.info("publish config info dataId:[{}],configInfo:[{}]", dataId, configInfo);
        // 清空原有上下文
        Collection<AsyncTask> asyncTasks = dataIdContext.removeAll(dataId);
        for (AsyncTask task : asyncTasks) {
            task.setTimeOutFlag(false);
            HttpServletResponse response = (HttpServletResponse) task.getAsyncContext().getResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(configInfo);
            task.getAsyncContext().complete();
        }
        return "SUCCESS";
    }

    // 一个dataId对应多个异步任务
    private Multimap<String, AsyncTask> dataIdContext = Multimaps.synchronizedMultimap(HashMultimap.create());

    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("longPolling-timeout-checker-%d").build();

    private ScheduledExecutorService timOutCheck = new ScheduledThreadPoolExecutor(1, threadFactory);

    @Data
    private class AsyncTask {
        // 长轮询的上下文，包括请求和响应体
        private AsyncContext asyncContext;
        // 超时标记
        private boolean timeOutFlag;

        public AsyncTask(AsyncContext asyncContext, boolean timeOutFlag) {
            this.asyncContext = asyncContext;
            this.timeOutFlag = timeOutFlag;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PollingServer.class, args);
    }
}
