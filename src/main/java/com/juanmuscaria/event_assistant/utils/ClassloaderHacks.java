package com.juanmuscaria.event_assistant.utils;

import com.google.common.io.ByteStreams;
import com.juanmuscaria.event_assistant.ReflectionAssistant;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.Function;

/**
 * A hack class to call code inside a classloader context which will be appropriately remapped and call the right classes.
 */
public class ClassloaderHacks {
    final ReflectionAssistant.MethodInvoker defineClass = ReflectionAssistant.getMethod(ClassLoader.class,
            "defineClass",
            String.class, byte[].class, int.class, int.class);

    private final ClassLoader context;

    public ClassloaderHacks(ClassLoader cl) {
        context = cl;
    }

    public <T, R> Function<T, R> inject(Function<T, R> function) {
        try {
            Class<?> clazz = injectClass(function.getClass());
            // Creates a new function object inside the classloader
            Constructor<?> c = clazz.getDeclaredConstructors()[0];
            c.setAccessible(true);
            return (Function<T, R>) c.newInstance(new Object[c.getParameterCount()]);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Lambda expressions are not supported!", e);
            // fucking lambda
//            URLClassLoader loader = (URLClassLoader) context;
//            try {
//                SerializedLambda lambda = serialized(function);
//                byte[] bytes = ByteStreams.toByteArray(loader.findResource(lambda.getImplClass().replace('.', '/') + ".class").openStream());
//                Class<?> clazz = injectClass(bytes, lambda.getImplClass());
//                Constructor<?> c = clazz.getDeclaredConstructors()[0];
//                c.setAccessible(true);
//                return (Function<T, R>) c.newInstance(new Object[c.getParameterCount()]);
//            } catch (ReflectiveOperationException | IOException e1) {
//                throw new RuntimeException(e1);
//            }
        }
    }


    public Class<?> injectClass(Class<?> clazz) {
        String name = clazz.getName().replace('.', '/') + ".class";
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(name)) {
            byte[] bytes = ByteStreams.toByteArray(Objects.requireNonNull(in, "Class file not found " + name));
            return (Class<?>) defineClass.invoke(context, clazz.getName(), bytes, 0, bytes.length);
        } catch (NullPointerException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("Error while injecting " + name, e);
        }
    }

    public Class<?> injectClass(byte[] bytes, String name) {
        try {
            return (Class<?>) defineClass.invoke(context, name, bytes, 0, bytes.length);
        } catch (Throwable e) {
            throw new RuntimeException("Error while injecting " + name, e);
        }
    }

}
