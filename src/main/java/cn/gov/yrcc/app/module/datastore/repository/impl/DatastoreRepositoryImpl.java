package cn.gov.yrcc.app.module.datastore.repository.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.module.datastore.repository.DatastoreRepository;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.util.List;
import java.util.Optional;

@Repository
public class DatastoreRepositoryImpl implements DatastoreRepository {

    private final JpaDatastore jpaDatastore;

    public DatastoreRepositoryImpl(JpaDatastore jpaDatastore) {
        this.jpaDatastore = jpaDatastore;
    }

    @Override
    public Long save(Datastore datastore) {
        Datastore save = this.jpaDatastore.save(datastore);
        return save.getId();
    }

    @Override
    public Page<Datastore> pages(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<Datastore> specification = new Specification<>() {
            @Serial
            private static final long serialVersionUID = 7121610700339185854L;

            @Override
            public Predicate toPredicate(
                    @Nonnull Root<Datastore> root,
                    @Nonnull CriteriaQuery<?> query,
                    @Nonnull CriteriaBuilder cb) {
                return cb.equal(root.get("deleted").as(Boolean.class), false);
            }
        };
        return jpaDatastore.findAll(specification, pageRequest);
    }

    @Override
    public Datastore findById(Long id) {
        Optional<Datastore> optional = jpaDatastore.findById(id);
        if (optional.isEmpty()) {
            throw new BusinessException(GSErrorMessage.Datastore.NOT_EXISTS);
        }
        return optional.get();
    }

    @Override
    public void delete(Datastore datastore) {
        jpaDatastore.delete(datastore);
    }

	@Override
	public List<Datastore> findAll() {
		return jpaDatastore.findAll();
	}

	@Override
	public List<Datastore> findByWorkspace(String workspace) {
		Specification<Datastore> specification = new Specification<>() {

			@Serial
			private static final long serialVersionUID = 8370043959902740159L;

			@Override
			public Predicate toPredicate(
				@Nonnull Root<Datastore> root,
				@Nonnull CriteriaQuery<?> query,
				@Nonnull CriteriaBuilder cb) {
				query.orderBy(cb.desc(root.get("createdAt")));
				Predicate predicate = cb.equal(root.get("workspace").as(String.class), workspace);
				return cb.and(predicate, cb.equal(root.get("deleted").as(Boolean.class), false));
			}
		};
		return jpaDatastore.findAll(specification);
	}

	@Override
	public Datastore findByWorkspaceAndName(String workspace, String name) {
		return jpaDatastore.findByWorkspaceAndName(workspace, name);
	}
}
