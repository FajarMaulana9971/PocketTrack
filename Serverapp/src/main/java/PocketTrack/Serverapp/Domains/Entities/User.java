package PocketTrack.Serverapp.Domains.Entities;

import java.time.LocalDate;
import java.time.ZoneId;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import PocketTrack.Serverapp.Domains.Enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "tb_m_users", indexes = @Index(name = "idx_email", columnList = "email", unique = true))
public class User extends BaseEntity {

    private String name;

    private String email;

    private LocalDate birthDate;

    private LocalDate joinDate = LocalDate.now(ZoneId.of("Asia/Jakarta"));

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(mappedBy = "user")
    @PrimaryKeyJoinColumn
    private Account account;
}
