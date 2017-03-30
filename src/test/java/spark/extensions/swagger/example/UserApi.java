package spark.extensions.swagger.example;

import com.google.common.collect.ImmutableList;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import spark.Request;
import spark.Response;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
public class UserApi {

    public User get(Request request, Response response) {
        return new User();
    }

    public List<User> findByName(Request request, Response response) {
        return ImmutableList.of(new User());
    }

    public User change(Request request, Response response) {
        return null;
    }

    @ApiOperation(value = "Creates a new user", nickname = "CreateUserRoute")
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, dataType = "string", name = "auth", paramType = "header"),
            @ApiImplicitParam(required = true, dataType = "me.serol.spark_swagger.route.request.CreateUserRequest", paramType = "body")
    })
    public Object add(Request request, Response response) {
        return null;
    }

    @ApiImplicitParams(
            @ApiImplicitParam()
    )
    public Object delete(Request request, Response response) {
        return null;
    }
}