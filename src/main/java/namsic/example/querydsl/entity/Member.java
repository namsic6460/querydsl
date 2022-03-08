package namsic.example.querydsl.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString(of = {"id", "name", "age"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	
	@Id
	@GeneratedValue
	@Column
	private Long id;
	
	private String name;
	
	private int age;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
	
	public Member(String name, int age, Team team) {
		this.setName(name);
		this.setAge(age);
		this.setTeam(team);
	}
	
	public void setTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
	
}
