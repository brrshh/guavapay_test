package by.guavapay.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "parcel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parcel_seq")
    @SequenceGenerator(name = "parcel_seq", sequenceName = "parcel_seq", allocationSize = 1)
    Long id;

    @Column
    String address;

    @ManyToOne
    @JoinColumn(name = "created_id", nullable = false)
    User created;


    @ManyToOne
    @JoinColumn(name = "courier_id")
    User courier;

    @Enumerated(EnumType.STRING)
    Delivery status;

    @Column(name = "created_at")
    OffsetDateTime createdAt;
}