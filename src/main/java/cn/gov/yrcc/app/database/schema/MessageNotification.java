package cn.gov.yrcc.app.database.schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_notification")
public class MessageNotification {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "category_id")
    private Integer categoryId; // 消息类别

    @Column(name = "message")
    private String message;

    @Column(name = "receiver_id")
    private Long receiverId; // 接收者账户ID

    @Column(name = "`read`")
    private boolean read; // 是否已读

    @Column(name = "created_at", nullable = false, columnDefinition = "datetime default current_timestamp")
    private Date createdAt;

    @Column(name = "read_at", columnDefinition = "datetime")
    private Date readAt;

    @Column(name = "deleted")
    private boolean deleted;
}
