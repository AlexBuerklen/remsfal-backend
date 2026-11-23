package de.remsfal.core.api;

import java.util.UUID;

import de.remsfal.core.json.UserJson;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path(UserEndpoint.CONTEXT + "/" + UserEndpoint.VERSION)
public interface UserAvatarEndpoint {

    String OWN_AVATAR_PATH = UserEndpoint.SERVICE + "/avatar";
    String FILE_PART = "file";
    String MEDIA_TYPE = "image/webp";

    @PUT
    @Path(OWN_AVATAR_PATH)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Upload or replace the avatar of the signed-in user")
    @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA,
        schema = @Schema(type = SchemaType.OBJECT, requiredProperties = {FILE_PART},
            properties = @SchemaProperty(name = FILE_PART, type = SchemaType.STRING, format = "binary"))))
    @APIResponse(responseCode = "200", description = "Avatar saved and updated user returned")
    @APIResponse(responseCode = "400", description = "Expected exactly one decodable image in the file part")
    @APIResponse(responseCode = "401", description = "No user authentication provided via session cookie")
    @APIResponse(responseCode = "413", description = "Image exceeds the file size or pixel limit")
    @APIResponse(responseCode = "415", description = "Unsupported image type")
    UserJson putAvatar(@Parameter(hidden = true) MultipartFormDataInput input);

    @DELETE
    @Path(OWN_AVATAR_PATH)
    @Operation(summary = "Remove the avatar of the signed-in user")
    @APIResponse(responseCode = "204", description = "The user has no avatar")
    @APIResponse(responseCode = "401", description = "No user authentication provided via session cookie")
    void deleteAvatar();

    @GET
    @Path("users/{userId}/avatar")
    @Produces(MEDIA_TYPE)
    @Operation(summary = "Retrieve an avatar visible to the signed-in user")
    @APIResponse(responseCode = "200", description = "Avatar image",
        content = @Content(mediaType = MEDIA_TYPE, schema = @Schema(type = SchemaType.STRING, format = "binary")))
    @APIResponse(responseCode = "400", description = "Invalid avatar size")
    @APIResponse(responseCode = "401", description = "No user authentication provided via session cookie")
    @APIResponse(responseCode = "404", description = "Avatar not found")
    Response getAvatar(
        @PathParam("userId") UUID userId,
        @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, enumeration = {"64", "128", "512"}))
        @QueryParam("size") @NotNull String size,
        @Parameter(description = "Optional version of the avatar identified by its content hash")
        @QueryParam("version") String version);
}
