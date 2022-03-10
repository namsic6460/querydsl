package namsic.example.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class MemberDto {
	
	private String name;
	private int age;
	
	@QueryProjection
	public MemberDto(String name, int age) {
		this.setName(name);
		this.setAge(age);
	}
	
}
