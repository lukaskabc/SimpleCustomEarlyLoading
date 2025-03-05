package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

/**
 * Allows reflection access to private fields and methods.
 * @implNote Using {@link MethodHandle} should be more performant than using {@link java.lang.reflect} api.
 */
public class ReflectionAccessor {
    private ReflectionAccessor() {
        throw new AssertionError();
    }

    protected static MethodHandles.Lookup privateLookup(final Class<?> targetClass) {
        try {
            return MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    protected static MethodHandle findVirtual(final MethodHandles.Lookup privateLookup, final String methodName, final Class<?> returnType, final Class<?>... parameterTypes) {
        try {
            return privateLookup.findVirtual(privateLookup.lookupClass(), methodName, MethodType.methodType(returnType, parameterTypes));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    protected static MethodHandle findConstructor(final MethodHandles.Lookup privateLookup, final Class<?>... parameterTypes) {
        try {
            return privateLookup.findConstructor(privateLookup.lookupClass(), MethodType.methodType(void.class, parameterTypes));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    protected static MethodHandle findGetter(final MethodHandles.Lookup privateLookup, String fieldName, Class<?> fieldType) {
        try {
            return privateLookup.findGetter(privateLookup.lookupClass(), fieldName, fieldType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }

    }

    protected static VarHandle findField(final MethodHandles.Lookup privateLookup, String fieldName, Class<?> fieldType) {
        try {
            return privateLookup.findVarHandle(privateLookup.lookupClass(), fieldName, fieldType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    protected static VarHandle findStaticField(final MethodHandles.Lookup privateLookup, String fieldName, Class<?> fieldType) {
        try {
            return privateLookup.findStaticVarHandle(privateLookup.lookupClass(), fieldName, fieldType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    public static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }
}
