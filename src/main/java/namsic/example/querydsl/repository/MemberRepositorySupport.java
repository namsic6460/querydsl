package namsic.example.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import namsic.example.querydsl.dto.MemberSearchCondition;
import namsic.example.querydsl.dto.MemberTeamDto;
import namsic.example.querydsl.dto.QMemberTeamDto;
import namsic.example.querydsl.entity.Member;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.*;
import static namsic.example.querydsl.entity.QMember.member;
import static namsic.example.querydsl.entity.QTeam.team;

@Repository
public class MemberRepositorySupport extends QuerydslRepositorySupport {
	
	private final JPAQueryFactory queryFactory;
	
	public MemberRepositorySupport(JPAQueryFactory jpaQueryFactory) {
		super(Member.class);
		this.queryFactory = jpaQueryFactory;
	}
	
	public List<Member> findByName(@NonNull String name) {
		return queryFactory
			.selectFrom(member)
			.where(member.name.eq(name))
			.fetch();
	}
	
	public List<MemberTeamDto> searchByBuilder(@NonNull MemberSearchCondition condition) {
		return queryFactory
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.name.as("memberName"),
				member.age,
				team.id.as("teamId"),
				team.name.as("teamName")
			))
			.from(member)
			.where(
				memberNameEquals(condition.getMemberName()),
				teamNameEquals(condition.getTeamName()),
				memberAgeGoe(condition.getAgeGoe()),
				memberAgeLoe(condition.getAgeLoe())
			)
			.leftJoin(member.team, team)
			.fetch();
	}
	
	private BooleanExpression memberNameEquals(@Nullable String memberName) {
		return hasText(memberName) ? member.name.eq(memberName) : null;
	}
	
	private BooleanExpression teamNameEquals(@Nullable String teamName) {
		return hasText(teamName)? team.name.eq(teamName) : null;
	}
	
	private BooleanExpression memberAgeGoe(@Nullable Integer goe) {
		return goe != null ? member.age.goe(goe): null;
	}
	
	private BooleanExpression memberAgeLoe(@Nullable Integer loe) {
		return loe != null ? member.age.loe(loe): null;
	}
	
}
