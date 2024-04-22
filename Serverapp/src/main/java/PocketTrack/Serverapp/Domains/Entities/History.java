package PocketTrack.Serverapp.Domains.Entities;

import java.time.LocalDateTime;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "tb_tr_histories")
@Entity
public class History extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 500)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
}
