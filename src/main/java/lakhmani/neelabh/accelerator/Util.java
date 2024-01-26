package lakhmani.neelabh.accelerator;

import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
class Util {
        static void writeFile(Path path, String content) throws Exception {
            Path parentDir = path.getParent();
            if (!parentDir.toFile().exists()) {
                if(parentDir.toFile().mkdirs()){
                    System.out.println(parentDir+" directory created successfully.");
                } else {
                    System.err.println("Failed to create "+parentDir+" directory.");
                    return;  // Exit the method if directory creation fails
                }

            }
            java.nio.file.Files.write(path, content.getBytes());
        }

        public static void zipDirectory(File sourceDirectory, String destinationZipFile) {
            try {
                FileOutputStream fos = new FileOutputStream(destinationZipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                zipDirectoryRecursive(sourceDirectory, sourceDirectory, zos);
                zos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        private static void zipDirectoryRecursive(File rootDir, File source, ZipOutputStream zos) throws IOException {
            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        zipDirectoryRecursive(rootDir, file, zos);
                    } else {
                        String entryName = rootDir.toPath().relativize(file.toPath()).toString();
                        ZipEntry zipEntry = new ZipEntry(entryName);
                        zos.putNextEntry(zipEntry);

                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        }


    }
