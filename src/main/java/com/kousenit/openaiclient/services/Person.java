package com.kousenit.openaiclient.services;

import java.time.LocalDate;

public record Person(String firstName, String lastName, LocalDate dob) {
}
