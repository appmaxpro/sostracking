/*
 * Copyright 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class ClassScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);
    private ClassScanner() {
    }

    public static List<Class<?>> findSubclasses(
            Class<?> baseClass) throws IOException, URISyntaxException, ReflectiveOperationException {
        return findSubclasses(baseClass, baseClass.getPackageName());
    }

    public static List<Class<?>> findSubclasses(Class<?> baseClass, String packageName)
            throws IOException, URISyntaxException, ReflectiveOperationException {

        final List<String> names = new LinkedList<>();
        String packagePath = packageName.replace('.', '/');
        URL packageUrl = baseClass.getClassLoader().getResource(packagePath);

        if (packageUrl.getProtocol().equals("jar")) {
            String jarFileName = URLDecoder.decode(packageUrl.getFile(), StandardCharsets.UTF_8);
            try (JarFile jf = new JarFile(jarFileName.substring(5, jarFileName.indexOf("!")))) {
                Enumeration<JarEntry> jarEntries = jf.entries();
                while (jarEntries.hasMoreElements()) {
                    var jarEntry = jarEntries.nextElement();
                    if (!jarEntry.isDirectory()) {
                        String entryName = jarEntry.getName();
                        if (entryName.startsWith(packagePath) && entryName.length() > packagePath.length() + 5) {
                            names.add(entryName.substring(packagePath.length() + 1, entryName.lastIndexOf('.')));
                        }
                    }
                }
            }
        } else {
            //ClassLoader.getSystemResource(
            Path relative = Paths.get(new URI(packageUrl.toString()));
            try (Stream<Path> stream = Files.walk(Paths.get(new URI(packageUrl.toString())))) {
                stream.filter(Files::isRegularFile)
                   .forEach( actual -> {
                       LOGGER.info("Try load entity: {}", relative.relativize(actual));

                     String entryName = relative.relativize(actual).toString()
                             .replace('\\', '.')
                             .replace('/', '.');
                     names.add(entryName.substring(0, entryName.lastIndexOf('.')));
                });
            }

            /*
            File folder = new File(new URI(packageUrl.toString()));
            File[] files = folder.listFiles();

            if (files != null) {
                for (File actual: files) {
                    if (actual.isFile()) {
                        String entryName = actual.getName();
                        LOGGER.info("Try load entity: {}", entryName);

                        names.add(entryName.substring(0, entryName.lastIndexOf('.')));
                    }
                }
            }

             */
        }

        var classes = new LinkedList<Class<?>>();
        for (String name : names) {
            var clazz = Class.forName(packageName + '.' + name);
            if (baseClass.isAssignableFrom(clazz)) {
                classes.add(clazz);
            }
        }
        return classes;
    }

}
