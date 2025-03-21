package com.chingubackend.service;

import com.chingubackend.dto.request.FriendRequest;
import com.chingubackend.entity.Friend;
import com.chingubackend.model.RequestStatus;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public String sendFriendRequest(FriendRequest dto){
        Long userId = dto.getUserId();
        Long friendId = dto.getFriendId();

        if (userId.equals(friendId)) {
            return "자기 자신에게는 친구 요청을 보낼 수 없습니다.";
        }

        boolean alreadyFriend = friendRepository.existsByUserIdAndFriendIdAndRequestStatus(userId, friendId, RequestStatus.ACCEPTED)
                                || friendRepository.existsByUserIdAndFriendIdAndRequestStatus(friendId, userId, RequestStatus.ACCEPTED);
        if (alreadyFriend){
            return "이미 친구입니다.";
        }

        Optional<Friend> reversePendingRequest = friendRepository.findByUserIdAndFriendIdAndRequestStatus(friendId, userId, RequestStatus.PENDING);
        if(reversePendingRequest.isPresent()){
            Friend reverseRequest = reversePendingRequest.get();
            reverseRequest.setRequestStatus(RequestStatus.ACCEPTED);
            reverseRequest.setFriendSince(Timestamp.from(Instant.now()));
            friendRepository.save(reverseRequest);
            return "상대방이 먼저 보낸 요청이 있어 자동으로 친구가 되었습니다.";
        }

        boolean alreadyRequested = friendRepository.existsByUserIdAndFriendIdAndRequestStatus(userId, friendId, RequestStatus.PENDING);
        if (alreadyRequested){
            return "이미 친구 요청을 보냈습니다.";
        }

        Friend friendRequest = new Friend();
        friendRequest.setUserId(dto.getUserId());
        friendRequest.setFriendId(dto.getFriendId());
        friendRequest.setRequestStatus(RequestStatus.PENDING);
        friendRequest.setFriendSince(Timestamp.from(Instant.now()));
        friendRepository.save(friendRequest);
        return "친구 요청이 전송되었습니다.";
    }


    public List<FriendRequest.PendingRequestDto> getReceivedFriendRequests(Long userId) {
        List<Friend> requests = friendRepository.findPendingRequestsForUser(userId);

        return requests.stream()
                .map(friend -> {
                    String nickname = userRepository.findById(friend.getUserId())
                            .map(user -> user.getNickname())
                            .orElse("Unknown");
                    return new FriendRequest.PendingRequestDto(friend.getUserId(), nickname, friend.getFriendSince());
                })
                .collect(Collectors.toList());
    }

}
