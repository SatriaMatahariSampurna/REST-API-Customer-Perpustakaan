package com.example.dto;

import java.math.BigDecimal;

public record CustomerData(
        Long id,
        String name,
        BigDecimal balance
) {}
