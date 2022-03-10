package namsic.example.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import namsic.example.querydsl.dto.MemberSearchCondition;
import namsic.example.querydsl.dto.MemberTeamDto;
import namsic.example.querydsl.entity.Member;
import namsic.example.querydsl.entity.Team;
import namsic.example.querydsl.repository.MemberRepository;
import namsic.example.querydsl.repository.MemberRepositorySupport;
import namsic.example.querydsl.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class MemberJpaRepoTest {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	MemberRepositorySupport memberRepositorySupport;
	
	@Autowired
	TeamRepository teamRepository;
	
	@BeforeEach
	public void before() {
		Team teamA = Team.builder().name("teamA").build();
		Team teamB = Team.builder().name("teamB").build();
		
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
	}
	
	@Test
	public void basic() {
		Member member = Member
			.builder()
			.name("member1")
			.age(10)
			.build();
		member = memberRepository.save(member);
		
		Member foundMember = memberRepository.findById(member.getId()).orElseThrow();
		assertThat(foundMember).isEqualTo(member);
		
		List<Member> memberList1 = memberRepository.findAll();
		assertThat(memberList1).containsExactly(member);
		
		List<Member> memberList2 = memberRepositorySupport.findByName("member1");
		assertThat(memberList2).containsExactly(member);
	}
	
	@Test
	public void search() {
		List<MemberTeamDto> result = memberRepositorySupport
			.searchByBuilder(
				MemberSearchCondition.builder()
					.teamName("teamB")
					.ageGoe(25)
					.ageLoe(40)
					.build()
			);
		
		for(MemberTeamDto dto : result) {
			System.out.println(dto);
		}
	}

}
