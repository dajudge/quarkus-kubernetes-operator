package com.dajudge.playop.facade;

import com.dajudge.playop.adapter.k8s.PodRepository;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static java.util.Arrays.asList;

@ApplicationScoped
public class PodFacade {
    private static final Logger LOG = LoggerFactory.getLogger(PodFacade.class);
    private PodRepository podRepository;

    public PodFacade() {
    }

    @Inject
    public PodFacade(final PodRepository podRepository) {
        this.podRepository = podRepository;
    }

    public void onPodEvent(@Observes final Watch.Response<V1Pod> podEvent) {
        if (!isRelevantEventType(podEvent.type)) {
            LOG.info("Irrelevant due to wrong event type: {} {}", podEvent.type, displayName(podEvent.object));
            return;
        }
        if (!hasTriggeringAnnotation(podEvent.object)) {
            LOG.info("Irrelevant due to missing annotation: {}", displayName(podEvent.object));
            return;
        }
        if (hasProcessedAnnotation(podEvent.object)) {
            LOG.info("Pod has already been processed: {}", displayName(podEvent.object));
            return;
        }
        processPod(podEvent.object);
    }

    private void processPod(final V1Pod pod) {
        LOG.info("Processing pod {}...", displayName(pod));
        pod.getMetadata().getAnnotations().put("play.dajudge.com/processed", "true");
        podRepository.patch(pod);
    }

    private String displayName(final V1Pod object) {
        return object.getMetadata().getNamespace() + "/" + object.getMetadata().getName();
    }

    private boolean hasTriggeringAnnotation(final V1Pod object) {
        if (null == object.getMetadata().getAnnotations()) {
            return false;
        }
        return "true".equals(object.getMetadata().getAnnotations().get("play.dajudge.com/managed"));
    }

    private boolean hasProcessedAnnotation(final V1Pod object) {
        if (null == object.getMetadata().getAnnotations()) {
            return false;
        }
        return "true".equals(object.getMetadata().getAnnotations().get("play.dajudge.com/processed"));
    }

    private boolean isRelevantEventType(final String type) {
        return asList("ADDED", "MODIFIED").contains(type);
    }
}
