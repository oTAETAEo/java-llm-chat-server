package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    public static MemberEntity from(Member member) {
        return new MemberEntity(
                member.getId(),
                member.getNickname()
        );
    }

    public static MemberEntity reference(Member member) {

        if (member.getId() == null) {
            throw new IllegalArgumentException("회원 ID는 운동 기록 연관관계 저장에 필수 값입니다.");
        }

        return new MemberEntity(
                member.getId(),
                member.getNickname()
        );
    }

    public Member toDomain() {
        return Member.of(id, nickname);
    }
}
