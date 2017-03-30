package spark.extensions.swagger;

import io.swagger.models.Operation;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.annotation.Nonnull;

public class SwaggerRoute implements Route {
    private final Operation operation;
    private final Route route;

    SwaggerRoute(Operation operation, Route route) {
        this.operation = operation;
        this.route = route;
    }

    @Override
    public Object handle(@Nonnull Request request, @Nonnull Response response) throws Exception {
        return route.handle(request, response);
    }

    public Route route() {
        return route;
    }

    public Operation operation() {
        return operation;
    }

//    public static Operation operation() {
//        return new Operation();
//    }

    public static Operation operation(String summary) {
        return new Operation().summary(summary);
    }

    public static SwaggerRoute operation(String summary, Route route) {
        return (SwaggerRoute) new SwaggerRoute(operation(summary), route);
    }

    public static Parameter bodyParam() {
        return new BodyParameter();
    }

    public static Parameter headerParam() {
        return new BodyParameter();
    }

    public static Parameter queryParam(String description) {
        QueryParameter queryParameter = new QueryParameter();
        queryParameter.setDescription(description);
        return queryParameter;
    }

    public static Parameter pathParam(String description) {
        PathParameter pathParameter = new PathParameter();
        pathParameter.setDescription(description);
        return pathParameter;
    }
}