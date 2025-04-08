package com.alibaba.cloud.examples;

import com.alibaba.csp.sentinel.adapter.okhttp.SentinelOkHttpInterceptor;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;

@RestController
public class MyController {

    private final OkHttpClient client;

    public MyController() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new MySentinelOkHttpInterceptor())
                .build();

        initSentinelRules();
    }

    private void initSentinelRules() {
        DegradeRuleManager.loadRules(Collections.singletonList(
                new DegradeRule("okhttp:GET:http://localhost:18083/hello")
                        .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO)
                        .setCount(0.5)
                        .setTimeWindow(10)
        ));
    }

    @GetMapping("/call-api")
    @SentinelResource(value = "externalApi")
    public String callApi() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:18083/hello")
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();


}

    public String apiFallback(Throwable t) {
        return "Service temporarily unavailable. Please try again later.";
    }
}