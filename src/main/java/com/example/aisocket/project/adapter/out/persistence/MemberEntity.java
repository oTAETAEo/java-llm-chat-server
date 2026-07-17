package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberEntity {

    @Id
    private Long id;

    private String nickname;

    public static MemberEntity from(Member member) {
        return new MemberEntity(
                member.getId(),
                member.getNickname()
        );
    }

    public Member toDomain() {
        return Member.of(id, nickname);
    }
}
