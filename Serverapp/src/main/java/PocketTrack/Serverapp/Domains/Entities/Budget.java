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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @Column(length = 20)
    private String title;

    @Column(length = 50)
    private String description;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Income> income;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Outcome> outcome;

    // @ManyToMany
    // @JoinTable(name = "tb_tr_user_budget", joinColumns = @JoinColumn(name =
    // "budget_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // private List<User> users;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Budget> budget;
}