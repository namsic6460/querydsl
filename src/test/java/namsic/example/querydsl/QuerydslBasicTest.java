package namsic.example.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import namsic.example.querydsl.entity.Member;
import namsic.example.querydsl.entity.QMember;
import namsic.example.querydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static namsic.example.querydsl.entity.QMember.member;
import static namsic.example.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class QuerydslBasicTest {
	
	@Autowired
	EntityManager entityManager;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	JPAQueryFactory queryFactory;
	
	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(entityManager);
		
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
	}
	
	@Test
	public void startJPQL() {
		String queryStr = "SELECT m from Member m WHERE m.name = :name";
		Member foundByJPQL = entityManager.createQuery(queryStr, Member.class)
			.setParameter("name", "member1")
			.getSingleResult();
		
		assertThat(foundByJPQL.getName()).isEqualTo("member1");
	}
	
	@Test
	public void startQuerydsl() {
		Member foundMember = queryFactory
			.select(member)
			.from(member)
			.where(member.name.eq("member1"))
			.fetchOne();
		
		assert foundMember != null;
		assertThat(foundMember.getName()).isEqualTo("member1");
	}
	
	@Test
	public void search() {
		Member foundMember = queryFactory
			.selectFrom(member)
			.where(member.name.eq("member1")
				.and(member.age.between(10, 30)))
			.fetchOne();
		
		assert foundMember != null;
		assertThat(foundMember.getName()).isEqualTo("member1");
	}
	
	@Test
	public void searchAndParam() {
		Member foundMember = queryFactory
			.selectFrom(member)
			.where(
				member.name.eq("member1"),
				member.age.between(10, 30)
			)
			.fetchOne();
		
		assert foundMember != null;
		assertThat(foundMember.getName()).isEqualTo("member1");
	}
	
	@Test
	public void resultFetch() {
//		List<Member> fetch = queryFactory
//			.selectFrom(member)
//			.fetch();
//
//		Member fetchOne = queryFactory
//			.selectFrom(member)
//			.fetchOne();
//
//		Member fetchFirst = queryFactory
//			.selectFrom(QMember.member)
//			.fetchFirst();
//
//		QueryResults<Member> results = queryFactory
//			.selectFrom(member)
//			.fetchResults();
		
		long count = queryFactory
			.selectFrom(member)
			.stream()
			.count();
		
		assertThat(count).isEqualTo(4);
	}
	
	@Test
	public void sort() {
		entityManager.persist(
			Member.builder().age(100).build()
		);
		entityManager.persist(
			Member.builder().name("member5").age(100).build()
		);
		entityManager.persist(
			Member.builder().name("member6").age(100).build()
		);
		
		List<Member> result = queryFactory
			.selectFrom(member)
			.where(member.age.eq(100))
			.orderBy(member.age.desc(), member.name.asc().nullsLast())
			.fetch();
		
		assertThat(result.get(0).getName()).isEqualTo("member5");
		assertThat(result.get(1).getName()).isEqualTo("member6");
		assertThat(result.get(2).getName()).isNull();
	}
	
	@Test
	public void paging1() {
		List<Member> result = queryFactory
			.selectFrom(member)
			.orderBy(member.name.desc())
			.offset(1)
			.limit(2)
			.fetch();
		
		assertThat(result.size()).isEqualTo(2);
	}
	
