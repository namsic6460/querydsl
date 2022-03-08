package namsic.example.querydsl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString(of = {"id", "name"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	@Builder.Default
	@OneToMany(mappedBy = "team")
	List<Member> members = new ArrayList<>();
	
}
