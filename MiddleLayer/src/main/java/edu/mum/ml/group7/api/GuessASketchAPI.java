package edu.mum.ml.group7.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.glassfish.jersey.media.multipart.FormDataParam;

import static org.apache.http.HttpHeaders.USER_AGENT;


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
        return "Got it!";
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonObject myObject = Json.createObjectBuilder()
                .add("name", "Mo")
                .add("age", 33)
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
        System.out.println("\nProcessing a request ...");

        String url = Constants.TENSORFLOW_API;
        File temp = File.createTempFile("guess-",".jpg", new File(Constants.UPLOAD_FOLDER_BASE));
        String uploadedFileLocation = temp.getName();
        // save it
        writeToFile(fileInputStream, temp);

        String result = doPost(url, temp, label);
        temp.delete();

        return Response.status(200).entity(result).build();
    }

    private String doPost(String url, File uploadedFile, String label) throws IOException {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        FileBody bin = new FileBody(uploadedFile);
        StringBody id = new StringBody(label);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", bin);
        reqEntity.addPart("label", id);


        post.setEntity(reqEntity);

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result1 = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result1.append(line);
        }

        System.out.println(result1.toString());

        return result1.toString();

    }

    private void writeToFile(InputStream uploadedInputStream,
                             File tempFile) {

        try {
            OutputStream out = new FileOutputStream(tempFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
