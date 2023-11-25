package cn.gov.yrcc.app.module.message.service;

import cn.gov.yrcc.app.database.schema.MessageNotification;
import org.springframework.data.domain.Page;

public interface MessageNotificationService {

    Page<MessageNotification> pages(Long userId, Integer page, Integer size);

    void  read(Long id);
}
