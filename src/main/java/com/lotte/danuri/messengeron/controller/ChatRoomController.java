package com.lotte.danuri.messengeron.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotte.danuri.messengeron.model.dto.Chat;
import com.lotte.danuri.messengeron.model.vo.ChatVo;
import com.lotte.danuri.messengeron.model.vo.ChatsVo;
import com.lotte.danuri.messengeron.service.ChatRoomService;
import com.lotte.danuri.messengeron.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("chatRoom")
public class ChatRoomController {

    @Autowired
    UserService userService;
    @Autowired
    ChatRoomService chatRoomService;


    @PostMapping("chat")
    @ApiOperation(value = "sendChat")
    ResponseEntity pushChat(@RequestBody ChatVo vo) throws FirebaseMessagingException {
        chatRoomService.pushChat(vo.getChatRoomId(), vo.getSendTo(), vo.getChat());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("chats")
    @ApiOperation(value = "sendChats")
    ResponseEntity pushChats(@RequestBody ChatsVo vo) throws FirebaseMessagingException {
        chatRoomService.pushChats(vo.getChatRoomIds(), vo.getChat());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("chats/{userId}/{roomId}")
    @ApiOperation(value = "getChats")
    ResponseEntity<List<Chat>> getChats(@PathVariable String userId, @PathVariable String roomId) {

        return new ResponseEntity<>(chatRoomService.getChats(userId, new ObjectId(roomId)), HttpStatus.OK);
    }

    @GetMapping("newChats/{userId}/{roomId}")
    @ApiOperation(value = "getNewChats")
    ResponseEntity<List<Chat>> getNewChats(@PathVariable String userId, @PathVariable String roomId) {
        return new ResponseEntity<>(chatRoomService.getNewChats(userId, new ObjectId(roomId)), HttpStatus.OK);
    }

    @PostMapping("image")
    @ApiOperation(value = "postImage")
    ResponseEntity pushImage(@RequestPart ChatVo chatVo, @RequestPart MultipartFile imageFile) throws FirebaseMessagingException {
        Chat chat = chatVo.getChat();
        chat.setSource(chatRoomService.pushImage(imageFile));
        chatRoomService.pushChat(chatVo.getChatRoomId(),chatVo.getSendTo(),chat);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
