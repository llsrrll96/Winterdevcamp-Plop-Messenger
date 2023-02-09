package smilegate.plop.presence.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smilegate.plop.presence.config.kafka.PresenceMessage;
import smilegate.plop.presence.config.kafka.Producer;
import smilegate.plop.presence.dto.PresenceUserDto;
import smilegate.plop.presence.dto.response.ResponsePresenceUsers;
import smilegate.plop.presence.model.jwt.JwtTokenProvider;
import smilegate.plop.presence.service.FriendService;
import smilegate.plop.presence.service.PresenceService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final JwtTokenProvider jwtTokenProvider;
    private final PresenceService presenceService;
    private final FriendService friendService;
    private final Producer producer;

    private String getTokenToUserId(String jwt){
        return jwtTokenProvider.getUserInfo(jwtTokenProvider.removeBearer(jwt)).getUserId();
    }

    @GetMapping("/v1/users")
    public ResponseEntity<ResponsePresenceUsers> presenceUsers(@RequestHeader("Authorization") String jwt){
        /**
         * 유저서버를 통해 친구리스트API 요청
          */
        //List<String> f = friendService.getMyFriends(jwt);
        List<String> friends = new ArrayList<>();
        friends.add("imuser2"); friends.add("imuser3"); friends.add("imuser4");
        if(friends.isEmpty()) return new ResponseEntity<>(new ResponsePresenceUsers(), HttpStatus.OK);
        return new ResponseEntity<>(presenceService.getUsersPresence(friends),HttpStatus.OK);
    }

    @PutMapping("/v1/on")
    public ResponseEntity<String> sendPresenceOn(@RequestHeader("Authorization") String jwt){
        String userId = getTokenToUserId(jwt);
        PresenceUserDto presenceUserDto = presenceService.presenceOn(userId);

        //List<String> f = friendService.getMyFriends(jwt);
        List<String> friends = new ArrayList<>();
        friends.add("imuser2"); friends.add("imuser3"); friends.add("imuser4");

        PresenceMessage presenceMessage = PresenceMessage.builder()
                        .user_id(presenceUserDto.getUser_id())
                        .status(presenceUserDto.getStatus())
                        .friends(friends)
                        .build();

        producer.sendMessage(presenceMessage);

        return ResponseEntity.ok("success");
    }

    @PutMapping("/v1/off")
    public ResponseEntity<String> sendPresenceOff(@RequestHeader("Authorization") String jwt){
        String userId = getTokenToUserId(jwt);
        PresenceUserDto presenceUserDto = presenceService.presenceOff(userId);

        //List<String> f = friendService.getMyFriends(jwt);
        List<String> friends = new ArrayList<>();
        friends.add("imuser2"); friends.add("imuser3"); friends.add("imuser4");
        PresenceMessage presenceMessage = PresenceMessage.builder()
                .user_id(presenceUserDto.getUser_id())
                .status(presenceUserDto.getStatus())
                .friends(friends)
                .build();

        producer.sendMessage(presenceMessage);

        return ResponseEntity.ok("success");
    }
}
