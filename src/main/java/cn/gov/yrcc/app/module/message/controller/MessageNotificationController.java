package cn.gov.yrcc.app.module.message.controller;

import cn.gov.yrcc.app.module.message.service.MessageNotificationService;
import cn.gov.yrcc.utils.base.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class MessageNotificationController {

    private final MessageNotificationService messageNotificationService;

    public MessageNotificationController(MessageNotificationService messageNotificationService) {
        this.messageNotificationService = messageNotificationService;
    }

    @GetMapping("/message/pages/{page}/{size}")
    public BaseResult<Object> messagesDelivery(@PathVariable("page")Integer page,@PathVariable("size")Integer size) {
        return BaseResult.success(messageNotificationService.pages(null, page, size));
    }

    @PutMapping("message/read/{id}")
    public BaseResult<Void> readDelivery(@PathVariable("id") Long id) {
        messageNotificationService.read(id);
        return BaseResult.success();
    }
}
