package com.chenhy.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 自定义 SimpleClientHttpRequestFactory 设置超时
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // 连接超时 5 秒
        requestFactory.setReadTimeout(30000); // 读取超时 30 秒

        // 使用自定义 RequestFactory 构建 RestTemplate
        RestTemplate restTemplate = builder.requestFactory(() -> requestFactory).build();

        // 添加 Gzip 解压缩拦截器
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(new GzipDecompressingClientHttpRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    // 自定义 Gzip 解压缩拦截器
    public static class GzipDecompressingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            if (isGzipResponse(response)) {
                return new GzipDecompressingClientHttpResponse(response);
            }
            return response;
        }

        private boolean isGzipResponse(ClientHttpResponse response) {
            return "gzip".equalsIgnoreCase(response.getHeaders().getFirst("Content-Encoding"));
        }

        private static class GzipDecompressingClientHttpResponse implements ClientHttpResponse {

            private final ClientHttpResponse response;

            public GzipDecompressingClientHttpResponse(ClientHttpResponse response) {
                this.response = response;
            }

            @Override
            public InputStream getBody() throws IOException {
                return new GZIPInputStream(response.getBody());
            }

            @Override
            public HttpStatusCode getStatusCode() throws IOException {
                return response.getStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return response.getStatusText();
            }

            @Override
            public void close() {
                response.close();
            }

            @Override
            public org.springframework.http.HttpHeaders getHeaders() {
                return response.getHeaders();
            }
        }
    }
}