//	@Test
//	public void paging2() {
//		QueryResults<Member> result = queryFactory
//			.selectFrom(member)
//			.orderBy(member.name.desc())
//			.offset(1)
//			.limit(2)
//			.fetchResults();
//
//		assertThat(result.getTotal()).isEqualTo(4);
//		assertThat(result.getLimit()).isEqualTo(2);
//		assertThat(result.getOffset()).isEqualTo(1);
//		assertThat(result.getResults().size()).isEqualTo(2);
//	}
	
	@Test
	public void aggregation() {
		Tuple result = queryFactory
			.select(
				member.count(),
				member.age.sum(),
				member.age.avg(),
				member.age.max(),
				member.age.min()
			)
			.from(member)
			.fetchOne();
		
		assert result != null;
		assertThat(result.get(member.count())).isEqualTo(4);
		assertThat(result.get(member.age.sum())).isEqualTo(100);
		assertThat(result.get(member.age.avg())).isEqualTo(25);
		assertThat(result.get(member.age.max())).isEqualTo(40);
		assertThat(result.get(member.age.min())).isEqualTo(10);
	}
	
	@Test
	public void group() {
		List<Tuple> result = queryFactory
			.select(team.name, member.age.avg())
			.from(member)
			.join(member.team, team)
			.groupBy(team.name)
			.fetch();
		
		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);
		
		assertThat(teamA.get(team.name)).isEqualTo("teamA");
		assertThat(teamA.get(member.age.avg())).isEqualTo(15);
		
		assertThat(teamB.get(team.name)).isEqualTo("teamB");
		assertThat(teamB.get(member.age.avg())).isEqualTo(35);
	}
	
	@Test
	public void join() {
		List<Member> result = queryFactory
			.selectFrom(member)
			.join(member.team, team)
			.where(team.name.eq("teamA"))
			.fetch();
		
		assertThat(result)
			.extracting("name")
			.containsExactly("member1", "member2");
	}
	
	@Test
	public void thetaJoin() {
		entityManager.persist(Member.builder().name("teamA").build());
		entityManager.persist(Member.builder().name("teamB").build());
		entityManager.persist(Member.builder().name("teamC").build());
		
		List<Member> result = queryFactory
			.select(member)
			.from(member, team)
			.where(member.name.eq(team.name))
			.fetch();
		
		assertThat(result)
			.extracting("name")
			.containsExactly("teamA", "teamB");
	}
	
	@Test
	public void joinOnFiltering() {
		List<Tuple> result = queryFactory
			.select(member, team)
			.from(member)
			.leftJoin(member.team, team).on(team.name.eq("teamA"))
			.fetch();
		
		for(Tuple tuple : result) {
			System.out.println("tuple: " + tuple);
		}
	}
	
	@Test
	public void joinOnNonRelation() {
		entityManager.persist(Member.builder().name("teamA").build());
		entityManager.persist(Member.builder().name("teamB").build());
		entityManager.persist(Member.builder().name("teamC").build());
		
		List<Tuple> result = queryFactory
			.select(member, team)
			.from(member)
			.leftJoin(team).on(member.name.eq(team.name))
			.fetch();
		
		for(Tuple tuple : result) {
			System.out.println("tuple: " + tuple);
		}
	}
	
	@Test
	public void fetchJoin() {
		entityManager.flush();
		entityManager.clear();
		
		Member foundMember = queryFactory
			.selectFrom(QMember.member)
			.join(member.team, team).fetchJoin()
			.where(QMember.member.name.eq("member1"))
			.fetchOne();
		
		assert foundMember != null;
		boolean loaded = entityManagerFactory.getPersistenceUnitUtil().isLoaded(foundMember.getTeam());
		assertThat(loaded).isTrue();
	}
	
	@Test
	public void subQuery() {
		QMember subMember = new QMember("subMember");
		List<Member> result = queryFactory
			.selectFrom(member)
			.where(member.age.goe(
				select(subMember.age.avg())
					.from(subMember)
			))
			.fetch();
		
		assertThat(result)
			.extracting("age")
			.containsExactly(30, 40);
	}
	
	@Test
	public void subQueryIn() {
		QMember subMember = new QMember("subMember");
		List<Member> result = queryFactory
			.selectFrom(member)
			.where(member.age.in(
				select(subMember.age)
					.from(subMember)
					.where(subMember.age.gt(10))
			))
			.fetch();
		
		assertThat(result)
			.extracting("age")
			.containsExactly(30, 40);
	}
	
	@Test
	public void selectSubQuery() {
		QMember subMember = new QMember("subMember");
		List<Tuple> result = queryFactory
			.select(
				member.name,
				select(subMember.age.avg())
					.from(subMember)
			)
			.from()
			.fetch();
		
		for(Tuple tuple : result) {
			System.out.println("tuple: " + tuple);
		}
	}
	
}
