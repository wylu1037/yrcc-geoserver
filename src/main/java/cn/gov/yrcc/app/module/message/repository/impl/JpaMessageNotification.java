package cn.gov.yrcc.app.module.message.repository.impl;

import cn.gov.yrcc.app.database.schema.MessageNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMessageNotification extends PagingAndSortingRepository<MessageNotification, Long>,
        JpaRepository<MessageNotification, Long>, JpaSpecificationExecutor<MessageNotification> {

}
