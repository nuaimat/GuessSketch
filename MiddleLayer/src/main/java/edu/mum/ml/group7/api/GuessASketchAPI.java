package edu.mum.ml.group7.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;


/**
 * Root resource (exposed at "api" path)
 */
@Path("api")
public class GuessASketchAPI {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it! simple GET";
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonObject myObject = Json.createObjectBuilder()
                .add("name", "Agamemnon")
                .add("age", 32)
                .build();
        return myObject.toString();
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response guessImage(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("label") String label
            ) throws IOException {
        String result = String.format("Image guessed image label (%s)", label) ;

        /*final File tempFile = File.createTempFile("guess-a-sketch", "");
        //tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(fileInputStream, out);
        }

        result += "on " + tempFile.getAbsolutePath();*/
        return Response.status(201).entity(result).build();
    }
}
