package PocketTrack.Serverapp.Domains.Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
}
