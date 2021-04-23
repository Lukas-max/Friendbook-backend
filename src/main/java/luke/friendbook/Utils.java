package luke.friendbook;

import luke.friendbook.mainFeed.MainFeedController;
import luke.friendbook.storage.FileController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;

public interface Utils {

    static String createFileTypeFromMimeType(String mimeType) {
        return mimeType.split("/")[0];
    }

    static String createFeedFileUrl(String methodName, Long feedId, String fileName){
        return MvcUriComponentsBuilder
                .fromMethodName(MainFeedController.class, methodName, feedId.toString(), fileName)
                .build()
                .toString();
    }

    static String createStorageFileUrl(String methodName, String user, String dir, Path file) {
        return MvcUriComponentsBuilder
                .fromMethodName(FileController.class, methodName, user, dir, file.getFileName().toString())
                .build().toString();
    }
}
