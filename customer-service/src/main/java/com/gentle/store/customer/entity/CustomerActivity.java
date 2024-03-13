package com.gentle.store.customer.entity;

import com.gentle.store.customer.entity.enums.ActivityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.gentle.store.customer.util.Constants.CONTENT_MAX_LENGTH;
import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "customer_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerActivity {
    @Id
    @GeneratedValue
    private UUID id;

    /**Art der Kundenaktivität, z.B. Anfrage, Beschwerde, Beratung usw*/
    @NotNull(message = "Die Art der Kundenaktivität darf nicht null sein")
    @Enumerated(STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @CreationTimestamp
    private LocalDateTime timestamp;

    /**Inhalt der Aktivität, z.B. detaillierte Informationen oder Beschreibung*/
    @Column(length = CONTENT_MAX_LENGTH)
    private String content;
}
