package smilegate.plop.push.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import smilegate.plop.push.domain.UserEntity;
import smilegate.plop.push.domain.UserRepository;
import smilegate.plop.push.dto.request.RequestMessage;
import smilegate.plop.push.dto.response.ResponseUser;
import smilegate.plop.push.exception.PushException;
import smilegate.plop.push.exception.PushFormatException;
import smilegate.plop.push.exception.UserNotFoundException;
import smilegate.plop.push.model.JwtUser;
import smilegate.plop.push.security.JwtTokenProvider;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {
    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    @Value("${fcm.key.scope")
    private String firebaseScope;

    UserRepository userRepository;
    JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .createScoped(List.of(firebaseScope)))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    @Autowired
    public NotificationService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }
//비동기 처리?
    // data 에 객체 형식으로 다 넣어야 하는 지
    public void sendByTokenList(RequestMessage message) {
        List<String> tokenList = new ArrayList<>();
        message.getTarget().forEach(target -> {
            UserEntity userEntity = userRepository.findByUserIdOrEmail(target,target);
            String token = userEntity.getFcmToken();
            log.error(userEntity.getUserId() + " : " +token);
            tokenList.add(token);
        });
        List<Message> messages;
        try {
            messages = tokenList.stream().map(token-> Message.builder()
                    .putData("time", LocalDateTime.now().toString())
                    .putData("title", message.getTitle())
                    .putData("body", message.getBody())
                    .putData("roomId", message.getRoomId())
                    .setNotification(new Notification(message.getTitle(),message.getBody()))
                    .setToken(token)
                    .build()).collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new PushFormatException("요청 데이터가 유효하지 않습니다.");
        }

//        MulticastMessage.builder().addAllTokens(tokenList).putData()
        BatchResponse response;
        try {
            //알림 발송
            response = FirebaseMessaging.getInstance().sendAll(messages);
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i< responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                    log.error(responses.get(i).getMessageId());
                }
                if (!failedTokens.isEmpty()) {
                    log.error("List of tokens are not valid FCM token : " + failedTokens);
                    throw new PushException("List of tokens are not valid FCM token : " + failedTokens);
                }
            }


        } catch (FirebaseMessagingException e ) {
            log.error("can not send to memberList push message. error info : {}", e.getMessage());
            throw new PushException("can not send to memberList push message. error info : " + e.getMessage());
        }
    }

    public ResponseUser registerFcmToken(String jwt, String tokenId) {
        JwtUser sender = jwtTokenProvider.getUserInfo(jwt);
        UserEntity userEntity = userRepository.findByUserId(sender.getUserId());
        if (userEntity == null)
            throw new UserNotFoundException(String.format("[%s] is Not Found", userEntity.getUserId()));
        userEntity.setFcmToken(tokenId);
        userRepository.save(userEntity);

        return new ResponseUser(
                userEntity.getEmail(),userEntity.getProfile().get("nickname").toString(),userEntity.getUserId());
    }
}

