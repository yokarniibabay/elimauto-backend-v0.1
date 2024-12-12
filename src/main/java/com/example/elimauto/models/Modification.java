package com.example.elimauto.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "modification", catalog = "carbase")
public class Modification {
    @Id
    @Column(name = "complectation-id")
    private String complectationId;

    @Column(name = "offers-price-from")
    private Integer offersPriceFrom;

    @Column(name = "offers-price-to")
    private Integer offersPriceTo;

    @Column(name = "group-name")
    private String groupName;

    @Column(name = "configuration_id")
    private String configurationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="configuration_id", referencedColumnName="id", insertable=false, updatable=false)
    @JsonBackReference
    private Configuration configuration;

    // Связь 1:1 с Specifications и Options
    @OneToOne(mappedBy = "modification", fetch = FetchType.LAZY)
    @JsonIgnore
    private Specifications specifications;

    @OneToOne(mappedBy = "modification", fetch = FetchType.LAZY)
    @JsonIgnore
    private Options options;
}
