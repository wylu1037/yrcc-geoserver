package cn.gov.yrcc.app.module.message.repository.impl;

import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.module.message.repository.MessageNotificationRepository;
import cn.gov.yrcc.internal.error.BusinessException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.io.Serial;
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
        Specification<MessageNotification> specification = new Specification<>() {
            @Serial
            private static final long serialVersionUID = 2968389488155384114L;

            @Override
            public Predicate toPredicate(
                    @Nonnull Root<MessageNotification> root,
                    @Nonnull CriteriaQuery<?> query,
                    @Nonnull CriteriaBuilder cb) {
                return cb.equal(root.get("deleted").as(Boolean.class), false);
            }
        };
        return jpaMessageNotification.findAll(specification, pageRequest);
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
