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
@Table(name = "layer")
public class Layer {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "title")
	private String title;

	@Column(name = "workspace")
	private String workspace;

	@Column(name = "datastore")
	private String datastore;

	@Column(name = "type")
	private String type;

	@Column(name = "status")
	private String status;

	@Column(name = "enable")
	private Boolean enable;

	@Column(name = "created_at", nullable = false, columnDefinition = "datetime default current_timestamp")
	private Date createdAt;

	@Column(name = "deleted", nullable = false)
	private Boolean deleted;
}
