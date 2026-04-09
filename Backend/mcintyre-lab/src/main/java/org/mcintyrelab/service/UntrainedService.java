package org.mcintyrelab.service;

import org.mcintyrelab.dto.CreateUntrainedRequest;
import org.mcintyrelab.dto.UntrainedResponse;
import org.mcintyrelab.model.Untrained;
import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UntrainedRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UntrainedService {
    private final UntrainedRepository untrainedRepository;
    private final UserService userService;

    public UntrainedService(UntrainedRepository untrainedRepository, UserService userService) {
        this.untrainedRepository = untrainedRepository;
        this.userService = userService;
    }

    public void createUntrained(CreateUntrainedRequest createUntrainedRequest) {
        List<Untrained> untrainedList = new ArrayList<>();
        User currentUser = userService.getUserById(createUntrainedRequest.userId());

        for (String url : createUntrainedRequest.videoUrl()) {
            if (url == null) {
                continue;
            }
            if (url.isBlank()) {
                continue;
            }
            if (untrainedRepository.existsByVideoUrl((url))) {
                continue;
            }
            Untrained untrained = Untrained.builder()
                    .videoUrl(url)
                    .user(currentUser)
                    .isTrained(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            untrainedList.add(untrained);
        }
        if (untrainedList.isEmpty()) {
            return;
        }
        untrainedRepository.saveAll(untrainedList);
    }

    public List<UntrainedResponse> getAllUploads(Integer userId) {
        return untrainedRepository.findByUserUserId(userId);
    }
}
