package com.alibaba.cloud.examples;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.adapter.okhttp.SentinelOkHttpConfig;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MySentinelOkHttpInterceptor implements Interceptor {

    private final SentinelOkHttpConfig config;

    public MySentinelOkHttpInterceptor() {
        this.config = new SentinelOkHttpConfig();
    }

    public MySentinelOkHttpInterceptor(SentinelOkHttpConfig config) {
        AssertUtil.notNull(config, "config cannot be null");
        this.config = config;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Entry entry = null;
        try {
            Request request = chain.request();
            String name = config.getResourceExtractor().extract(request, chain.connection());
            if (StringUtil.isNotBlank(config.getResourcePrefix())) {
                name = config.getResourcePrefix() + name;
            }
            entry = SphU.entry(name, ResourceTypeConstants.COMMON_WEB, EntryType.OUT);
            Response resp = chain.proceed(request);
            if (!resp.isSuccessful()) {

                RuntimeException e= new RuntimeException("xxxxxxxxxxxxxxxxx");
                Tracer.traceEntry(e, entry);
                throw e;
            }
            return resp;
        } catch (BlockException e) {
            return config.getFallback().handle(chain.request(), chain.connection(), e);
        } catch (IOException ex) {
            Tracer.traceEntry(ex, entry);
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }
}
