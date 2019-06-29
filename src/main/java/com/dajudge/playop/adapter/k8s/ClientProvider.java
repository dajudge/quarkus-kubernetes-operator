package com.dajudge.playop.adapter.k8s;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.ClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ClientProvider {
    @Produces
    @ApplicationScoped
    public ApiClient getApiClient() throws IOException {
        final ApiClient client = ClientBuilder.cluster().build();
        client.getHttpClient().setReadTimeout(0, SECONDS);
        Configuration.setDefaultApiClient(client);
        return client;
    }

}
