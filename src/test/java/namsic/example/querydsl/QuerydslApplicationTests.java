package namsic.example.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import namsic.example.querydsl.entity.QTest;
import namsic.example.querydsl.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@Commit
@Transactional
@SpringBootTest
class QuerydslApplicationTests {
	
	@Autowired
	EntityManager entityManager;
	
	@org.junit.jupiter.api.Test
	@Rollback(value = false)
	void contextLoads() {
		Test test = new Test();
		entityManager.persist(test);
		
		JPAQueryFactory query = new JPAQueryFactory(entityManager);
		QTest qTestEntity = QTest.test;
		
		Test result = query
			.selectFrom(qTestEntity)
			.fetchOne();
		
		assertThat(result).isEqualTo(test);
		
		assert result != null;
		assertThat(result.getId()).isEqualTo(test.getId());
	}

}
