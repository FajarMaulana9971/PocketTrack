package PocketTrack.Serverapp.Domains.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "tb_m_outcome")
public class Outcome extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, name = "approval_status")
    @Comment("this atributte for approval status")
    private Boolean status;

    @Column(nullable = false, name = "deleted_status")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    @Comment("foreign key table budget")
    private Budget budget;

    @OneToMany(mappedBy = "outcome", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<History> histories;
}
