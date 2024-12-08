package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "modification", schema = "carbase")
public class Modification {
    @Id
    @Column(name = "complectation_id")
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
    private Configuration configuration;

    // Связь 1:1 с Specifications и Options
    @OneToOne(mappedBy = "modification", fetch = FetchType.LAZY)
    private Specifications specifications;

    @OneToOne(mappedBy = "modification", fetch = FetchType.LAZY)
    private Options options;
}
