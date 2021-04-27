package luke.friendbook.utilities;

import luke.friendbook.mainFeed.MainFeedController;
import luke.friendbook.mainFeed.model.FeedModel;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.security.model.SecurityContextUser;
import luke.friendbook.storage.FileController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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

    static SecurityContextUser getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityContextUser) auth.getPrincipal();
    }

    static List<FeedModelDto> returnFeedModelDto(List<FeedModel> feedModels) {
        return feedModels.stream()
                .map(model -> {
                    return new FeedModelDto(
                            model.getId(),
                            model.getText(),
                            model.getFiles(),
                            model.getImages(),
                            model.getFeedTimestamp(),
                            model.getUser().getUsername(),
                            model.getUser().getUserUUID()
                    );
                }).collect(Collectors.toList());
    }
}
