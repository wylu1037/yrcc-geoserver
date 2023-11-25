package cn.gov.yrcc.app.module.message.service.impl;

import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.module.message.repository.MessageNotificationRepository;
import cn.gov.yrcc.app.module.message.service.MessageNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class MessageNotificationServiceImpl implements MessageNotificationService {

    private final MessageNotificationRepository messageNotificationRepository;

    public MessageNotificationServiceImpl(MessageNotificationRepository messageNotificationRepository) {
        this.messageNotificationRepository = messageNotificationRepository;
    }

    @Override
    public Page<MessageNotification> pages(Long userId, Integer page, Integer size) {
        return messageNotificationRepository.pages(userId, page, size);
    }

    @Override
    public void read(Long id) {
        MessageNotification messageNotification = messageNotificationRepository.findById(id);
        messageNotification.setRead(true);
        messageNotificationRepository.save(messageNotification);
    }
}
