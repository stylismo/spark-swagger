package spark.extensions.swagger;

import com.stylismo.sparkswagger.example.User;
import com.stylismo.sparkswagger.example.UserApi;
import com.stylismo.sparkswagger.example.typeref.MethodFinder;
import com.stylismo.sparkswagger.example.typeref.TypeReference;
import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import spark.Route;
import spark.route.SwaggerService;
import sun.reflect.ConstantPool;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;


public class ResponseFactory {

    public static <T> T create(TypeReference<T> type) {
        return type.newInstance();
    }

    public static io.swagger.models.Response type(Type responseType) {

//        if (responseType == null) {
//            // pick out response from method declaration
//            LOGGER.debug("picking up response class from method " + method);
//            responseType = method.getGenericReturnType();
//        }
//        if (isValidResponse(responseType)) {
//            final Property property = ModelConverters.getInstance().readAsProperty(responseType);
//            if (property != null) {
//                final Property responseProperty = Reader.ContainerWrapper.wrapContainer(responseContainer, property);
//                final int responseCode = (apiOperation == null) ? 200 : apiOperation.code();
//                operation.response(responseCode, new io.swagger.models.Response().description(SUCCESSFUL_OPERATION).schema(responseProperty)
//                        .headers(defaultResponseHeaders));
//                appendModels(responseType);
//            }
//        }

        Property property = ModelConverters.getInstance().readAsProperty(responseType);
        property = ContainerWrapper.wrapContainer(null, property);
        appendModels(responseType);

        return new io.swagger.models.Response()
                .description("successful operation")
                .schema(property);
//                    .schema(responseProperty)
//                    .headers(defaultResponseHeaders));
//        }
    }

    private static void appendModels(Type type) {
        final Map<String, Model> models = ModelConverters.getInstance().readAll(type);
        for (Map.Entry<String, Model> entry : models.entrySet()) {
            SwaggerService.swagger.model(entry.getKey(), entry.getValue());
        }
    }


    public static void main(String[] args) throws Exception {
        UserApi userApi = new UserApi();

        Object serializedLambda = returnType(userApi::get);
        serializedLambda = returnType((q, a) -> {
            return new User();
        });
        System.out.println(serializedLambda);
    }

    public static Type returnType(Route lambda) throws Exception {
//        Class containingClass = Class.forName(lambda.getClass().getName());
//        String methodName = lambda.getImplMethodName();

        String[] strings = methodRefInfo(lambda);
        System.out.println(Arrays.asList(strings));
        Class containingClass = Class.forName(strings[0].replace("/", "."));
        for (Method m : containingClass.getDeclaredMethods()) {
            if (m.getName().equals(strings[1])) {
                System.out.println(m.getName());
                System.out.println("return type: " + m.getReturnType());

                return m.getGenericReturnType();
//                return m.getReturnType();

//                Method method = lambda.getClass().getDeclaredMethod("get$Lambda");
//                m.setAccessible(true);

//                if (m.getName().equals("lambda$main$0"))
//                {
//                    System.out.println(method(lambda));
//                }
//                Object x = m.invoke(lambda);
//                System.out.println(x);
            }
        }

//
//        for (Method m : lambda.getClass().getDeclaredMethods()) {
//            if (m.getName().equals("get$Lambda"))
//            {
//                System.out.println(m);
//
////                Method method = lambda.getClass().getDeclaredMethod("get$Lambda");
//                m.setAccessible(true);
//                Object x = m.invoke(lambda);
//                System.out.println(x);
//            }
//        }
//
//        final Method method = lambda.getClass().getDeclaredMethod("writeReplace");
//        method.setAccessible(true);
//        return (SerializedLambda) method.invoke(lambda);

        return null;
    }

    // http://dan.bodar.com/2014/09/01/getting-the-generic-signature-of-a-java-8-lambda/
    // http://stackoverflow.com/questions/23861619/how-to-read-lambda-expression-bytecode-using-asm
    public static String[] methodRefInfo(Object routeTarget) {
        try {
            // This is some robustified shit right here.
            Method getConstantPool = Class.class.getDeclaredMethod("getConstantPool");
            getConstantPool.setAccessible(true);

            ConstantPool constantPool = (ConstantPool) getConstantPool.invoke(routeTarget.getClass());
            return constantPool.getMemberRefInfoAt(constantPool.getSize() - 3);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }


    // https://github.com/benjiman/lambda-type-references
    static Method method(Object obj) {
        SerializedLambda lambda = serialized(obj);
        Class<?> containingClass = getContainingClass(obj);
        return Arrays.stream(containingClass.getDeclaredMethods())
                .filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
                .findFirst()
                .orElseThrow(MethodFinder.UnableToGuessMethodException::new);
    }

    static SerializedLambda serialized(Object lamda) {
        try {
            Method replaceMethod = lamda.getClass().getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            return (SerializedLambda) replaceMethod.invoke(lamda);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Class<?> getContainingClass(Object obj) {
        try {
            String className = serialized(obj).getImplClass().replaceAll("/", ".");
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    enum ContainerWrapper {
        LIST("list") {
            @Override
            protected Property doWrap(Property property) {
                return new ArrayProperty(property);
            }
        },
        ARRAY("array") {
            @Override
            protected Property doWrap(Property property) {
                return new ArrayProperty(property);
            }
        },
        MAP("map") {
            @Override
            protected Property doWrap(Property property) {
                return new MapProperty(property);
            }
        },
        SET("set") {
            @Override
            protected Property doWrap(Property property) {
                ArrayProperty arrayProperty = new ArrayProperty(property);
                arrayProperty.setUniqueItems(true);
                return arrayProperty;
            }
        };

        private final String container;

        ContainerWrapper(String container) {
            this.container = container;
        }

        public static Property wrapContainer(String container, Property property, ContainerWrapper... allowed) {
            Set<ContainerWrapper> tmp = (allowed.length > 0)
                    ? EnumSet.copyOf(Arrays.asList(allowed))
                    : EnumSet.allOf(ContainerWrapper.class);
            for (ContainerWrapper wrapper : tmp) {
                Property prop = wrapper.wrap(container, property);
                if (prop != null) {
                    return prop;
                }
            }
            return property;
        }

        public Property wrap(String container, Property property) {
            if (this.container.equalsIgnoreCase(container)) {
                return doWrap(property);
            }
            return null;
        }

        protected abstract Property doWrap(Property property);
    }
}
