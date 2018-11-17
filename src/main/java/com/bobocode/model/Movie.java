package com.bobocode.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "releaseDate"})
@ToString
@Builder
public class Movie {
    private Long id;
    private String name;
    private Long duration;
    private LocalDate releaseDate;
}
