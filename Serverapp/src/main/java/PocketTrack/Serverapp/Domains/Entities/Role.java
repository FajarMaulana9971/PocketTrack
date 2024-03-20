package PocketTrack.Serverapp.Domains.Entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tb_m_roles")
@Entity
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity {

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<AccountRole> accountRoles;

}
