package com.dajudge.playop.rest;

import com.dajudge.playop.adapter.k8s.PodRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/control")
public class HelloWorldResource {
    @Inject
    private PodRepository podRepository;

    @GET
    @Path("/exit")
    @Produces("text/plain")
    public String sayHello() {
        return "world";
    }
}
