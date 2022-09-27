package com.lotte.danuri.messengeron.repository;

import com.lotte.danuri.messengeron.model.dto.Chat;
import com.lotte.danuri.messengeron.model.dto.Message;
import com.lotte.danuri.messengeron.model.dto.RoomData;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class ChatDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Chat> findChatRoomDatas(List<RoomData> roomDataList) {
        List<Chat> chatRoomList = new ArrayList<Chat>();

        for(RoomData roomData : roomDataList) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(roomData.getRoomId()));
            query.fields().slice("messageList",-1);
            chatRoomList.add(mongoTemplate.findOne(query, Chat.class, "chat"));
        }
        return chatRoomList;
    }

    //채팅방 생성 메소드
    public ObjectId createChat(Chat chat) {
        return mongoTemplate.insert(chat, "chat").getChatId();
    }

    //채팅방에 메세지 삽입하는 메소드
    public void pushMessage(ObjectId roomId, Message message) {
        Query query = Query.query(Criteria.where("_id").is(roomId));

        Update updateChat = new Update();
        Update updateMessage = new Update();

        updateChat.set("updateAt", LocalDateTime.now());
        //updateChat.set("lastMessage", message.getContent());
        updateMessage.push("messageList").each(message);

        mongoTemplate.updateFirst(query, updateChat,"chat");
        mongoTemplate.updateFirst(query, updateMessage, "chat");
    }

    //메세지들을 읽어오는 메소드
    public Optional<List<Message>> getMessages(ObjectId roomId){


        Query query = Query.query(Criteria.where("_id").is(roomId));


        Chat chat =  mongoTemplate.findOne(query,Chat.class,"chat");

        Optional<List<Message>> messages = Optional.ofNullable(chat.getMessageList());

        return messages;
    }

    public Optional<List<Message>> getNewMessages(ObjectId roomId){
        Query query = Query.query(Criteria.where("_id").is(roomId));
        query.fields().include("messageList").exclude("_id");
        return Optional.of(mongoTemplate.findOne(query, Chat.class, "chat").getMessageList());
    }


    //방 유효성 확인
    public boolean validChat(ObjectId chatId){
        Query query = Query.query(Criteria.where("_id").is(chatId));
        return mongoTemplate.findOne(query,Chat.class,"chat").isValid();
    }

    //방 비활성화
    public void closeChat(ObjectId roomId){
        Query query = Query.query(Criteria.where("_id").is(roomId));

        Update update = new Update();

        update.set("valid", false);

        mongoTemplate.updateFirst(query, update, "chat");
    }


}
