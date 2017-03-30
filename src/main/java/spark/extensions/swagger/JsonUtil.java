package spark.extensions.swagger;


//import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * Utilities for JSON
 */
public class JsonUtil {

    /**
     * Constructor.
     */
    private JsonUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Returns the JSON representation of the given object.
     *
     * @param object the object
     * @return the JSON representation
     */
    public static String toJson(Object object) {
//        return new Gson().toJson(object);
        return "";
    }

    /**
     * Returns the <code>ResponseTransformer</code>.
     *
     * @return the <code>ResponseTransformer</code>
     */
    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }

}