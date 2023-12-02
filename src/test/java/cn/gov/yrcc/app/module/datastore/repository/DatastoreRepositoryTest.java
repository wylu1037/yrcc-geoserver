package cn.gov.yrcc.app.module.datastore.repository;

import cn.gov.yrcc.app.database.schema.Datastore;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class DatastoreRepositoryTest {

	@Resource
	private DatastoreRepository datastoreRepository;

	@Test
	public void test() {
		Datastore datastore = datastoreRepository.findByWorkspaceAndName("ne", "luwenyang13");
		Assertions.assertNotNull(datastore);
	}
}
