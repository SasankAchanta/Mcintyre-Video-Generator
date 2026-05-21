package org.mcintyrelab.service.impl;

import org.mcintyrelab.dto.UntrainedResponse;
import org.mcintyrelab.repository.UntrainedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UntrainedService {
    private final UntrainedRepository untrainedRepository;
    private final UserServiceImpl userServiceImpl;

    public UntrainedService(UntrainedRepository untrainedRepository, UserServiceImpl userServiceImpl) {
        this.untrainedRepository = untrainedRepository;
        this.userServiceImpl = userServiceImpl;
    }

//    public void createUntrained(CreateUntrainedRequest createUntrainedRequest) {
//        List<Untrained> untrainedList = new ArrayList<>();
//        User currentUser = userService.getUserById(createUntrainedRequest.userId());
//
//        for (String url : createUntrainedRequest.videoUrl()) {
//            if (url == null) {
//                continue;
//            }
//            if (url.isBlank()) {
//                continue;
//            }
//            if (untrainedRepository.existsByVideoUrl((url))) {
//                continue;
//            }
//            Untrained untrained = Untrained.builder()
//                    .videoUrl(url)
//                    .user(currentUser)
//                    .isTrained(false)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//            untrainedList.add(untrained);
//        }
//        if (untrainedList.isEmpty()) {
//            return;
//        }
//        untrainedRepository.saveAll(untrainedList);
//    }

    public List<UntrainedResponse> getAllUploads(Integer userId) {
        return untrainedRepository.findByUserUserId(userId);
    }
}
