package smilegate.plop.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smilegate.plop.chat.config.kafka.Producers;
import smilegate.plop.chat.dto.APIMessage;
import smilegate.plop.chat.dto.request.ReqDmDto;
import smilegate.plop.chat.dto.request.ReqGroupDto;
import smilegate.plop.chat.dto.request.ReqInviteDto;
import smilegate.plop.chat.dto.response.RespMyChatRoom;
import smilegate.plop.chat.dto.response.RespRoomDto;
import smilegate.plop.chat.model.jwt.JwtTokenProvider;
import smilegate.plop.chat.service.ChatRoomService;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chatting/room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomMongoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Producers producers;

    private String getTokenToUserId(String jwt){
        return jwtTokenProvider.getUserInfo(jwtTokenProvider.removeBearer(jwt)).getUserId();
    }

    @PostMapping("/v1/dm-creation")
    public ResponseEntity<RespRoomDto> dmCreation(@RequestHeader("Authorization") String jwt, @RequestBody ReqDmDto reqDmDto){
        String userId = getTokenToUserId(jwt);
        reqDmDto.setCreator(userId);
        return new ResponseEntity<>(chatRoomMongoService.createDmRoom(reqDmDto),HttpStatus.CREATED);
    }

    @PostMapping("/v1/group-creation")
    public ResponseEntity<RespRoomDto> groupCreation(@RequestHeader("Authorization") String jwt, @RequestBody ReqGroupDto reqGroupDto){
        String userId = getTokenToUserId(jwt);
        reqGroupDto.setCreator(userId);

        RespRoomDto respRoomDto = chatRoomMongoService.createGroup(reqGroupDto);
        producers.sendRoomMessage(respRoomDto);
        return new ResponseEntity<>(respRoomDto,HttpStatus.CREATED);
    }

    @PostMapping("/v1/invitation")
    public ResponseEntity<APIMessage> groupInvitation(@RequestBody ReqInviteDto reqInviteDto){
        if(chatRoomMongoService.inviteMembers(reqInviteDto)){
            log.info("Error: {}","채팅방이 없거나 멤버가 없음");
            return new ResponseEntity<>(new APIMessage(APIMessage.ResultEnum.success,reqInviteDto),HttpStatus.OK);
        }
        return new ResponseEntity<>(new APIMessage(APIMessage.ResultEnum.failed,reqInviteDto),HttpStatus.OK);
    }

    /**
     * 나의 채팅방 리스트 조회
     */
    @GetMapping("/v1/my-rooms")
    public ResponseEntity<APIMessage> myChatRooms(@RequestHeader("Authorization") String jwt){
        //jwt를 auth 서버를 통해 사용자 id 가져온다.
        String userId = getTokenToUserId(jwt);
        List<RespMyChatRoom> respMyChatRooms = chatRoomMongoService.findMyRoomsByUserId(userId);
        APIMessage apiMessage = new APIMessage();
        apiMessage.setMessage(APIMessage.ResultEnum.success);
        apiMessage.setData(respMyChatRooms);
        return new ResponseEntity<>(apiMessage,HttpStatus.OK);
    }

    /**
     * 채팅방 나가기
     */
    @DeleteMapping("/v1/out/{roomid}")
    public ResponseEntity<APIMessage> outOfTheRoom(@RequestHeader("Authorization") String jwt, @PathVariable(value = "roomid") String roomId){
        String userId = getTokenToUserId(jwt);
        if(chatRoomMongoService.outOfTheRoom(roomId, userId)){
            return new ResponseEntity<>(new APIMessage(APIMessage.ResultEnum.success, new HashMap<String,String>() {{
                put("room_id",roomId);
            }}), HttpStatus.OK);
        }
        return new ResponseEntity<>(new APIMessage(APIMessage.ResultEnum.failed, new HashMap<String,String>() {{
            log.info("채팅방 나가기 실패: roomid: {}, userid: {}", roomId, userId);
            put("room_id",roomId);
        }}), HttpStatus.OK);
    }

    @GetMapping("/v1/info/{roomid}")
    public ResponseEntity<RespRoomDto> chatRoomInfo(@PathVariable(value = "roomid") String roomId){
        return new ResponseEntity<>(chatRoomMongoService.getChatRoomInfo(roomId),HttpStatus.OK);
    }
}
