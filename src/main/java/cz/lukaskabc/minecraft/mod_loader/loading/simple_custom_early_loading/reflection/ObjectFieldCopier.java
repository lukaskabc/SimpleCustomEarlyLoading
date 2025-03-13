package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.privateLookup;

public class ObjectFieldCopier {

    public static void copyAllFields(Object source, Object target, Class<?> clazz) {
        // Validate the objects
        if (source == null || target == null) {
            throw new NullPointerException("Source and target objects cannot be null");
        }

        // Get all fields from the source class and its superclasses
        Set<Field> sourceFields = getAllFields(source.getClass());
        Set<Field> targetFields = getAllFields(target.getClass());

        final MethodHandles.Lookup privateLookup = privateLookup(clazz);

        // Iterate through all fields from the source
        for (Field sourceField : sourceFields) {
            // Find the corresponding field in the target class
            Field targetField = findCorrespondingField(sourceField, targetFields);

            if (targetField != null && areFieldsCompatible(sourceField, targetField)) {
                copyField(source, target, sourceField, targetField, privateLookup);
            }
        }
    }

    private static Set<Field> getAllFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();

        // Add all fields from the class and its superclasses
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    private static Field findCorrespondingField(Field sourceField, Set<Field> targetFields) {
        for (Field targetField : targetFields) {
            if (targetField.getName().equals(sourceField.getName())) {
                return targetField;
            }
        }
        return null;
    }

    private static boolean areFieldsCompatible(Field sourceField, Field targetField) {
        return targetField.getType().isAssignableFrom(sourceField.getType());
    }

    private static void copyField(Object source, Object target, Field sourceField, Field targetField, MethodHandles.Lookup privateLookup) {
        if (Modifier.isFinal(targetField.getModifiers()))
            return;
        try {
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            targetField.set(target, sourceField.get(source));
        } catch (Exception e) {
            System.err.println("Error copying field: " + sourceField.getName() + " - " + e.getMessage());
            throw new ReflectionException(e);
        }
    }
}