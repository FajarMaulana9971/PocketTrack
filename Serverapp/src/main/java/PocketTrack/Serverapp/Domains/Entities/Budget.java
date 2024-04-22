package PocketTrack.Serverapp.Domains.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_m_Budget")
@EqualsAndHashCode(callSuper = false)
public class Budget extends BaseEntity {

    @Column
    private LocalDateTime date;

    @Column(name = "total_balance")
    private BigDecimal totalBalance;

    @Column(length = 50)
    private String title;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Income> income;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Outcome> outcome;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<User> user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<History> histories;
}