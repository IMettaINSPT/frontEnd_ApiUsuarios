package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Dashboard.DashboardSummaryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DashboardApiClient extends BaseApiClient {

    public DashboardApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public DashboardSummaryResponse summary(String jwt) {
        return get("/api/dashboard/summary", jwt, DashboardSummaryResponse.class);
    }
}
