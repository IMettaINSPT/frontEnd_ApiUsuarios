package com.tp.frontend.client;

import com.tp.frontend.dto.Dashboard.DashboardSummaryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
@Component
public class DashboardApiClient extends BaseApiClient {

    public DashboardApiClient(WebClient webClient) {
        super(webClient);
    }

    public DashboardSummaryResponse summary(String jwt) {
        return get("/dashboard/summary", jwt, DashboardSummaryResponse.class);
    }
}
