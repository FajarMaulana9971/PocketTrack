package PocketTrack.Serverapp.Domains.Entities;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_tr_Account_Roles")
@EqualsAndHashCode(callSuper = false)
public class AccountRole extends BaseEntity {

    @ManyToOne
    private Account account;

    @ManyToOne
    private Role role;
}
