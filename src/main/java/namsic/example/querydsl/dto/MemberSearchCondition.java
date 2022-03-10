package namsic.example.querydsl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSearchCondition {
	
	private String memberName;
	private String teamName;
	private Integer ageGoe;
	private Integer ageLoe;
	
}
