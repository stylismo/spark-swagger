package spark.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import spark.Spark;
import spark.extensions.swagger.SwaggerRoute;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static spark.extensions.swagger.ObjectMapperFactory.createJson;
import static spark.extensions.swagger.ResponseFactory.*;

// todo needs to be in this package until properties of route enry are accessible
public class SwaggerService {
    public static Swagger swagger = new Swagger();
    private static String swaggerJson = null;

    public static String getSwaggerJson() throws JsonProcessingException, ClassNotFoundException {
        if (swaggerJson == null) {

            for (RouteEntry routeEntry : Spark.routes()) {
                HttpMethod httpMethod = routeEntry.httpMethod;
                String acceptedType = routeEntry.acceptedType;
                String path = routeEntry.path;
                Object target = routeEntry.target;

                if (target instanceof SwaggerRoute) {
                    Operation operation = ((SwaggerRoute) target).operation();
                    operation.consumes(acceptedType);

                    try {
                        Type s = returnType(((SwaggerRoute) target).route());
                        Map<String, Response> myResponses = operation.getResponses();
                        if (myResponses == null || !myResponses.containsKey(String.valueOf(200))) {
                            operation.response(200, type(s));
                        }
                        swagger.path(path, new Path().set(httpMethod.name(), operation));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!httpMethod.equals(HttpMethod.before)) { // todo
                    String[] strings = methodRefInfo(target);
                    System.out.println(Arrays.asList(strings));
                    Class containingClass = Class.forName(strings[0].replace("/", "."));
                    Operation operation = new Operation().summary(containingClass.getSimpleName() + "::" + strings[1]);
                    swagger.path(path, new Path().set(httpMethod.name(), operation));
                }
            }

            swaggerJson = createJson().writeValueAsString(swagger);
        }

        return swaggerJson;
    }
}