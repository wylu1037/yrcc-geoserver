package cn.gov.yrcc.app.module.message.repository.impl;

import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.module.message.repository.MessageNotificationRepository;
import cn.gov.yrcc.internal.error.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MessageNotificationRepositoryImpl implements MessageNotificationRepository {

    private final JpaMessageNotification jpaMessageNotification;

    public MessageNotificationRepositoryImpl(JpaMessageNotification jpaMessageNotification) {
        this.jpaMessageNotification = jpaMessageNotification;
    }

    @Override
    public Page<MessageNotification> pages(Long userId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jpaMessageNotification.findAll(pageRequest);
    }

    @Override
    public Long save(MessageNotification messageNotification) {
        messageNotification = jpaMessageNotification.save(messageNotification);
        return messageNotification.getId();
    }

    @Override
    public void update(MessageNotification messageNotification) {
        jpaMessageNotification.saveAndFlush(messageNotification);
    }

    @Override
    public MessageNotification findById(Long id) {
        Optional<MessageNotification> optional = jpaMessageNotification.findById(id);
        if (optional.isEmpty()) {
            throw new BusinessException("消息不存在");
        }
        return optional.get();
    }
}
