package namsic.example.querydsl.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

@Commit
@Transactional
@SpringBootTest
class MemberTest {
	
	@Autowired
	EntityManager entityManager;
	
	@Test
	@Rollback
	public void testEntity() {
		Team teamA = Team.builder().name("teamA").build();
		Team teamB = Team.builder().name("teamB").build();
		
		entityManager.persist(teamA);
		entityManager.persist(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		
		entityManager.persist(member1);
		entityManager.persist(member2);
		entityManager.persist(member3);
		entityManager.persist(member4);
		
		entityManager.flush();
		entityManager.clear();
		
		List<Member> members = entityManager
			.createQuery("select m from Member m", Member.class)
			.getResultList();
		
		for(Member member : members) {
			System.out.println("member: " + member);
			System.out.println("-> member.team: " + member.getTeam());
		}
	}
	
}
