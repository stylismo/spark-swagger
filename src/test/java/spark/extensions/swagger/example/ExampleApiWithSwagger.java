package spark.extensions.swagger.example;

import com.stylismo.sparkswagger.example.UserApi;
import org.eclipse.jetty.util.resource.Resource;
import spark.route.SwaggerService;

import static spark.extensions.swagger.SparkSwagger.*;
import static spark.extensions.swagger.SwaggerRoute.*;

public class ExampleApiWithSwagger {

    public static void main(String[] args) {
        UserApi userApi = new UserApi();

        path("/api", () -> {
            path("/user", () -> {
                get("/:id",
                        operation("Get user by id").parameter(pathParam("id of the user")),
                        userApi::get);

                get("/find/:name",
                        operation("Find users by name"),
                        userApi::findByName);
            });
        });

        get("/apidoc/swagger", (q, a) -> SwaggerService.getSwaggerJson());

        get("/list", (req, res) -> {
            Resource resource = Resource.newClassPathResource("/META-INF/resources");
            return resource.getListHTML("/", true);
        });

        exception(Exception.class, (exception, request, response) -> exception.printStackTrace());
    }

}