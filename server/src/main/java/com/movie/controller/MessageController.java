package com.movie.controller;

import com.movie.entity.Message;
import com.movie.repository.MessageRepository;
import com.movie.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Set<String> VALID_TYPES = new HashSet<>(Arrays.asList(
        "suggestion", "content", "technical", "other"
    ));

    @Autowired
    private MessageRepository messageRepository;

    // 用户获取自己的消息列表
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Message>> getUserMessages(@PathVariable Long userId) {
        List<Message> messages = messageRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ApiResponse.success(messages);
    }

    // 管理员获取所有消息列表
    @GetMapping("/admin/all")
    public ApiResponse<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAllByOrderByCreatedAtDesc();
        return ApiResponse.success(messages);
    }

    // 创建新消息
    @PostMapping
    public ApiResponse<Message> createMessage(@RequestBody Message message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ApiResponse.error("消息内容不能为空");
        }
        if (message.getContent().length() > 1000) {
            return ApiResponse.error("消息内容不能超过1000字符");
        }
        if (message.getUserId() == null) {
            return ApiResponse.error("用户ID不能为空");
        }
        if (message.getType() == null || !VALID_TYPES.contains(message.getType())) {
            return ApiResponse.error("无效的留言类型");
        }
        
        Message savedMessage = messageRepository.save(message);
        return ApiResponse.success(savedMessage);
    }

    // 更新消息状态
    @PutMapping("/{id}/status")
    public ApiResponse<Message> updateMessageStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            return ApiResponse.error("消息不存在");
        }

        if (!status.equals("PENDING") && !status.equals("PROCESSED") && !status.equals("CLOSED")) {
            return ApiResponse.error("无效的状态值");
        }

        Message message = messageOpt.get();
        message.setStatus(status);
        Message updatedMessage = messageRepository.save(message);
        return ApiResponse.success(updatedMessage);
    }

    // 删除消息
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long id) {
        if (!messageRepository.existsById(id)) {
            return ApiResponse.error("消息不存在");
        }
        messageRepository.deleteById(id);
        return ApiResponse.success(null);
    }
} 