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
@Table(name = "datastore")
public class Datastore {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "workspace", nullable = false)
    private String workspace;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, columnDefinition = "datetime default current_timestamp")
    private Date createdAt;
}
