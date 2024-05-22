package io.avania.io.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/***
 *
 * @version 2.0
 */
@MappedSuperclass
@Getter
@Setter
public class BaseEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Nairobi")
    public Date createdOn;
    @JsonIgnore
    @Column(name = "soft_delete", columnDefinition = "char(1) default 0")
    private boolean softDelete;

    @PrePersist
    public void addData() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        ZoneId zoneId = ZoneId.of("Africa/Lusaka");
        ZonedDateTime lusaka = zonedDateTime.withZoneSameInstant(zoneId);
        this.createdOn = Date.from (lusaka.toInstant ());
        this.softDelete = false;
    }
}
