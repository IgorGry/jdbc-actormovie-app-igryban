package com.bobocode.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Builder
public class Actor {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
}
