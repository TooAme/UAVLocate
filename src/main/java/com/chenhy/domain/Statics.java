package com.chenhy.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Statics.
 */
@Entity
@Table(name = "statics")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Statics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time")
    private Instant time;

    @Column(name = "pos_x")
    private Long posX;

    @Column(name = "pos_y")
    private Long posY;

    @Column(name = "pos_z")
    private Long posZ;

    @Column(name = "wind_speed")
    private Long windSpeed;

    @Column(name = "wind_direction")
    private String windDirection;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Statics id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTime() {
        return this.time;
    }

    public Statics time(Instant time) {
        this.setTime(time);
        return this;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Long getPosX() {
        return this.posX;
    }

    public Statics posX(Long posX) {
        this.setPosX(posX);
        return this;
    }

    public void setPosX(Long posX) {
        this.posX = posX;
    }

    public Long getPosY() {
        return this.posY;
    }

    public Statics posY(Long posY) {
        this.setPosY(posY);
        return this;
    }

    public void setPosY(Long posY) {
        this.posY = posY;
    }

    public Long getPosZ() {
        return this.posZ;
    }

    public Statics posZ(Long posZ) {
        this.setPosZ(posZ);
        return this;
    }

    public void setPosZ(Long posZ) {
        this.posZ = posZ;
    }

    public Long getWindSpeed() {
        return this.windSpeed;
    }

    public Statics windSpeed(Long windSpeed) {
        this.setWindSpeed(windSpeed);
        return this;
    }

    public void setWindSpeed(Long windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return this.windDirection;
    }

    public Statics windDirection(String windDirection) {
        this.setWindDirection(windDirection);
        return this;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Statics)) {
            return false;
        }
        return getId() != null && getId().equals(((Statics) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Statics{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            ", posX=" + getPosX() +
            ", posY=" + getPosY() +
            ", posZ=" + getPosZ() +
            ", windSpeed=" + getWindSpeed() +
            ", windDirection='" + getWindDirection() + "'" +
            "}";
    }
}
