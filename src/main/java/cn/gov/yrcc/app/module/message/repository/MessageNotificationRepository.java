package cn.gov.yrcc.app.module.message.repository;

import cn.gov.yrcc.app.database.schema.MessageNotification;
import org.springframework.data.domain.Page;

public interface MessageNotificationRepository {

    Page<MessageNotification> pages(Long userId, Integer page, Integer size);

    Long save(MessageNotification messageNotification);

    void update(MessageNotification messageNotification);

    MessageNotification findById(Long id);
}
