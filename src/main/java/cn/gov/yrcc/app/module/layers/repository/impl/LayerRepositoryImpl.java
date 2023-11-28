package cn.gov.yrcc.app.module.layers.repository.impl;

import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.module.layers.repository.LayerRepository;
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

@Repository
public class LayerRepositoryImpl implements LayerRepository {

	private final JpaLayer jpaLayer;

	public LayerRepositoryImpl(JpaLayer jpaLayer) {
		this.jpaLayer = jpaLayer;
	}

	@Override
	public Long save(Layer layer) {
		return jpaLayer.save(layer).getId();
	}

	@Override
	public Layer findByNameAndWorkspace(String name, String workspace) {
		return jpaLayer.findByNameAndWorkspace(name, workspace);
	}

	@Override
	public Page<Layer> pages(Integer page, Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Specification<Layer> specification = new Specification<>() {
			@Serial
			private static final long serialVersionUID = -996273007729683967L;

			@Override
			public Predicate toPredicate(
				@Nonnull Root<Layer> root,
				@Nonnull CriteriaQuery<?> query,
				@Nonnull CriteriaBuilder cb) {
				return cb.equal(root.get("deleted").as(Boolean.class), false);
			}
		};
		return jpaLayer.findAll(specification, pageRequest);
	}
}
