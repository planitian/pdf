package com.example.pdf.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @Author: plani
 * 创建时间: 2019/8/23 10:05
 */
public class FileUtils {

    /**
     * 改变文件权限
     * @param path
     * @throws IOException
     */
    public static void changePermission(Path path) {
        Objects.requireNonNull(path, "参数为空 ");
        if (!Files.exists(path)) {
            return;
        }
        Set<PosixFilePermission> permissions = new HashSet<>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        permissions.add(PosixFilePermission.OWNER_EXECUTE);

        permissions.add(PosixFilePermission.GROUP_READ);
        permissions.add(PosixFilePermission.GROUP_WRITE);
        permissions.add(PosixFilePermission.GROUP_EXECUTE);

        permissions.add(PosixFilePermission.OTHERS_READ);
        permissions.add(PosixFilePermission.OTHERS_WRITE);
        permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        try {
            Files.setPosixFilePermissions(path, permissions);
        } catch (Exception e) {
        }
    }
}
