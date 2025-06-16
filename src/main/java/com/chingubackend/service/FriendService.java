package com.chingubackend.service;

import com.chingubackend.dto.request.FriendRequest;
import com.chingubackend.entity.Friend;
import com.chingubackend.entity.FriendshipScore;
import com.chingubackend.entity.User;
import com.chingubackend.exception.NotFoundException;
import com.chingubackend.exception.SuccessResponse;
import com.chingubackend.model.RequestStatus;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.FriendshipScoreRepository;
import com.chingubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendshipScoreRepository friendshipScoreRepository;
    private final UserRepository userRepository;

    public SuccessResponse sendFriendRequest(Long userId, FriendRequest dto) {
        Long friendId = dto.getFriendId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("요청자 정보가 존재하지 않습니다."));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("친구로 추가하려는 사용자가 존재하지 않습니다."));

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }

        boolean alreadyFriend = friendRepository.existsByUserIdAndFriendIdAndRequestStatus(userId, friendId, RequestStatus.ACCEPTED)
                || friendRepository.existsByUserIdAndFriendIdAndRequestStatus(friendId, userId, RequestStatus.ACCEPTED);
        if (alreadyFriend) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        Optional<Friend> reversePendingRequest = friendRepository.findByUserIdAndFriendIdAndRequestStatus(friendId, userId, RequestStatus.PENDING);
        if (reversePendingRequest.isPresent()) {
            Friend reverseRequest = reversePendingRequest.get();
            reverseRequest.setRequestStatus(RequestStatus.ACCEPTED);
            reverseRequest.setFriendSince(Timestamp.from(Instant.now()));
            friendRepository.save(reverseRequest);

            return SuccessResponse.of("상대방이 먼저 보낸 요청이 있어 자동으로 친구가 되었습니다.");
        }

        Optional<Friend> rejected = friendRepository.findByUserIdAndFriendIdAndRequestStatus(userId, friendId, RequestStatus.REJECTED);
        if (rejected.isPresent()) {
            Friend request = rejected.get();
            request.setRequestStatus(RequestStatus.PENDING);
            request.setFriendSince(Timestamp.from(Instant.now()));
            friendRepository.save(request);

            return SuccessResponse.of("거절 요청이 다시 친구 요청으로 활성화 되었습니다.");
        }

        boolean alreadyRequested = friendRepository.existsByUserIdAndFriendIdAndRequestStatus(userId, friendId, RequestStatus.PENDING);
        if (alreadyRequested) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
        }

        Friend friendRequest = new Friend();
        friendRequest.setUserId(userId);
        friendRequest.setFriendId(friendId);
        friendRequest.setRequestStatus(RequestStatus.PENDING);
        friendRequest.setFriendSince(Timestamp.from(Instant.now()));
        friendRepository.save(friendRequest);

        return SuccessResponse.of("친구 요청이 전송되었습니다.");
    }


    public List<FriendRequest.PendingRequest> getReceivedFriendRequests(Long userId) {
        List<Friend> requests = friendRepository.findPendingRequestsForUser(userId);

        return requests.stream()
                .map(friend -> {
                    String nickname = userRepository.findById(friend.getUserId())
                            .map(user -> user.getNickname())
                            .orElse("Unknown");
                    return new FriendRequest.PendingRequest(friend.getUserId(), nickname, friend.getFriendSince());
                })
                .collect(Collectors.toList());
    }

    public SuccessResponse respondToFriendRequest(Long userId, FriendRequest.ResponseRequest dto) {
        Long friendId = dto.getFriendId();
        String status = dto.getStatus();

        Friend friendRequest = friendRepository
                .findByUserIdAndFriendIdAndRequestStatus(friendId, userId, RequestStatus.PENDING)
                .orElseThrow(() -> new NotFoundException("친구 요청이 존재하지 않습니다."));

        if ("ACCEPTED".equalsIgnoreCase(status)) {
            friendRequest.setRequestStatus(RequestStatus.ACCEPTED);
            friendRequest.setFriendSince(Timestamp.from(Instant.now()));
            friendRepository.save(friendRequest);
            return SuccessResponse.of("친구 요청을 수락했습니다.");
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            friendRequest.setRequestStatus(RequestStatus.REJECTED);
            friendRepository.save(friendRequest);
            return SuccessResponse.of("친구 요청을 거절했습니다.");
        } else {
            throw new IllegalArgumentException("올바르지 않은 응답 상태입니다.");
        }
    }

    public List<FriendRequest.FriendList> getAcceptedFriends(Long userId) {
        List<Friend> accepted = friendRepository.findAllAcceptedFriends(userId);
        List<FriendRequest.FriendList> friends = new ArrayList<>();

        for (Friend friend : accepted) {
            Long otherId = friend.getUserId().equals(userId)
                    ? friend.getFriendId()
                    : friend.getUserId();

            Optional<User> optionalUser = userRepository.findById(otherId);
            if (optionalUser.isEmpty()) continue;

            User user = optionalUser.get();

            Long left = Math.min(userId, otherId);
            Long right = Math.max(userId, otherId);

            int score = friendshipScoreRepository
                    .findByUserIdAndFriendUserId(left, right)
                    .map(FriendshipScore::getScore)
                    .orElse(0);

            friends.add(new FriendRequest.FriendList(
                    otherId,
                    user.getNickname(),
                    user.getName(),
                    score,
                    friend.getFriendSince()
            ));
        }

        return friends;
    }


    public SuccessResponse deleteFriend(Long userId, Long friendUserId) {
        Friend friend = friendRepository.findAcceptedFriend(userId, friendUserId)
                .orElseThrow(() -> new NotFoundException("해당 친구 관계가 존재하지 않습니다."));

        friendRepository.delete(friend);
        return SuccessResponse.of("친구 관계가 삭제되었습니다.");
    }
}
