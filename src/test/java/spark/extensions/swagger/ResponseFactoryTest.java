package spark.extensions.swagger;

import com.stylismo.sparkswagger.example.User;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;
import org.junit.Test;
import spark.Request;
import spark.Route;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static spark.extensions.swagger.ResponseFactory.*;
import static java.util.Collections.singletonList;

public class ResponseFactoryTest {

    @Test
    public void responseTypeFromLambda() throws Exception {
        Type myClass = returnType((q, a) -> singletonList(new User()));
        Response myResponse = type(myClass);
        System.out.println(myClass);
        System.out.println(myResponse.getSchema().getType());
        System.out.println(myResponse.getSchema().getType());
    }

    @Test
    public void test() throws ClassNotFoundException {
        Route routeFromLambda = (q, a) -> singletonList(new User());
        String[] strings = methodRefInfo(routeFromLambda);
        System.out.println(Arrays.asList(strings));
        Class containingClass = Class.forName(strings[0].replace("/", "."));
        Operation operation = new Operation().summary(containingClass.getSimpleName() + "::" + strings[1]);
        System.out.println(operation);
    }

    @Test
    public void responseTypeFromMethodReference() throws Exception {
        Type myClass = returnType(this::example);
        Response myResponse = type(myClass);
        System.out.println(myClass);
        System.out.println(myResponse.getSchema().getType());
        System.out.println(((RefProperty) ((ArrayProperty) myResponse.getSchema()).getItems()).getSimpleRef());
    }

//    @Test
//    public void responseTypeFromRoute() throws Exception {
//        Class myClass = returnType(new Route() {
//            @Override
//            public Object handle(Request request, spark.Response response) throws Exception {
//                return singletonList(new User());
//            }
//        });
//        Response myResponse = type(myClass);
//        System.out.println(myClass);
//        System.out.println(myResponse.getSchema().getType());
//    }
//
//    @Test
//    public void responseTypeFromSwaggerRoute() throws Exception {
//        SparkSwagger.SwaggerRoute mySparkOperation = SparkSwagger.operation(this::example, "summary");
//        Class myClass = returnType(mySparkOperation);
//        Response myResponse = type(myClass);
//        System.out.println(myClass);
//        System.out.println(myResponse.getSchema().getType());
//    }

    public List<User> example(Request request, spark.Response response) {
        return singletonList(new User());
    }
}