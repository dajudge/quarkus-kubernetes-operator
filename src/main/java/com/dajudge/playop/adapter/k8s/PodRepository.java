package com.dajudge.playop.adapter.k8s;

import com.dajudge.playop.util.HandlerManager;
import com.dajudge.playop.util.HandlerRegistration;
import com.google.common.reflect.TypeToken;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.util.function.Consumer;

import static io.kubernetes.client.util.Watch.createWatch;

@ApplicationScoped
public class PodRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PodRepository.class);
    private final HandlerManager handlers = new HandlerManager();

    private ApiClient client;
    private Event<Watch.Response<V1Pod>> podEvent;

    public PodRepository() {
    }

    @Inject
    public PodRepository(final ApiClient client, final Event<Watch.Response<V1Pod>> podEvent) {
        this.client = client;
        this.podEvent = podEvent;
    }


    void onStart(@Observes StartupEvent ev) {
        handlers.add(watch(podEvent::fire));
    }

    void onStop(@Observes ShutdownEvent ev) {
        handlers.release();
    }

    private HandlerRegistration watch(final Consumer<Watch.Response<V1Pod>> cb) {
        final Watch<V1Pod> watch = createPodWatch();
        new Thread(() -> watchPods(watch, cb)).start();
        return () -> closeWatch(watch);
    }

    private void closeWatch(final Watch<V1Pod> watch) {
        try {
            watch.close();
        } catch (final IOException e) {
            LOG.error("Failed to stop watching for pods", e);
        }
    }

    private void watchPods(final Watch<V1Pod> watch, final Consumer<Watch.Response<V1Pod>> cb) {
        LOG.info("Entered pod watcher thread.");
        try {
            for (final Watch.Response<V1Pod> item : watch) {
                try {
                    cb.accept(item);
                } catch (final Exception e) {
                    LOG.error("Error handling pod event", e);
                }
            }
        } catch (final RuntimeException e) {
            if (LOG.isDebugEnabled()) {
                LOG.info("Watcher loop interrupted", e);
            } else {
                LOG.info("Watcher loop interrupted");
            }
        }
        LOG.info("Left pod watcher thread.");
    }

    private Watch<V1Pod> createPodWatch() {
        try {
            final CoreV1Api api = new CoreV1Api(client);
            final Call listPodsCall = api.listPodForAllNamespacesCall(null, null, true, null, null, null, null, null, true, null, null);
            return createWatch(client, listPodsCall, new TypeToken<Watch.Response<V1Pod>>() {
            }.getType());
        } catch (final ApiException e) {
            throw new RuntimeException("Failed to setup watch for new pods", e);
        }
    }

    public void patch(final V1Pod pod) {
        try {
            final CoreV1Api api = new CoreV1Api(client);
            api.replaceNamespacedPod(pod.getMetadata().getName(), pod.getMetadata().getNamespace(), pod, null, null);
        } catch (final ApiException e) {
            throw new RuntimeException("Failed to patch pod: " + pod.getMetadata().getNamespace() + "/" + pod.getMetadata().getName(), e);
        }
    }
}
