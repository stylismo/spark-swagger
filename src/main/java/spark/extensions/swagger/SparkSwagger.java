package spark.extensions.swagger;

import io.swagger.models.Operation;
import spark.Route;
import spark.Spark;

public class SparkSwagger extends Spark {

    public static void get(String path, Operation operation, Route route) {
        get(path, new SwaggerRoute(operation, route));
    }

}
