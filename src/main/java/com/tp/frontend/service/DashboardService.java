package com.tp.frontend.service;

import com.tp.frontend.client.DashboardApiClient;
import com.tp.frontend.dto.Dashboard.DashboardSummaryResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardApiClient api;

    public DashboardService(DashboardApiClient api) {
        this.api = api;
    }

    public DashboardSummaryResponse summary(String jwt) {
        return api.summary(jwt);
    }
}
