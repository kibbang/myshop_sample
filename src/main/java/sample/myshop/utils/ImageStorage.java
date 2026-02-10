package sample.myshop.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageStorage {
    public void store(Long productId, MultipartFile[] files) {

        if (files == null || files.length == 0) {
            return;
        }

        Path directory = Paths.get("./uploads/products/" + productId);

        try {
            Files.createDirectories(directory);

            int nextIndex = findNextIndex(directory); // 기존에 1~3 있으면 새로 4~로 저장

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                Path target = directory.resolve(nextIndex + ".jpg");

                file.transferTo(target);

                nextIndex++;
            }

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    /**
     * 다음 인덱스 채번
     * @param directory
     * @return
     */
    private int findNextIndex(Path directory) {
        int i = 1;
        while (Files.exists(directory.resolve(i + ".jpg"))) {
            i++;
        }
        return i;
    }

    public void clearAll(Long productId) {
        Path directory = Paths.get("./uploads/products/" + productId);
        if (!Files.isDirectory(directory)) return;

        try (var paths = Files.list(directory)) {
            paths.forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) { // 삭제가 안되면 진행 하면 안됨 예외 던지고 끝
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
