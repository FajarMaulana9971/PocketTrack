package PocketTrack.Serverapp.Domains.Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
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
public class Histories extends BaseEntity {

    private LocalDateTime date;

    private LocalDate recordDate;
}
