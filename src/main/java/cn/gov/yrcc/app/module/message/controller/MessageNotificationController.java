package cn.gov.yrcc.app.module.message.controller;

import cn.gov.yrcc.app.module.message.service.MessageNotificationService;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "消息通知")
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

    @DeleteMapping("message/delete/{id}")
    public BaseResult<Void> deleteDelivery(@PathVariable("id") Long id) {
        messageNotificationService.delete(id);
        return BaseResult.success();
    }
}
