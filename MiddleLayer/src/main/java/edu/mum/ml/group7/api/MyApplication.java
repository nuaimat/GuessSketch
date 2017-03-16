package edu.mum.ml.group7.api;


import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by Mo nuaimat on 3/14/17.
 */
@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        super(GuessASketchAPI.class, MultiPartFeature.class);
    }
}